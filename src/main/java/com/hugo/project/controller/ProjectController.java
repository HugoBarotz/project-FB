package com.hugo.project.controller;

import com.hugo.project.model.create.CreateProjectResponse;
import com.hugo.project.model.Project;
import com.hugo.project.model.delete.DeleteBulkProjectResponse;
import com.hugo.project.model.search.SearchProjectResponse;
import com.hugo.project.model.update.UpdateProjectResponse;
import com.hugo.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Operation(summary = "Create a project")
    @CrossOrigin(origins = {"https://myportfolio-eb126.web.app/", "http://localhost:63342"})
    @PostMapping("/_create")
    public CreateProjectResponse createProject(@RequestBody Project project) {
        return projectService.createProject(project);
    }

    @Operation(summary = "Delete multiple projects")
    @CrossOrigin(origins = {"https://myportfolio-eb126.web.app/", "http://localhost:63342"})
    @DeleteMapping("/_delete_bulk")
    public DeleteBulkProjectResponse deleteBulkProject(@RequestBody List<String> ids){
        return projectService.deleteBulkProject(ids);
    }

    @Operation(summary = "Update a project")
    @CrossOrigin(origins = {"https://myportfolio-eb126.web.app/", "http://localhost:63342"})
    @PutMapping("/_update")
    public UpdateProjectResponse updateProject(@RequestBody Project project){
        return projectService.updateProject(project);
    }

    @Operation(summary = "Search projects based on their name or skills")
    @CrossOrigin(origins = {"https://myportfolio-eb126.web.app/", "http://localhost:63342"})
    @PostMapping("/_search")
    public SearchProjectResponse searchProject(@RequestBody List<String> filter){
        return projectService.searchProject(filter);
    }
}
