package com.hugo.project.model.update;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateProjectResponse {
    private final static String CODE_FIELD = "code";

    @JsonProperty(CODE_FIELD)
    private UpdateProjectResponseEnum code;

    public UpdateProjectResponse(UpdateProjectResponseEnum code) {
        this.code = code;
    }
}
