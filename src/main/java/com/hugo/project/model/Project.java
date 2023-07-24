package com.hugo.project.model;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Project {

    private String id;

    private String name;

    private List<String> skills;

    private String description;

    private String githubLink;

    public Project(String id) {
        this.id = id;
    }

}
