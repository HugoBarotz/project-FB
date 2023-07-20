package com.hugo.project.model.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hugo.project.model.Project;
import lombok.Data;

import java.util.List;

@Data
public class SearchProjectResponse {
    private final static String CODE_FIELD = "code";
    private final static String TOTAL_PROJECTS_FIELD = "totalProjects";
    private final static String PROJECTS_FIELD = "projects";

    @JsonProperty(CODE_FIELD)
    private SearchProjectResponseEnum searchProjectResponseEnum;

    @JsonProperty(TOTAL_PROJECTS_FIELD)
    private Integer totalProjects;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(PROJECTS_FIELD)
    private List<Project> projects;

    public SearchProjectResponse(SearchProjectResponseEnum searchProjectResponseEnum, Integer totalProjects, List<Project> projects) {
        this.searchProjectResponseEnum = searchProjectResponseEnum;
        this.totalProjects = totalProjects;
        this.projects = projects;
    }
}
