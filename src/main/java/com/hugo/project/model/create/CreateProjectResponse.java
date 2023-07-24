package com.hugo.project.model.create;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateProjectResponse {
    private final static String ID_FIELD = "id";
    private final static String CODE_FIELD = "code";

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(ID_FIELD)
    private String id;
    @JsonProperty(CODE_FIELD)
    private CreateProjectResponseEnum createProjectResponseEnum;

    public CreateProjectResponse() {
    }

    public CreateProjectResponse(String id, CreateProjectResponseEnum createProjectResponseEnum) {
        this.id = id;
        this.createProjectResponseEnum = createProjectResponseEnum;
    }
}
