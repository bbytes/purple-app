package com.bbytes.purple.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bbytes.purple.auth.jwt.TokenAuthenticationProvider;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.rest.dto.models.UserDTO;
import com.bbytes.purple.service.DataModelToDTOConversionService;
import com.bbytes.purple.service.EmailService;
import com.bbytes.purple.service.PasswordHashService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.StringUtils;
import com.bbytes.purple.utils.SuccessHandler;

/**
 * User Controller
 * 
 * @author akshay
 *
 */
@RestController
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@Autowired
	protected TokenAuthenticationProvider tokenAuthenticationProvider;

	@Autowired
	private PasswordHashService passwordHashService;

	@Autowired
	private EmailService emailService;

	@Value("${base.url}")
	private String baseUrl;

	@Value("${email.invite.subject}")
	private String inviteSubject;

	@Autowired
	private DataModelToDTOConversionService dataModelToDTOConversionService;

	/**
	 * The add user method is used to add users into tenant
	 * 
	 * @param userDTO
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/admin/user/add", method = RequestMethod.POST)
	public RestResponse addUser(@RequestBody UserDTO userDTO) throws PurpleException {

		final String template = GlobalConstants.EMAIL_INVITE_TEMPLATE;
		DateFormat dateFormat = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);

		String generatePassword = StringUtils.nextSessionId();

		Organization org = userService.getLoggedInUser().getOrganization();
		User addUser = new User(userDTO.getUserName(), userDTO.getEmail().toLowerCase());
		addUser.setOrganization(org);
		addUser.setPassword(passwordHashService.encodePassword(generatePassword));
		addUser.setStatus(User.PENDING);
		addUser.setTimePreference(User.DEFAULT_EMAIL_REMINDER_TIME);

		User user = userService.addUsers(addUser);
		final String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(user.getEmail(), 720);
		String postDate = dateFormat.format(new Date());
		List<String> emailList = new ArrayList<String>();
		emailList.add(user.getEmail());

		Map<String, Object> emailBody = new HashMap<>();
		emailBody.put(GlobalConstants.USER_NAME, user.getName());
		emailBody.put(GlobalConstants.SUBSCRIPTION_DATE, postDate);
		emailBody.put(GlobalConstants.PASSWORD, generatePassword);
		emailBody.put(GlobalConstants.ACTIVATION_LINK, baseUrl + GlobalConstants.TOKEN_URL + xauthToken);

		emailService.sendEmail(emailList, emailBody, inviteSubject, template);

		UserDTO responseDTO = dataModelToDTOConversionService.convertUser(user);
		logger.debug("User with email  '" + userDTO.getEmail() + "' are added successfully");
		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, responseDTO, SuccessHandler.ADD_USER_SUCCESS);

		return userReponse;
	}

	/**
	 * The bulk upload users method is used to upload list of users from csv
	 * file.
	 * 
	 * @param file
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/admin/user/bulkupload", method = RequestMethod.POST)
	public RestResponse bulkUploadUsers(@RequestParam("file") MultipartFile file) throws PurpleException {

		final String template = GlobalConstants.EMAIL_INVITE_TEMPLATE;
		List<User> allUser = new LinkedList<User>();

		Organization org = userService.getLoggedInUser().getOrganization();
		Map<String, User> userMap = userService.bulkUsers(org, file);

		for (Map.Entry<String, User> entry : userMap.entrySet()) {
			allUser.add(entry.getValue());
		}
		for (Map.Entry<String, User> entry : userMap.entrySet()) {

			final String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(entry.getValue().getEmail(), 720);
			List<String> emailList = new ArrayList<String>();
			emailList.add(entry.getValue().getEmail());
			Map<String, Object> emailBody = new HashMap<>();
			emailBody.put(GlobalConstants.USER_NAME, entry.getValue().getName());
			emailBody.put(GlobalConstants.SUBSCRIPTION_DATE, new Date());
			emailBody.put(GlobalConstants.PASSWORD, entry.getKey());
			emailBody.put(GlobalConstants.ACTIVATION_LINK, baseUrl + GlobalConstants.TOKEN_URL + xauthToken);

			emailService.sendEmail(emailList, emailBody, inviteSubject, template);
		}

		List<UserDTO> responseDTO = dataModelToDTOConversionService.convertUsers(allUser);
		logger.debug(responseDTO.size() + "' User are added successfully");

		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, responseDTO, SuccessHandler.ADD_USER_SUCCESS);

		return userReponse;
	}

	/**
	 * The delete user method is used to delete particular user from tenant
	 * 
	 * @param email
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/user/delete/{email:.+}", method = RequestMethod.DELETE)
	public RestResponse deleteUser(@PathVariable("email") String email) throws PurpleException {

		final String DELETE_USER_SUCCESS_MSG = "Successfully deleted user";
		userService.deleteUser(email);

		logger.debug("User with email  '" + email + "' are deleted successfully");
		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, DELETE_USER_SUCCESS_MSG,
				SuccessHandler.DELETE_USER_SUCCESS);

		return userReponse;
	}

	/**
	 * disableUser method is used to disable particular user from tenant
	 * 
	 * @param userId
	 * @return
	 * @throws PurpleException
	 */

	@RequestMapping(value = "/api/v1/user/disable/{userId}", method = RequestMethod.PUT)
	public RestResponse disableUser(@PathVariable("userId") String userId, @RequestParam(value = "state") String state)
			throws PurpleException {

		User user = userService.disableUser(userId, state);
		UserDTO responseDTO = dataModelToDTOConversionService.convertUser(user);

		logger.debug("User with email  '" + user.getEmail() + "' are disabled successfully");
		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, responseDTO);

		return userReponse;
	}

	/**
	 * The get user method is used to fetched all users related to tenant
	 * 
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/admin/user", method = RequestMethod.GET)
	public RestResponse getAllUsers() throws PurpleException {

		List<User> users = userService.getAllUsers();
		Map<String, Object> usersMap = dataModelToDTOConversionService
				.getResponseMapWithGridDataAndUserStatusCount(users);
		logger.debug("Users are fetched successfully");
		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, usersMap, SuccessHandler.GET_USER_SUCCESS);

		return userReponse;
	}

	/**
	 * The getUserListToBeAddedToProject method is used to get userList are to
	 * be added to project
	 * 
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/admin/users/project", method = RequestMethod.GET)
	public RestResponse getuserListToBeAddedToProject(@RequestParam(required = false) String projectId)
			throws PurpleException {

		List<User> user = userService.getUsersToBeAdded(projectId);
		Map<String, Object> usersMap = dataModelToDTOConversionService
				.getResponseMapWithGridDataAndUserStatusCount(user);
		logger.debug("Users are fetched successfully");
		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, usersMap, SuccessHandler.GET_USER_SUCCESS);

		return userReponse;
	}

	/**
	 * The updateUserRole method is used to assigning role to user
	 * 
	 * @param userId
	 * @param role
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/user/role", method = RequestMethod.PUT)
	public RestResponse updateUserRole(@RequestParam(value = "userId") String userId,
			@RequestParam(value = "role") String role) throws PurpleException {

		User user = userService.updateUserRole(userId, role);
		UserDTO responseDTO = dataModelToDTOConversionService.convertUser(user);
		logger.debug("User role is updated successfully");
		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, responseDTO, SuccessHandler.GET_USER_SUCCESS);

		return userReponse;
	}

	/**
	 * reInvite method is used to re-send email for pending user.
	 * 
	 * @param userDTO
	 * @return
	 * @throws PurpleException
	 */

	@RequestMapping(value = "/api/v1/admin/user/reinvite", method = RequestMethod.GET)
	public RestResponse reInvite(@RequestParam("name") String name, @RequestParam("email") String email)
			throws PurpleException {

		final String template = GlobalConstants.EMAIL_INVITE_TEMPLATE;
		final String REINVITE_SUCCESS_MSG = "Reinvite is successful";
		DateFormat dateFormat = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);

		String generatePassword = StringUtils.nextSessionId();

		User user = userService.reInvitetoUser(email, generatePassword);
		final String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(user.getEmail(), 720);
		String postDate = dateFormat.format(new Date());
		List<String> emailList = new ArrayList<String>();
		emailList.add(email);

		Map<String, Object> emailBody = new HashMap<>();
		emailBody.put(GlobalConstants.USER_NAME, name);
		emailBody.put(GlobalConstants.SUBSCRIPTION_DATE, postDate);
		emailBody.put(GlobalConstants.PASSWORD, generatePassword);
		emailBody.put(GlobalConstants.ACTIVATION_LINK, baseUrl + GlobalConstants.TOKEN_URL + xauthToken);

		emailService.sendEmail(emailList, emailBody, inviteSubject, template);

		logger.debug("Reinvite is done successfully to user with email - " + email);
		RestResponse inviteReponse = new RestResponse(RestResponse.SUCCESS, REINVITE_SUCCESS_MSG,
				SuccessHandler.REINVITE_SUCCESS);

		return inviteReponse;
	}

	/**
	 * The getAllProjectsByUser method is used to fetched all projects by logged
	 * in user
	 * 
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/user/projects", method = RequestMethod.GET)
	public RestResponse getAllProjectsByUser() throws PurpleException {

		User user = userService.getLoggedInUser();
		List<Project> projects = userService.getProjects(user);
		Map<String, Object> projectsMap = dataModelToDTOConversionService
				.getResponseMapWithGridDataAndProjectList(projects);
		logger.debug("Projects are fetched successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, projectsMap,
				SuccessHandler.GET_PROJECT_SUCCESS);

		return projectReponse;
	}

	/**
	 * Method is used to get current user info
	 * 
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/currentUser", method = RequestMethod.GET)
	public RestResponse getCurrentUser() throws PurpleException {

		User user = userService.getLoggedInUser();
		if (user == null)
			throw new PurpleException("User is not authorized", ErrorHandler.AUTH_FAILURE);
		UserDTO currentUserMap = dataModelToDTOConversionService.convertUser(user);
		logger.debug("Current user are fetched successfully");
		RestResponse currentUserReponse = new RestResponse(RestResponse.SUCCESS, currentUserMap,
				SuccessHandler.GET_USER_SUCCESS);

		return currentUserReponse;
	}

	/**
	 * The getUsersByProjects method is used to fetched all user which are part
	 * of projects in user
	 * 
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/projects/users/all", method = RequestMethod.POST)
	public RestResponse getUsersByProjects(@RequestBody List<String> projectList) throws PurpleException {

		Set<User> users = userService.getAllUsersbyProjects(projectList);
		List<User> userList = new ArrayList<User>();
		userList.addAll(users);
		Map<String, Object> usersMap = dataModelToDTOConversionService
				.getResponseMapWithGridDataAndUserStatusCount(userList);
		logger.debug("Projects are fetched successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, usersMap, SuccessHandler.GET_USER_SUCCESS);

		return projectReponse;
	}

	@RequestMapping(value = "/api/v1/projects/users/all/map", method = RequestMethod.POST)
	public RestResponse getUsersByProjectsMap(@RequestBody List<String> projectList) throws PurpleException {

		List<Project> projects = userService.getAllUsersbyProjectMap(projectList);
		Map<String, Object> projectMap = dataModelToDTOConversionService
				.getResponseMapWithGridDataAndProjectCount(projects);
		logger.debug("Projects are fetched successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, projectMap,
				SuccessHandler.GET_PROJECT_SUCCESS);

		return projectReponse;
	}

	/**
	 * The updateUser methods is used to update user's profile
	 * 
	 * @param userName
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/user/update", method = RequestMethod.PUT)
	public RestResponse updateUser(@RequestParam("userName") String userName) throws PurpleException {

		User user = userService.getLoggedInUser();

		User updatedUser = userService.updateUserProfile(userName, user);
		UserDTO responseDTO = dataModelToDTOConversionService.convertUser(updatedUser);
		logger.debug("User profile is updated successfully");
		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, responseDTO,
				SuccessHandler.UPDATE_USER_SUCCESS);

		return userReponse;
	}

}
