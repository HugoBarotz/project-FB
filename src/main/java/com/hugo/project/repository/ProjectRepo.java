package com.hugo.project.repository;

import com.hugo.project.model.Project;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepo extends ElasticsearchRepository<Project, String> {
}
