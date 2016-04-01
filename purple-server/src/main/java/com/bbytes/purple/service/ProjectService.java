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
		return projectRepository.findOneByProjectName(name);
	}

	public List<Project> findProjectByUser(User user) {
		return projectRepository.findByUser(user);
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
		if (!projectId.equals("null") && users != null && !users.isEmpty()) {
			if (!projectIdExist(projectId))
				throw new PurpleException("Error while adding users in project", ErrorHandler.ADD_USER_FAILED);
			try {
				Project getproject = findByProjectId(projectId);
				getproject.setUser(users);
				project = projectRepository.save(getproject);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.ADD_USER_FAILED);
			}
		} else if (users.isEmpty() && users != null)
			throw new PurpleException("Can not add null users", ErrorHandler.ADD_USER_FAILED);
		return project;
	}

	public Project deleteUsers(String projectId, List<String> toBeRemoved) throws PurpleException {

		Project project = null;
		if (!projectId.equals("null") && toBeRemoved != null) {
			if (!projectIdExist(projectId))
				throw new PurpleException("Error while deleting users from project", ErrorHandler.DELETE_USER_FAILED);
		}
		try {
			Project userProject = findByProjectId(projectId);
			List<User> existUsers = userProject.getUser();
			List<User> temp = new ArrayList<User>();
			for (User user : existUsers) {
				if (toBeRemoved.contains(user.getEmail()))
					temp.add(user);
			}
			existUsers.removeAll(temp);
			userProject.setUser(existUsers);
			project = projectRepository.save(userProject);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_STATUS_FAILED);
		}

		return project;
	}

	public List<User> getAllUsers(String projectId) throws PurpleException {

		List<User> users = new ArrayList<User>();
		if (!projectId.equals("null")) {
			try {
				users = findByProjectId(projectId).getUser();
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.GET_USER_FAILED);
			}
		} else
			throw new PurpleException("Error while getting users", ErrorHandler.GET_USER_FAILED);
		return users;
	}
}