package com.bbytes.purple.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.utils.ErrorHandler;

@Service
public class AdminService {

	@Autowired
	private UserService userService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private TenantResolverService tenantResolverService;

	public User addUsers(User user) throws PurpleException {

		if (user != null) {
			if (userService.userEmailExist(user.getEmail()) || tenantResolverService.emailExist(user.getEmail()))
				throw new PurpleException("Error while adding users", ErrorHandler.USER_NOT_FOUND);
			try {
				userService.save(user);
				user = userService.getUserByEmail(user.getEmail());
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.ADD_USER_FAILED);
			}
		}
		return user;
	}

	public void deleteUser(String email) throws PurpleException {

		if (email != null && !email.isEmpty()) {
			if (userService.userEmailExist(email) || tenantResolverService.emailExist(email))
				throw new PurpleException("Error while deleting user", ErrorHandler.USER_NOT_FOUND);
			try {
				User user = userService.getUserByEmail(email);
				userService.delete(user);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.DELETE_USER_FAILED);
			}
		} else
			throw new PurpleException("Can not delete empty user", ErrorHandler.USER_NOT_FOUND);
	}

	public List<User> getAllUsers() throws PurpleException {

		List<User> users = new ArrayList<User>();
		try {
			users = userService.findAll();
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_USER_FAILED);
		}

		return users;
	}

	public Project createProject(Project project) throws PurpleException {

		if (project != null) {
			if (projectService.projectNameExist(project.getProjectName()))
				throw new PurpleException("Error while adding project", ErrorHandler.PROJECT_NOT_FOUND);
			try {
				projectService.save(project);
				project = projectService.findByProjectId(project.getProjectId());
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.ADD_PROJECT_FAILED);
			}
		}
		return project;
	}

	public void deleteProject(String projectId) throws PurpleException {

		if (projectId != null && !projectId.isEmpty()) {
			if (projectService.projectIdExist(projectId))
				throw new PurpleException("Error while deleting project", ErrorHandler.PROJECT_NOT_FOUND);
			try {
				Project project = projectService.findByProjectId(projectId);
				projectService.delete(project);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.DELETE_PROJECT_FAILED);
			}
		} else
			throw new PurpleException("Can not delete empty project", ErrorHandler.PROJECT_NOT_FOUND);
	}

	public List<Project> getAllProjects() throws PurpleException {

		List<Project> Projects = new ArrayList<Project>();
		try {
			Projects = projectService.findAll();
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_USER_FAILED);
		}

		return Projects;
	}

	public Project updateProject(String projectId, Project project) throws PurpleException {

		Project updatedProject = null;
		if (projectId != null && !projectId.isEmpty() && project != null) {
			if (projectService.projectIdExist(projectId) || projectService.projectNameExist(project.getProjectName()))
				throw new PurpleException("Error while adding project", ErrorHandler.PROJECT_NOT_FOUND);
			try {
				Project updateProject = projectService.findByProjectId(projectId);
				updateProject.setProjectName(project.getProjectName());
				updateProject.setTimePreference(project.getTimePreference());
				updateProject.setUser(project.getUser());
				updatedProject = projectService.save(updateProject);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.UPDATE_PROJECT_FAILED);
			}
		}
		return updatedProject;
	}
}
