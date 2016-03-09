package com.bbytes.purple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.repository.ProjectRepository;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projectRepository;

	public Project findByProjectId(String projectId) {
		return projectRepository.findOne(projectId);
	}

	public Project findByProjectName(String name) {
		return projectRepository.findOne(name);
	}
}