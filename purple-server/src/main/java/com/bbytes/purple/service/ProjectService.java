package com.bbytes.purple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.repository.ProjectRepository;

@Service
public class ProjectService extends AbstractService<Project, String> {

	private ProjectRepository projectRepository;

	@Autowired
	public ProjectService(ProjectRepository projectRepository) {
		super(projectRepository);
		this.projectRepository = projectRepository;
	}

	public Project findByProjectId(String projectId) {
		return projectRepository.findOne(projectId);
	}

	public Project findByProjectName(String name) {
		return projectRepository.findOne(name);
	}

	public boolean projectNameExist(String name) {
		boolean state = projectRepository.findOneByProjectName(name) == null ? false : true;
		return state;
	}

	public boolean projectIdExist(String projectId) {
		boolean state = projectRepository.findOne(projectId) == null ? false : true;
		return state;
	}
}