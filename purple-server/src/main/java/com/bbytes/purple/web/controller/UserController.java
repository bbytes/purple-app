package com.bbytes.purple.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
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
import com.bbytes.purple.service.NotificationService;
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
	private NotificationService notificationService;

	@Autowired
	private ResourceLoader resourceloader;

	@Value("${base.url}")
	private String baseUrl;

	@Value("${email.invite.subject}")
	private String inviteSubject;

	@Value("${sample.bulkuplaod.file}")
	private String sampleBulkuploadFile;

	@Autowired
	private DataModelToDTOConversionService dataModelToDTOConversionService;

	/**
	 * The add user method is used to add users into tenant
	 * 
	 * @param userDTO
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/user/add", method = RequestMethod.POST)
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

		notificationService.sendTemplateEmail(emailList, inviteSubject, template, emailBody);

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
	@RequestMapping(value = "/api/v1/user/bulkupload", method = RequestMethod.POST)
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

			notificationService.sendTemplateEmail(emailList, inviteSubject, template, emailBody);
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

		logger.debug("User with email  '" + email + "' is deleted successfully");
		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, DELETE_USER_SUCCESS_MSG,
				SuccessHandler.DELETE_USER_SUCCESS);

		return userReponse;
	}

	/**
	 * The addDeviceToken method is used to save the device token for push
	 * notification in mobile app.
	 * 
	 * @param userId
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/user/devicetoken/add", method = RequestMethod.PUT)
	public RestResponse addDeviceToken(@RequestParam("deviceToken") String deviceToken) throws PurpleException {

		User user = userService.getLoggedInUser();
		User updatedUser = userService.saveDeviceToken(user.getUserId(), deviceToken);

		logger.debug("User with email  '" + updatedUser.getEmail() + "' is saved device token successfully");
		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, updatedUser,
				SuccessHandler.ADD_DEVICE_TOKEN_SUCCESS);

		return userReponse;
	}

	/**
	 * The isDeviceTokenAvailable method is used to check whether device token
	 * is available or not
	 * 
	 * @param userId
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/user/devicetoken", method = RequestMethod.GET)
	public RestResponse isDeviceTokenAvailable() throws PurpleException {

		User user = userService.getLoggedInUser();
		boolean deviceTokenAvailable = userService.isDeviceTokenAvailable(user.getUserId());

		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, deviceTokenAvailable,
				SuccessHandler.GET_DEVICE_TOKEN_SUCCESS);

		return userReponse;
	}

	/**
	 * disableUser method is used to disable particular user from tenant
	 * 
	 * @param userId
	 * @param state
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/user/disable/{userId}", method = RequestMethod.PUT)
	public RestResponse disableUser(@PathVariable("userId") String userId,
			@RequestParam(value = "disableState") String state) throws PurpleException {

		User user = userService.disableUser(userId, state);
		UserDTO responseDTO = dataModelToDTOConversionService.convertUser(user);

		logger.debug("User with email  '" + user.getEmail() + "' is disabled successfully");
		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, responseDTO);

		return userReponse;
	}

	/**
	 * markForDeleteUser method is used to set mark for delete user (User and
	 * their statues, comments will be deleted after 30 days with running thread
	 * logic)
	 * 
	 * @param userId
	 * @param markdeleteState
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/user/markdelete/{userId}", method = RequestMethod.DELETE)
	public RestResponse markForDeleteUser(@PathVariable("userId") String userId,
			@RequestParam(value = "markdeleteState") String markdeleteState, @RequestParam(value = "days") int days)
			throws PurpleException {

		RestResponse userReponse = null;
		User user = userService.getUserById(userId);
		if (userService.doesAdminRoleExistInDB(userService.getUserById(userId).getUserRole().getRoleName())) {
			UserDTO responseDTO = dataModelToDTOConversionService.convertUser(user);
			userReponse = new RestResponse(RestResponse.FAILED, responseDTO, ErrorHandler.DELETION_NOT_ALLOWED);
			return userReponse;
		}
		// note: days is capturing from angular side to delete user data after
		// these many no of days.
		User updatedUser = userService.markForDeleteUser(userId, markdeleteState, days);
		UserDTO responseDTO = dataModelToDTOConversionService.convertUser(updatedUser);

		logger.debug("User with email  '" + updatedUser.getEmail() + "' is marked for delete");
		userReponse = new RestResponse(RestResponse.SUCCESS, responseDTO);

		return userReponse;
	}

	/**
	 * The get user method is used to fetched all users related to tenant
	 * 
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/user", method = RequestMethod.GET)
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
	@RequestMapping(value = "/api/v1/users/project", method = RequestMethod.GET)
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

		RestResponse userReponse = null;
		User user = userService.getUserById(userId);
		if (userService.doesAdminRoleExistInDB(userService.getUserById(userId).getUserRole().getRoleName())) {
			UserDTO responseDTO = dataModelToDTOConversionService.convertUser(user);
			userReponse = new RestResponse(RestResponse.FAILED, responseDTO, ErrorHandler.ROLE_CHANGED_NOT_ALLOWED);
			return userReponse;
		}
		User updatedUser = userService.updateUserRole(userId, role);
		UserDTO responseDTO = dataModelToDTOConversionService.convertUser(updatedUser);
		logger.debug("User role is updated successfully");
		userReponse = new RestResponse(RestResponse.SUCCESS, responseDTO, SuccessHandler.GET_USER_SUCCESS);

		return userReponse;
	}

	/**
	 * reInvite method is used to re-send email for pending user.
	 * 
	 * @param userDTO
	 * @return
	 * @throws PurpleException
	 */

	@RequestMapping(value = "/api/v1/user/reinvite", method = RequestMethod.GET)
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

		notificationService.sendTemplateEmail(emailList, inviteSubject, template, emailBody);

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
	 * Method is used to set the view type for timeline
	 * 
	 * @param viewType
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/user/viewtype", method = RequestMethod.PUT)
	public RestResponse setViewType(@RequestParam(value = "viewType") String viewType) throws PurpleException {

		User loggedInUser = userService.getLoggedInUser();
		loggedInUser = userService.setViewType(viewType, loggedInUser);
		UserDTO responseDTO = dataModelToDTOConversionService.convertUser(loggedInUser);
		logger.debug("View Type is updated successfully");
		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, responseDTO,
				SuccessHandler.UPDATE_USER_SUCCESS);

		return userReponse;
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

	/**
	 * The get csv for all status related to project for current user
	 * 
	 * @return
	 * @throws PurpleException
	 * @throws IOException
	 */
	@RequestMapping(value = "/api/v1/bulkupload/sample/download", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public void getSampleBulkUploadFile(HttpServletResponse response) throws PurpleException, IOException {
		InputStream fileStream = resourceloader.getResource(sampleBulkuploadFile).getInputStream();
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition",
				String.format("attachment; filename=\"%s\"", "sample-user-upload.csv"));
		IOUtils.copy(fileStream, response.getOutputStream());
		response.flushBuffer();

	}

}
