package com.hugo.project.model.delete;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeleteBulkProjectResponse {
    private final static String CODE_FIELD = "code";
    private final static String DELETED_FIELD = "deleted";
    private final static String NOT_DELETED_FIELD = "not_deleted";

    @JsonProperty(CODE_FIELD)
    private DeleteBulkProjectResponseEnum deleteBulkProjectResponseEnum;
    @JsonProperty(DELETED_FIELD)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> deletedIds;
    @JsonProperty(NOT_DELETED_FIELD)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<NotDeleted> notDeletedProjects;

    public DeleteBulkProjectResponse(DeleteBulkProjectResponseEnum deleteBulkProjectResponseEnum, List<String> deletedIds, List<NotDeleted> notDeletedProjects) {
        this.deleteBulkProjectResponseEnum = deleteBulkProjectResponseEnum;
        this.deletedIds = deletedIds;
        this.notDeletedProjects = notDeletedProjects;
    }

    public DeleteBulkProjectResponse(DeleteBulkProjectResponseEnum deleteBulkProjectResponseEnum) {
        this.deleteBulkProjectResponseEnum = deleteBulkProjectResponseEnum;
    }
}
