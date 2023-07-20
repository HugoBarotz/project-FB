package com.hugo.project.controller;

import com.hugo.project.model.create.CreateProjectResponse;
import com.hugo.project.model.Project;
import com.hugo.project.model.delete.DeleteBulkProjectResponse;
import com.hugo.project.model.search.SearchProjectResponse;
import com.hugo.project.model.update.UpdateProjectResponse;
import com.hugo.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@RestController
@ComponentScan(basePackages = "com.hugo.project")
@RequestMapping("/apis/v1/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping()
    public Iterable<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @CrossOrigin(origins = "http://localhost:63342")
    @PostMapping("/_create")
    public CreateProjectResponse createProject(@RequestBody Project project) {
        return projectService.createProject(project);
    }
    @CrossOrigin(origins = "http://localhost:63342")
    @DeleteMapping("/_delete_bulk")
    public DeleteBulkProjectResponse deleteBulkProject(@RequestBody List<String> ids){
        return projectService.deleteBulkProject(ids);
    }
    @CrossOrigin(origins = "http://localhost:63342")
    @PutMapping("/_update")
    public UpdateProjectResponse updateProject(@RequestBody Project project){
        return projectService.updateProject(project);
    }
    @CrossOrigin(origins = "http://localhost:63342")
    @PostMapping("/_search")
    public SearchProjectResponse searchProject(@RequestBody List<String> filter){
        return projectService.searchProject(filter);
    }
}
