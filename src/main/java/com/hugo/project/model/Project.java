package com.hugo.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;


import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(indexName = "project")
public class Project {

    @Id
    private String id;

    private String name;

    private List<String> skills;

    private String description;

    private String githubLink;

    public Project(String id) {
        this.id = id;
    }

}
