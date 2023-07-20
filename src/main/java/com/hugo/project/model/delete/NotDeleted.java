package com.hugo.project.model.delete;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotDeleted {
    private final static String ID_FIELD = "id";
    private final static String CODE_FIELD = "code";


    @JsonProperty(ID_FIELD)
    private String id;
    @JsonProperty(CODE_FIELD)
    private NotDeletedCode code;

    public NotDeleted() {
    }

    public NotDeleted(@JsonProperty(ID_FIELD) String id,
                      @JsonProperty(CODE_FIELD) NotDeletedCode code) {
        this.id = id;
        this.code = code;
    }
}
