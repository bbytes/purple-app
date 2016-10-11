package com.bbytes.purple.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.StringUtils;

@Service
public class AdminService {

	@Autowired
	private UserService userService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private TenantResolverService tenantResolverService;

	@Autowired
	private PasswordHashService passwordHashService;

	public User addUsers(User user) throws PurpleException {

		if (user != null) {
			if (userService.userEmailExist(user.getEmail()) || tenantResolverService.emailExist(user.getEmail()))
				throw new PurpleException("Username or Email already exist", ErrorHandler.USER_NOT_FOUND);
			try {
				user = userService.save(user);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.ADD_USER_FAILED);
			}
		}
		return user;
	}

	public User reInvitetoUser(String email, String randomPassword) throws PurpleException {

		User user = null;
		if (!userService.userEmailExist(email) || !tenantResolverService.emailExist(email))
			throw new PurpleException("User not found", ErrorHandler.USER_NOT_FOUND);
		try {
			user = userService.getUserByEmail(email);
			user.setPassword(passwordHashService.encodePassword(randomPassword));
			user = userService.save(user);
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
				if ((!userService.userEmailExist(addUser.getEmail())
						|| !tenantResolverService.emailExist(addUser.getEmail())) && result) {
					try {
						User user = userService.save(addUser);
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

		if (!userService.userEmailExist(email))
			throw new PurpleException("Error while deleting user", ErrorHandler.USER_NOT_FOUND);
		try {
			User deleteuser = userService.getUserByEmail(email);
			userService.delete(deleteuser);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.DELETE_USER_FAILED);
		}
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

	public List<User> getUsersToBeAdded(String projectId) throws PurpleException {

		List<User> userList = new LinkedList<User>();
		try {
			if (projectId == null || projectId.isEmpty()) {
				List<User> users = userService.findAll();
				for (User user : users) {
					if (user.getStatus().equals(User.JOINED))
						userList.add(user);
				}
			} else {
				if (!projectService.projectIdExist(projectId))
					throw new PurpleException("Error while getting users list", ErrorHandler.PROJECT_NOT_FOUND);
				List<User> usersOfProject = projectService.getAllUsersByProject(projectId);
				List<User> users = userService.findAll();
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
		if (!userService.exists(userId))
			throw new PurpleException("Error while assigning role", ErrorHandler.USER_NOT_FOUND);
		try {
			user = userService.getUserById(userId);
			user.setUserRole(new UserRole(role));
			user = userService.save(user);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.UPDATE_USERROLE_FAILED);
		}

		return user;
	}

}
