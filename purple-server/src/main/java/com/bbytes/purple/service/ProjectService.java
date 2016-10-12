package com.bbytes.purple.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.repository.ProjectRepository;
import com.bbytes.purple.utils.ErrorHandler;

@Service
public class ProjectService extends AbstractService<Project, String> {

	private ProjectRepository projectRepository;

	@Autowired
	private UserService userService;

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

	public List<Project> findProjectByProjectOwner(User user) {
		return projectRepository.findByProjectOwner(user);
	}

	public boolean projectNameExist(String name) {
		boolean state = projectRepository.findOneByProjectName(name) == null ? false : true;
		return state;
	}

	public boolean projectIdExist(String projectId) {
		boolean state = projectRepository.findOne(projectId) == null ? false : true;
		return state;
	}

	/**
	 * This method is used to create project
	 * 
	 * @param project
	 * @param users
	 * @return
	 * @throws PurpleException
	 */
	public Project createProject(Project project, Set<User> users) throws PurpleException {

		if (project.getProjectName() != null) {
			if (projectNameExist(project.getProjectName()))
				throw new PurpleException("Project with given name '" + project.getProjectName() + "' already exist",
						ErrorHandler.PROJECT_NOT_FOUND);
			try {
				project = projectRepository.save(project);
				// this is to add reference of project in user object
				// for (User user : users) {
				// Set<Project> projectList = new HashSet<Project>();
				// Set<Project> list = new HashSet<Project>();
				// list = user.getProjects();
				// projectList.add(project);
				// projectList.addAll(list);
				// user.setProjects(projectList);
				// userService.save(user);
				// }
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.ADD_PROJECT_FAILED);
			}
		} else
			throw new PurpleException("Can not add empty project", ErrorHandler.ADD_PROJECT_FAILED);
		return project;
	}

	public Project updateProject(String projectId, Project projectToBeUpdated) throws PurpleException {

		Project updateProject = null;
		if (projectToBeUpdated != null) {
			if (!projectIdExist(projectId))
				throw new PurpleException("Error while updating project", ErrorHandler.PROJECT_NOT_FOUND);
			try {
				updateProject = findByProjectId(projectId);
				/*
				 * this is to first remove reference of project from all user
				 * associate with it one by one and setting all project list
				 * later (only need while updating)
				 */

				// List<User> usersToBeSaved = new ArrayList<>();
				// for (User userTobeRemoved : updateProject.getUser()) {
				// List<Project> projectListFromUser =
				// userTobeRemoved.getProjects();
				// // creating a hashset using the list
				// Set<Project> projectSet = new
				// HashSet<Project>(projectListFromUser);
				// // remove all the elements from the list
				// projectListFromUser.clear();
				// // add all the elements of the set to create a
				// // list with out duplicates
				// projectListFromUser.addAll(projectSet);
				// projectListFromUser.remove(updateProject);
				// usersToBeSaved.add(userTobeRemoved);
				// }
				// userService.save(usersToBeSaved);

				updateProject.setUser(projectToBeUpdated.getUser());
				updateProject.setProjectName(projectToBeUpdated.getProjectName());
				updateProject = projectRepository.save(updateProject);

				// List<User> updateUserList = new ArrayList<User>();
				// for (User user : updateProject.getUser()) {
				//
				// List<Project> projectListFromUser = user.getProjects();
				// // creating a hashset using the list
				// Set<Project> projectSet = new
				// HashSet<Project>(projectListFromUser);
				// // adding updated project into set which will assign to user
				// projectSet.add(updateProject);
				// // remove all the elements from the list
				// projectListFromUser.clear();
				// // add all the elements of the set to create a
				// // list with out duplicates
				// projectListFromUser.addAll(projectSet);
				//
				// user.setProjects(projectListFromUser);
				// updateUserList.add(user);
				//
				// }
				// userService.save(updateUserList);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.UPDATE_PROJECT_FAILED);
			}
		} else
			throw new PurpleException("Can not find empty project", ErrorHandler.PROJECT_NOT_FOUND);
		return updateProject;
	}

	public Project addUsers(String projectId, Set<User> users) throws PurpleException {

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
			Set<User> existUsers = userProject.getUser();
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

	/**
	 * Return all users by project
	 * 
	 * @param projectId
	 * @return
	 * @throws PurpleException
	 */
	public Set<User> getAllUsersByProject(String projectId) throws PurpleException {

		Set<User> users = new HashSet<User>();
		try {
			users = findByProjectId(projectId).getUser();
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_USER_FAILED);
		}
		return users;
	}

	public List<User> getUsersToAssignProject(String projectId) throws PurpleException {

		List<User> users = new ArrayList<User>();
		try {
			List<UserRole> userRoleList = new ArrayList<UserRole>();
			userRoleList.add(UserRole.ADMIN_USER_ROLE);
			userRoleList.add(UserRole.MANAGER_USER_ROLE);

			// getting project owner
			User projectOwner = findByProjectId(projectId).getProjectOwner();

			// getting all user who has role "ADMIN" and "Manager"
			users = userService.getUsersByRole(userRoleList);

			// final user list excluding current project owner
			users.remove(projectOwner);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_USER_FAILED);
		}
		return users;
	}

	/**
	 * Method is used to delete project
	 * 
	 * @param projectId
	 * @throws PurpleException
	 */
	public void deleteProject(String projectId) throws PurpleException {

		if (!projectIdExist(projectId))
			throw new PurpleException("Error while deleting project", ErrorHandler.PROJECT_NOT_FOUND);
		try {
			Project project = findByProjectId(projectId);
			projectRepository.delete(project);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.DELETE_PROJECT_FAILED);
		}
	}

	/**
	 * Return all user according to user role
	 * 
	 * @param user
	 * @return
	 * @throws PurpleException
	 */
	public List<Project> getAllProjects(User user) throws PurpleException {

		List<Project> allProjects = new ArrayList<Project>();
		try {
			if (user != null) {
				if (user.getUserRole().equals(UserRole.ADMIN_USER_ROLE))
					allProjects = projectRepository.findAll();
				else if (user.getUserRole().equals(UserRole.MANAGER_USER_ROLE))
					allProjects = findProjectByProjectOwner(user);
			}

		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_USER_FAILED);
		}
		return allProjects;
	}

	public Project getProject(String projectId) throws PurpleException {

		Project project = null;
		try {
			project = findByProjectId(projectId);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_USER_FAILED);
		}
		if (project == null)
			throw new PurpleException("Error while getting project", ErrorHandler.PROJECT_NOT_FOUND);
		return project;
	}

}