package com.bbytes.purple.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.repository.UserRepository;
import com.bbytes.purple.utils.ErrorHandler;

@Service
public class UserService extends AbstractService<User, String> {

	@Autowired
	private PasswordHashService passwordHashService;

	@Autowired
	private ProjectService projectService;

	private UserRepository userRepository;

	@Autowired
	public UserService(UserRepository userRepository) {
		super(userRepository);
		this.userRepository = userRepository;
	}

	public boolean userEmailExist(String email) {
		boolean state = userRepository.findOneByEmail(email) == null ? false : true;
		return state;
	}

	public User getUserById(String id) {
		return userRepository.findOne(id);
	}

	public List<User> getUserByRoleName(UserRole role) {
		return userRepository.findByUserRole(role);
	}

	public User getUserByEmail(String email) {
		return userRepository.findOneByEmail(email);
	}

	public Iterable<User> getAllUsers() {
		return userRepository.findAll(new Sort("email"));
	}

	public void delete(User user) {
		userRepository.delete(user);
	}

	public void deleteAll() {
		userRepository.deleteAll();
	}

	public User create(String email, String name, Organization org) {
		User user = new User(name, email);
		user.setOrganization(org);
		return userRepository.save(user);
	}

	public User create(String email, String name, String password, Organization org) {
		User user = new User(name, email);
		user.setOrganization(org);
		user.setPassword(passwordHashService.encodePassword(password));
		user.setUserRole(UserRole.ADMIN_USER_ROLE);
		return userRepository.save(user);
	}

	public boolean updatePassword(String password, String userEmail) {
		User user = userRepository.findOneByEmail(userEmail);
		return updatePassword(password, user);
	}

	public boolean updatePassword(String password, User user) {
		if (user != null && password != null) {
			user.setPassword(passwordHashService.encodePassword(password));
			userRepository.save(user);
			return true;
		}

		return false;
	}

	/**
	 * This method will return current logged in User's email address from the
	 * JWT Token
	 * 
	 * @return
	 */
	public String getLoggedInUserEmail() {
		final String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return email;
	}

	/**
	 * This method will return current logged in User Object from the JWT Token
	 * 
	 * @return
	 */
	public User getLoggedInUser() {
		final String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = getUserByEmail(email);
		return user;
	}

	public List<Project> getProjects(User user) throws PurpleException {

		List<Project> allProjects = new ArrayList<Project>();
		try {
			allProjects = projectService.findProjectByUser(user);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_PROJECT_FAILED);
		}
		return allProjects;
	}

	public Set<User> getAllUsersbyProjects(List<String> projectList) throws PurpleException {

		Set<User> allUsers = new HashSet<User>();
		try {
			for (String projectId : projectList) {
				Project project = projectService.findByProjectId(projectId);
				allUsers.addAll(project.getUser());
			}

		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_PROJECT_FAILED);
		}
		return allUsers;
	}

	public Set<User> getUsersbyProjects(List<Project> projectList) throws PurpleException {

		Set<User> allUsers = new HashSet<User>();
		try {
			for (Project project : projectList) {
				Project getProject = projectService.findByProjectId(project.getProjectId());
				allUsers.addAll(getProject.getUser());
			}

		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_PROJECT_FAILED);
		}
		return allUsers;
	}

	public List<Project> getAllUsersbyProjectMap(List<String> projectList) throws PurpleException {

		List<Project> allProjects = new LinkedList<Project>();
		try {
			for (String projectId : projectList) {
				Project project = projectService.findByProjectId(projectId);
				allProjects.add(project);
			}

		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_PROJECT_FAILED);
		}
		return allProjects;
	}

	public User updateUserProfile(String userName, User user) throws PurpleException {

		User updatedUser = null;
		if (!exists(user.getUserId()))
			throw new PurpleException("Error while updating user profile", ErrorHandler.USER_NOT_FOUND);
		try {
			user.setName(userName);
			updatedUser = userRepository.save(user);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.UPDATE_USER_PROFILE_FAILED);
		}

		return updatedUser;
	}
}