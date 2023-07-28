package com.hugo.project.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.hugo.project.exception.EmptyIdException;
import com.hugo.project.exception.EmptyNameException;
import com.hugo.project.exception.NameAlreadyTakenException;
import com.hugo.project.exception.NotFoundException;
import com.hugo.project.model.Project;
import com.hugo.project.model.create.CreateProjectResponse;
import com.hugo.project.model.create.CreateProjectResponseEnum;
import com.hugo.project.model.delete.DeleteBulkProjectResponse;
import com.hugo.project.model.delete.DeleteBulkProjectResponseEnum;
import com.hugo.project.model.delete.NotDeleted;
import com.hugo.project.model.delete.NotDeletedCode;
import com.hugo.project.model.search.SearchProjectResponse;
import com.hugo.project.model.search.SearchProjectResponseEnum;
import com.hugo.project.model.update.UpdateProjectResponse;
import com.hugo.project.model.update.UpdateProjectResponseEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ProjectService {
    private static final String COLLECTION_NAME = "project";

    private List<Project> getAllProjects() throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        List<Project> projects = new ArrayList<>();
        CollectionReference collectionRef = firestore.collection(COLLECTION_NAME);
        ApiFuture<QuerySnapshot> future = collectionRef.get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        documents.forEach(doc -> {
            Project project = doc.toObject(Project.class);
            project.setId(doc.getId());
            projects.add(project);
        });

        return projects;
    }

    public CreateProjectResponse createProject(Project project) {
        Firestore firestoreDb = FirestoreClient.getFirestore();
        try {
            if (project.getName() == null || project.getName().isBlank()) {
                throw new IllegalArgumentException("Name can't be empty");
            }
            ApiFuture<DocumentReference> result = firestoreDb.collection(COLLECTION_NAME).add(project);
            return new CreateProjectResponse(result.get().getId(), CreateProjectResponseEnum.CREATED);
        } catch (IllegalArgumentException e) {
            return new CreateProjectResponse(null, CreateProjectResponseEnum.EMPTY_NAME);
        } catch (Exception e) {
            return new CreateProjectResponse(null, CreateProjectResponseEnum.UNEXPECTED_ERROR);
        }
    }

    public DeleteBulkProjectResponse deleteBulkProject(List<String> ids) {
        Firestore firestoreDb = FirestoreClient.getFirestore();
        try {
            if (ids == null || ids.isEmpty()) {
                throw new IllegalArgumentException("Ids can't be empty");
            }
            List<String> deletedIds = new ArrayList<>();
            List<NotDeleted> notDeletedIds = new ArrayList<>();
            ids.forEach(
                    id -> {
                        DocumentReference docRef = firestoreDb.collection(COLLECTION_NAME).document(id);
                        ApiFuture<DocumentSnapshot> future = docRef.get();

                        try {
                            DocumentSnapshot document = future.get();
                            if (!document.exists()) {
                                notDeletedIds.add(new NotDeleted(id, NotDeletedCode.NOT_FOUND));
                            } else {
                                ApiFuture<WriteResult> deleteFuture = docRef.delete();
                                deleteFuture.get(); // wait for doc deletion
                                deletedIds.add(id);
                            }
                        } catch (ExecutionException | InterruptedException e) {
                            notDeletedIds.add(new NotDeleted(id, NotDeletedCode.NOT_FOUND));
                        }
                    }
            );

            if (deletedIds.isEmpty()) {
                return new DeleteBulkProjectResponse(DeleteBulkProjectResponseEnum.NONE_DELETED, null, notDeletedIds);
            }
            if (notDeletedIds.isEmpty()) {
                return new DeleteBulkProjectResponse(DeleteBulkProjectResponseEnum.ALL_DELETED, deletedIds, null);
            }
            return new DeleteBulkProjectResponse(DeleteBulkProjectResponseEnum.PARTIALLY_DELETED, deletedIds, notDeletedIds);
        } catch (IllegalArgumentException e) {
            return new DeleteBulkProjectResponse(DeleteBulkProjectResponseEnum.EMPTY_IDS);
        } catch (Exception e) {
            return new DeleteBulkProjectResponse(DeleteBulkProjectResponseEnum.UNEXPECTED_ERROR);
        }
    }

    public UpdateProjectResponse updateProject(Project updatedProject) {
        Firestore firestoreDb = FirestoreClient.getFirestore();
        try {
            if (updatedProject.getId() == null || updatedProject.getId().isBlank()) {
                throw new EmptyIdException("Id can't be empty");
            }
            if (updatedProject.getName() == null || updatedProject.getName().isBlank()) {
                throw new EmptyNameException("Name can't be empty");
            }

            List<Project> existingProjects = getAllProjects();

            // Look for the project with the same id
            Project correspondingProject = existingProjects.stream()
                    .filter(existingProject -> existingProject.getId().equals(updatedProject.getId()))
                    .findFirst()
                    .orElse(null);
            if (correspondingProject == null) {
                throw new NotFoundException("Project not found");
            }
            // Check for each other project if the name is already taken
            if (existingProjects.stream()
                    .filter(existingProject -> !existingProject.getId().equals(updatedProject.getId())) // ignore the project corresponding to the updated one
                    .anyMatch(existingProject -> existingProject.getName() != null && existingProject.getName().equals(updatedProject.getName()))) {
                throw new NameAlreadyTakenException("Name is already taken by another project");
            }

            DocumentReference docRef = firestoreDb.collection(COLLECTION_NAME).document(correspondingProject.getId());
            ApiFuture<WriteResult> future = docRef.update(
                    "name", updatedProject.getName(),
                    "description", updatedProject.getDescription(),
                    "skills", updatedProject.getSkills(),
                    "githubLink", updatedProject.getGithubLink(),
                    "applicationType", updatedProject.getApplicationType(),
                    "image", updatedProject.getImage()
            );

            future.get(); // wait doc update
            return new UpdateProjectResponse(UpdateProjectResponseEnum.UPDATED);
        } catch (EmptyIdException e) {
            return new UpdateProjectResponse(UpdateProjectResponseEnum.EMPTY_ID);
        } catch (EmptyNameException e) {
            return new UpdateProjectResponse(UpdateProjectResponseEnum.EMPTY_NAME);
        } catch (NotFoundException e) {
            return new UpdateProjectResponse(UpdateProjectResponseEnum.NOT_FOUND);
        } catch (NameAlreadyTakenException e) {
            return new UpdateProjectResponse(UpdateProjectResponseEnum.NAME_ALREADY_TAKEN);
        } catch (Exception e) {
            return new UpdateProjectResponse(UpdateProjectResponseEnum.UNEXPECTED_ERROR);
        }
    }

    /*
     * Search for projects for which at least one filter String is contained in either the project's name, or one of its skills (ignoring letter case)
     */
    public SearchProjectResponse searchProject(List<String> filter) {
        try {
            if (filter != null) {
                filter.removeAll(Arrays.asList("", null));
            }

            List<Project> projectsList = getAllProjects();

            if (filter == null || filter.isEmpty()) {
                return new SearchProjectResponse(SearchProjectResponseEnum.FOUND, projectsList.size(), projectsList);
            }

            // this is where we filter, and sort it by ASC name
            List<Project> projectsToReturn = projectsList.stream()
                    .filter(project -> filter.stream()
                            .anyMatch(constraint -> isConstraintMatched(project, constraint)))
                    .sorted(Comparator.comparing(Project::getName))
                    .toList();

            if (projectsToReturn.isEmpty()) {
                return new SearchProjectResponse(SearchProjectResponseEnum.NONE_FOUND, 0, null);
            }
            return new SearchProjectResponse(SearchProjectResponseEnum.FOUND, projectsToReturn.size(), projectsToReturn);
        } catch (ExecutionException | InterruptedException e) {
            return new SearchProjectResponse(SearchProjectResponseEnum.UNEXPECTED_ERROR, 0, null);
        }
    }

    public Boolean isConstraintMatched(Project project, String constraint) {
        if (project.getName().toLowerCase().contains(constraint.toLowerCase())) {
            return true;
        }

        List<String> skills = project.getSkills();
        if (skills != null && !skills.isEmpty()) {
            return skills.stream()
                    .map(String::toLowerCase)
                    .anyMatch(constraint.toLowerCase()::contains);
        }

        return false;
    }
}
