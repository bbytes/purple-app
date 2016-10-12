package com.bbytes.purple.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.repository.UserRepository;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.StringUtils;

@Service
public class UserService extends AbstractService<User, String> {

	@Autowired
	private PasswordHashService passwordHashService;

	@Autowired
	private ProjectService projectService;

	private UserRepository userRepository;

	@Autowired
	private TenantResolverService tenantResolverService;

	@Autowired
	public UserService(UserRepository userRepository) {
		super(userRepository);
		this.userRepository = userRepository;
	}

	public boolean userEmailExist(String email) {
		boolean state = userRepository.findOneByEmail(email) == null ? false : true;
		return state;
	}

	public boolean userExistById(String userId) {
		boolean state = getUserById(userId) == null ? false : true;
		return state;
	}

	public User getUserById(String id) {
		return userRepository.findOne(id);
	}

	public List<User> getUsersByRole(UserRole role) {
		return userRepository.findByUserRole(role);
	}

	public List<User> getUsersByRole(List<UserRole> roleList) {
		return userRepository.findByUserRoleIn(roleList);
	}

	public User getUserByEmail(String email) {
		return userRepository.findOneByEmail(email);
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
		user.setStatus(User.PENDING);
		;
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

	public Set<User> getUsersbyProjects(Set<Project> projectList) throws PurpleException {

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

	public User addUsers(User user) throws PurpleException {

		if (user != null) {
			if (userEmailExist(user.getEmail()) || tenantResolverService.emailExist(user.getEmail()))
				throw new PurpleException("Username or Email already exist", ErrorHandler.USER_NOT_FOUND);
			try {
				user = userRepository.save(user);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.ADD_USER_FAILED);
			}
		}
		return user;
	}

	public User reInvitetoUser(String email, String randomPassword) throws PurpleException {

		User user = null;
		if (!userEmailExist(email) || !tenantResolverService.emailExist(email))
			throw new PurpleException("User not found", ErrorHandler.USER_NOT_FOUND);
		try {
			user = getUserByEmail(email);
			user.setPassword(passwordHashService.encodePassword(randomPassword));
			user = userRepository.save(user);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.REINVITE_FAILED);
		}
		return user;
	}

	public Map<String, User> bulkUsers(Organization org, MultipartFile file) throws PurpleException {

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		Map<String, String> maps = new LinkedHashMap<String, String>();
		Map<String, User> bulkUsers = new LinkedHashMap<String, User>();
		InternetAddress emailAddr;

		try {

			byte[] content = file.getBytes();
			InputStream is = new ByteArrayInputStream(content);
			br = new BufferedReader(new InputStreamReader(is));

			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] users = line.split(cvsSplitBy);
				maps.put(users[0], users[1]);
			}
			for (Map.Entry<String, String> entry : maps.entrySet()) {

				String generatePassword = StringUtils.nextSessionId();
				User addUser = new User(entry.getValue(), entry.getKey().toLowerCase());
				addUser.setOrganization(org);
				addUser.setPassword(passwordHashService.encodePassword(generatePassword));
				addUser.setStatus(User.PENDING);
				addUser.setTimePreference(User.DEFAULT_EMAIL_REMINDER_TIME);

				boolean result = true;
				try {
					emailAddr = new InternetAddress(addUser.getEmail());
					emailAddr.validate();
				} catch (AddressException e) {
					result = false;
				}
				if ((!userEmailExist(addUser.getEmail()) || !tenantResolverService.emailExist(addUser.getEmail()))
						&& result) {
					try {
						User user = userRepository.save(addUser);
						bulkUsers.put(generatePassword, user);
					} catch (Throwable e) {
						throw new PurpleException(e.getMessage(), ErrorHandler.BULD_UPLAOD_FAILED);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.getMessage();
		} catch (IOException e) {
			e.getMessage();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.getMessage();
				}
			}
		}

		return bulkUsers;
	}

	public void deleteUser(String email) throws PurpleException {

		if (!userEmailExist(email))
			throw new PurpleException("Error while deleting user", ErrorHandler.USER_NOT_FOUND);
		try {
			User deleteuser = getUserByEmail(email);
			userRepository.delete(deleteuser);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.DELETE_USER_FAILED);
		}
	}

	/**
	 * Return user with disable state with date and time
	 * 
	 * @param email
	 * @throws PurpleException
	 */
	public User disableUser(String userId, String state) throws PurpleException {

		User userToBeDisbale = null;
		if (!userExistById(userId))
			throw new PurpleException("Error while disabling user", ErrorHandler.USER_NOT_FOUND);
		try {
			userToBeDisbale = getUserById(userId);
			userToBeDisbale.setDisableState(Boolean.parseBoolean(state));

			userToBeDisbale = userRepository.save(userToBeDisbale);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.DELETE_USER_FAILED);
		}
		return userToBeDisbale;
	}

	public List<User> getAllUsers() throws PurpleException {

		List<User> users = new ArrayList<User>();
		try {
			users = userRepository.findAll();
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_USER_FAILED);
		}

		return users;
	}

	public List<User> getUsersToBeAdded(String projectId) throws PurpleException {

		List<User> userList = new LinkedList<User>();
		try {
			if (projectId == null || projectId.isEmpty()) {
				List<User> users = userRepository.findAll();
				for (User user : users) {
					if (user.getStatus().equals(User.JOINED))
						userList.add(user);
				}
			} else {
				if (!projectService.projectIdExist(projectId))
					throw new PurpleException("Error while getting users list", ErrorHandler.PROJECT_NOT_FOUND);
				Set<User> usersOfProject = projectService.getAllUsersByProject(projectId);
				List<User> users = userRepository.findAll();
				for (User user : users) {
					if (user.getStatus().equals(User.JOINED))
						userList.add(user);
				}
				userList.removeAll(usersOfProject);
			}

		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_USER_FAILED);
		}
		return userList;
	}

	public User updateUserRole(String userId, String role) throws PurpleException {

		User user = null;
		if (!exists(userId))
			throw new PurpleException("Error while assigning role", ErrorHandler.USER_NOT_FOUND);
		try {
			user = getUserById(userId);
			user.setUserRole(new UserRole(role));
			user = userRepository.save(user);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.UPDATE_USERROLE_FAILED);
		}

		return user;
	}
}