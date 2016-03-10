package com.bbytes.purple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.repository.ProjectRepository;

@Service
public class ProjectService extends AbstractService<Project, String>{


	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	public ProjectService(ProjectRepository ProjectRepository) {
		super(ProjectRepository);
	}

	public Project findByProjectId(String projectId) {
		return projectRepository.findOne(projectId);
	}

	public Project findByProjectName(String name) {
		return projectRepository.findOne(name);
	}
}