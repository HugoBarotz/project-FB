package com.hugo.project.service;

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
import com.hugo.project.repository.ProjectRepo;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service
@Validated
@ComponentScan(basePackages = "com.hugo.project")
public class ProjectService {

    @Autowired
    private ProjectRepo projectRepo;

    public Iterable<Project> getAllProjects() {
        return projectRepo.findAll();
    }

    public CreateProjectResponse createProject(Project project) {
        try {
            if (project.getName() == null || project.getName().isBlank()) {
                throw new IllegalArgumentException("Name can't be empty");
            }
            Project savedProject = projectRepo.save(project);
            return new CreateProjectResponse(savedProject.getId(), CreateProjectResponseEnum.CREATED);
        } catch (IllegalArgumentException e) {
            return new CreateProjectResponse(null, CreateProjectResponseEnum.EMPTY_NAME);
        } catch (Exception e) {
            return new CreateProjectResponse(null, CreateProjectResponseEnum.UNEXPECTED_ERROR);
        }
    }

    // Transactional so we don't delete anything if one of the deletion goes wrong (the transaction doesn't go through)
    // Only did a bulk API to train myself, but this will probably be used only for 1 deletion at a time
    @Transactional
    public DeleteBulkProjectResponse deleteBulkProject(List<String> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                throw new IllegalArgumentException("Ids can't be empty");
            }
            List<String> deletedIds = new ArrayList<>();
            List<NotDeleted> notDeletedIds = new ArrayList<>();
            for (String id : ids) {
                if (projectRepo.findById(id).isEmpty()) {
                    notDeletedIds.add(new NotDeleted(id, NotDeletedCode.NOT_FOUND));
                } else {
                    projectRepo.deleteById(id);
                    deletedIds.add(id);
                }
            }
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
        try {
            if (updatedProject.getId() == null || updatedProject.getId().isBlank()) {
                throw new EmptyIdException("Id can't be empty");
            }
            if (updatedProject.getName() == null || updatedProject.getName().isBlank()) {
                throw new EmptyNameException("Name can't be empty");
            }
            Iterable<Project> existingProjects = projectRepo.findAll();

            // Look for the project with the same id
            Project correspondingProject = StreamSupport.stream(existingProjects.spliterator(), false)
                    .filter(existingProject -> existingProject.getId().equals(updatedProject.getId()))
                    .findFirst()
                    .orElse(null);
            if (correspondingProject == null) {
                throw new NotFoundException("Project not found");
            }
            // Check for each other project if the name is already taken
            if (StreamSupport.stream(existingProjects.spliterator(), false)
                    .filter(existingProject -> !existingProject.getId().equals(updatedProject.getId())) // ignore the project corresponding to the updated one
                    .anyMatch(existingProject -> existingProject.getName() != null && existingProject.getName().equals(updatedProject.getName()))) {
                throw new NameAlreadyTakenException("Name is already taken by another project");
            }

            projectRepo.save(updatedProject);
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
     * Search for projects for which each filter String is contained in either the project's name, or one of its skills
     */
    public SearchProjectResponse searchProject(List<String> filter) {
        try {
            if(filter != null){
                filter.removeAll(Arrays.asList("", null));
            }

            List<Project> projectsList = new ArrayList<>();
            projectRepo.findAll().forEach(projectsList::add);

            if (filter == null || filter.isEmpty()) {
                return new SearchProjectResponse(SearchProjectResponseEnum.FOUND, projectsList.size(), projectsList);
            }

            // this is where we filter, and sort it from oldest to most recent creation date
            List<Project> projectsToReturn = projectsList.stream()
                    .filter(project -> filter.stream()
                            .allMatch(constraint -> isConstraintMatched(project, constraint)))
                    .sorted(Comparator.comparing(Project::getName))
                    .collect(Collectors.toList());

            if (projectsToReturn.isEmpty()) {
                return new SearchProjectResponse(SearchProjectResponseEnum.NONE_FOUND, 0, null);
            }
            return new SearchProjectResponse(SearchProjectResponseEnum.FOUND, projectsToReturn.size(), projectsToReturn);
        } catch (Exception e) {
            return new SearchProjectResponse(SearchProjectResponseEnum.UNEXPECTED_ERROR, 0, null);
        }
    }

    public Boolean isConstraintMatched(Project project, String constraint) {
        if (project.getName().contains(constraint)) {
            return true;
        }

        List<String> skills = project.getSkills();
        if (skills != null && !skills.isEmpty()) {
            return skills.stream().anyMatch(constraint::contains);
        }

        return false;
    }
}
