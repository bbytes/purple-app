package com.bbytes.purple.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.repository.ProjectRepository;
import com.bbytes.purple.utils.ErrorHandler;

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

	public Project addUsers(String projectId, List<User> users) throws PurpleException {

		Project project = null;
		if (projectId != null && !projectId.isEmpty() && users != null && !users.isEmpty()) {
			if (projectIdExist(projectId))
				throw new PurpleException("Error while adding users in project", ErrorHandler.ADD_USER_FAILED);
			try {
				Project getproject = findByProjectId(projectId);
				getproject.setUser(users);
				project = projectRepository.save(getproject);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.GET_STATUS_FAILED);
			}
		}
		return project;
	}

	public Project deleteUsers(String projectId, List<User> users) throws PurpleException {

		Project project = null;
		if (projectId != null && !projectId.isEmpty() && users != null && !users.isEmpty()) {
			if (projectIdExist(projectId))
				throw new PurpleException("Error while deleting users from project", ErrorHandler.DELETE_USER_FAILED);
			for (int i = 0; i < users.size(); i++) {
				if (users.get(i).getUserId() != findByProjectId(projectId).getUser().get(i).getUserId())
					throw new PurpleException("Error while deleting users from project",
							ErrorHandler.DELETE_USER_FAILED);
			}
			try {
				Project usersProject = findByProjectId(projectId);
				List<User> temp = usersProject.getUser();
				List<User> toBeRemoved = new ArrayList<User>();
				for (User i : users) {
					for (User j : users) {
						if (i.getUserId().toString().equals(j.getUserId()))
							toBeRemoved.add(i);
					}
					temp.removeAll(toBeRemoved);
				}
				usersProject.setUser(temp);
				project = projectRepository.save(usersProject);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.GET_STATUS_FAILED);
			}
		}
		return project;
	}
}