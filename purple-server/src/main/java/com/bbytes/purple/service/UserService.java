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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.ProjectUserCountStats;
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

	@Autowired
	private StatusService statusService;

	private UserRepository userRepository;

	@Autowired
	private SpringProfileService springProfileService;

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

	/**
	 * Return total number of role count
	 * 
	 * @param roleName
	 * @return
	 */
	public Long totalRoleCount(UserRole role) {
		return userRepository.countByUserRole(role);
	}

	public Long countByMarkDelete(boolean markDelete) {
		return userRepository.countByMarkDelete(markDelete);
	}

	public Long countByDisableState(boolean disableState) {
		return userRepository.countByDisableState(disableState);
	}

	public User findTopByOrderByCreationDateAsc() {
		return userRepository.findTopByOrderByCreationDateAsc();
	}

	public boolean doesAdminRoleExistInDB(String roleName) {
		if (roleName.equals(UserRole.ADMIN_USER_ROLE.getRoleName())) {
			boolean state = totalRoleCount(new UserRole(roleName)) == 1 ? true : false;
			return state;
		} else
			return false;
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
		if (springProfileService.isEnterpriseMode()) {
			if (doesAdminRoleExistInDB(UserRole.ADMIN_USER_ROLE.getRoleName()))
				user.setUserRole(UserRole.NORMAL_USER_ROLE);
			else
				user.setUserRole(UserRole.ADMIN_USER_ROLE);
		} else {
			user.setUserRole(UserRole.ADMIN_USER_ROLE);
		}
		user.setStatus(User.PENDING);
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
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			final String email = SecurityContextHolder.getContext().getAuthentication().getName();
			return email;
		}
		return null;

	}

	/**
	 * This method will return current logged in User Object from the JWT Token
	 * 
	 * @return
	 */
	public User getLoggedInUser() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			final String email = SecurityContextHolder.getContext().getAuthentication().getName();
			User user = getUserByEmail(email);
			return user;
		}
		return null;

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
				allUsers.addAll(project.getUsers());
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
				allUsers.addAll(getProject.getUsers());
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
	public User disableUser(String userId, String disableState) throws PurpleException {

		User userToBeDisbale = null;
		if (!userExistById(userId))
			throw new PurpleException("Error while disabling user", ErrorHandler.USER_NOT_FOUND);
		try {
			userToBeDisbale = getUserById(userId);
			// disableState=true means set user in disable state
			userToBeDisbale.setDisableState(Boolean.parseBoolean(disableState));

			userToBeDisbale = userRepository.save(userToBeDisbale);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.DISABLE_USER_FAILED);
		}
		return userToBeDisbale;
	}

	/**
	 * Return user with saved device token
	 * 
	 * @param userId
	 * @param deviceToken
	 * @return
	 * @throws PurpleException
	 */
	public User saveDeviceToken(String userId, String deviceToken) throws PurpleException {

		User user = null;
		if (!userExistById(userId))
			throw new PurpleException("Error while saving device token", ErrorHandler.USER_NOT_FOUND);
		try {
			user = getUserById(userId);
			// adding device token in user for push notification for mobile app
			user.setDeviceToken(deviceToken);
			user = userRepository.save(user);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.DEVICE_TOKEN_ADD_FAILED);
		}
		return user;
	}

	/**
	 * return true or false based on device token availability
	 * 
	 * @param userId
	 * @return
	 * @throws PurpleException
	 */
	public boolean isDeviceTokenAvailable(String userId) throws PurpleException {

		if (!userExistById(userId))
			throw new PurpleException("Error while getting device token", ErrorHandler.USER_NOT_FOUND);

		User user = getUserById(userId);
		// checking device token available or not
		boolean isDeviceToken = user.getDeviceToken() == null ? false : true;
		return isDeviceToken;
	}

	public User markForDeleteUser(String userId, String markDeleteState, int days) throws PurpleException {

		User markDeleteUser = null;

		if (!userExistById(userId))
			throw new PurpleException("Error while marking delete user", ErrorHandler.USER_NOT_FOUND);

		markDeleteUser = getUserById(userId);
		if (projectService.projectOwnerExist(markDeleteUser))
			throw new PurpleException("Deletion of project owner is not allowed",
					ErrorHandler.PROJECT_OWNER_DELETE_FAILED);
		try {
			boolean state = Boolean.parseBoolean(markDeleteState);
			// markDeleteState=true means set user as mark for delete
			markDeleteUser.setMarkDelete(state);
			if (state)
				markDeleteUser.setMarkDeleteDate(DateTime.now().plusDays(days).toDate());
			else
				markDeleteUser.setMarkDeleteDate(null);

			markDeleteUser = userRepository.save(markDeleteUser);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.DELETE_USER_FAILED);
		}
		return markDeleteUser;
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
			userList = userRepository.findAll();
			if (projectId != null && !projectId.isEmpty()) {
				if (!projectService.projectIdExist(projectId))
					throw new PurpleException("Error while getting users list", ErrorHandler.PROJECT_NOT_FOUND);
				Set<User> usersOfProject = projectService.getAllUsersByProject(projectId);
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

	/**
	 * getDefaulterUsers method is used to pull the all users who didn't fill
	 * the status
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws PurpleException
	 */
	public List<User> getDefaulterUsers(Date startDate, Date endDate) throws PurpleException {

		List<User> allUsers = getAllUsers();
		Iterable<ProjectUserCountStats> result = statusService.getUserofStatus(startDate, endDate);
		Set<User> userList = new LinkedHashSet<User>();
		for (Iterator<ProjectUserCountStats> iterator = result.iterator(); iterator.hasNext();) {
			ProjectUserCountStats projectUserCountStats = (ProjectUserCountStats) iterator.next();
			userList.add(projectUserCountStats.getUser());
		}
		allUsers.removeAll(userList);
		return allUsers;
	}

	/**
	 * check the user is active or not, active means should be activate their
	 * account, not in disable or mark delete state
	 * 
	 * @param user
	 * @return
	 */
	public boolean isActiveUser(User user) {
		if (user.isAccountInitialise() && !user.isDisableState() && !user.isMarkDelete()
				&& User.JOINED.equals(user.getStatus()))
			return true;
		else
			return false;
	}

}