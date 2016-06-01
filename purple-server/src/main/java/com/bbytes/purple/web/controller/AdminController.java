package com.bbytes.purple.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.bbytes.purple.rest.dto.models.ProjectDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.rest.dto.models.UserDTO;
import com.bbytes.purple.service.AdminService;
import com.bbytes.purple.service.DataModelToDTOConversionService;
import com.bbytes.purple.service.EmailService;
import com.bbytes.purple.service.PasswordHashService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.SuccessHandler;

/**
 * Admin Controller
 * 
 * @author akshay
 *
 */
@RestController
public class AdminController {

	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	private AdminService adminService;

	@Autowired
	private UserService userService;

	@Autowired
	protected TokenAuthenticationProvider tokenAuthenticationProvider;

	@Autowired
	private DataModelToDTOConversionService dataModelToDTOConversionService;

	@Autowired
	private PasswordHashService passwordHashService;

	@Autowired
	private EmailService emailService;

	@Value("${base.url}")
	private String baseUrl;

	/**
	 * The add user method is used to add users into tenant
	 * 
	 * @param userDTO
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/admin/user/add", method = RequestMethod.POST)
	public RestResponse addUser(@RequestBody UserDTO userDTO) throws PurpleException {

		final String subject = GlobalConstants.EMAIL_INVITE_SUBJECT;
		final String template = GlobalConstants.EMAIL_INVITE_TEMPLATE;
		DateFormat dateFormat = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);

		Organization org = userService.getLoggedInUser().getOrganization();
		User addUser = new User(userDTO.getUserName(), userDTO.getEmail().toLowerCase());
		addUser.setOrganization(org);
		addUser.setPassword(passwordHashService.encodePassword(GlobalConstants.DEFAULT_PASSWORD));
		addUser.setStatus(User.PENDING);
		addUser.setTimePreference(User.DEFAULT_EMAIL_REMINDER_TIME);

		User user = adminService.addUsers(addUser);
		final String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(user.getEmail(), 720);
		String postDate = dateFormat.format(new Date());
		List<String> emailList = new ArrayList<String>();
		emailList.add(user.getEmail());

		Map<String, Object> emailBody = new HashMap<>();
		emailBody.put(GlobalConstants.USER_NAME, user.getName());
		emailBody.put(GlobalConstants.SUBSCRIPTION_DATE, postDate);
		emailBody.put(GlobalConstants.PASSWORD, GlobalConstants.DEFAULT_PASSWORD);
		emailBody.put(GlobalConstants.ACTIVATION_LINK, baseUrl + GlobalConstants.TOKEN_URL + xauthToken);

		emailService.sendEmail(emailList, emailBody, subject, template);

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

		final String subject = GlobalConstants.EMAIL_INVITE_SUBJECT;
		final String template = GlobalConstants.EMAIL_INVITE_TEMPLATE;

		Organization org = userService.getLoggedInUser().getOrganization();
		List<User> users = adminService.bulkUsers(org, file);

		for (User user : users) {
			final String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(user.getEmail(), 720);
			List<String> emailList = new ArrayList<String>();
			emailList.add(user.getEmail());
			Map<String, Object> emailBody = new HashMap<>();
			emailBody.put(GlobalConstants.USER_NAME, user.getName());
			emailBody.put(GlobalConstants.SUBSCRIPTION_DATE, new Date());
			emailBody.put(GlobalConstants.PASSWORD, GlobalConstants.DEFAULT_PASSWORD);
			emailBody.put(GlobalConstants.ACTIVATION_LINK, baseUrl + GlobalConstants.TOKEN_URL + xauthToken);

			emailService.sendEmail(emailList, emailBody, subject, template);
		}

		List<UserDTO> responseDTO = dataModelToDTOConversionService.convertUsers(users);
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
	@RequestMapping(value = "/api/v1/admin/user/delete/{email:.+}", method = RequestMethod.DELETE)
	public RestResponse deleteUser(@PathVariable("email") String email) throws PurpleException {

		final String DELETE_USER_SUCCESS_MSG = "Successfully deleted user";
		adminService.deleteUser(email);

		logger.debug("User with email  '" + email + "' are deleted successfully");
		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, DELETE_USER_SUCCESS_MSG,
				SuccessHandler.DELETE_USER_SUCCESS);

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

		List<User> users = adminService.getAllUsers();
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

		List<User> user = adminService.getUsersToBeAdded(projectId);
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
	@RequestMapping(value = "/api/v1/admin/user/role", method = RequestMethod.POST)
	public RestResponse updateUserRole(@RequestParam(value = "userId") String userId,
			@RequestParam(value = "role") String role) throws PurpleException {

		User user = adminService.updateUserRole(userId, role);
		UserDTO responseDTO = dataModelToDTOConversionService.convertUser(user);
		logger.debug("User role is updated successfully");
		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, responseDTO, SuccessHandler.GET_USER_SUCCESS);

		return userReponse;
	}

	/**
	 * The create project method is used to add project in to tenant
	 * 
	 * @param projectDTO
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/admin/project/create", method = RequestMethod.POST)
	public RestResponse createProject(@RequestBody ProjectDTO projectDTO) throws PurpleException {

		// we assume angular layer will do empty checks for project
		Organization org = userService.getLoggedInUser().getOrganization();
		Project addProject = new Project(projectDTO.getProjectName());
		addProject.setOrganization(org);
		List<User> usersTobeAdded = new ArrayList<User>();
		for (String i : projectDTO.getUsers()) {
			usersTobeAdded.add(userService.getUserByEmail(i));
		}
		addProject.setUser(usersTobeAdded);
		Project project = adminService.createProject(addProject, usersTobeAdded);
		ProjectDTO projectMap = dataModelToDTOConversionService.convertProject(project);

		logger.debug("User with email  '" + projectDTO.getProjectName() + "' are added successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, projectMap,
				SuccessHandler.ADD_PROJECT_SUCCESS);

		return projectReponse;
	}

	/**
	 * The delete project method is used to delete particular project from
	 * tenant
	 * 
	 * @param projectId
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/admin/project/delete/{projectid}", method = RequestMethod.DELETE)
	public RestResponse deleteProject(@PathVariable("projectid") String projectId) throws PurpleException {

		final String DELETE_PROJECT_SUCCESS_MSG = "Successfully deleted project";
		adminService.deleteProject(projectId);

		logger.debug("Project with Id  '" + projectId + "' are deleted successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, DELETE_PROJECT_SUCCESS_MSG,
				SuccessHandler.DELETE_PROJECT_SUCCESS);

		return projectReponse;
	}

	/**
	 * The get all projects method is used to fetched all projects from tenant
	 * 
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/admin/project", method = RequestMethod.GET)
	public RestResponse getAllProject() throws PurpleException {

		List<Project> projects = adminService.getAllProjects();
		Map<String, Object> projectsMap = dataModelToDTOConversionService
				.getResponseMapWithGridDataAndProjectCount(projects);
		logger.debug("Projects are fetched successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, projectsMap,
				SuccessHandler.GET_PROJECT_SUCCESS);

		return projectReponse;
	}

	@RequestMapping(value = "/api/v1/admin/project/{projectid}", method = RequestMethod.GET)
	public RestResponse getProject(@PathVariable("projectid") String projectId) throws PurpleException {

		Project project = adminService.getProject(projectId);
		ProjectDTO projectMap = dataModelToDTOConversionService.convertProject(project);

		logger.debug("Projects are fetched successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, projectMap,
				SuccessHandler.GET_PROJECT_SUCCESS);

		return projectReponse;
	}

	/**
	 * The update project method is used to update project into tenant
	 * 
	 * @param projectId
	 * @param projectDTO
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/admin/project/update/{projectid}", method = RequestMethod.PUT)
	public RestResponse updateProject(@PathVariable("projectid") String projectId, @RequestBody ProjectDTO projectDTO)
			throws PurpleException {

		// we assume angular layer will do null checks for project object
		Organization org = userService.getLoggedInUser().getOrganization();
		Project updateProject = new Project(projectDTO.getProjectName());
		updateProject.setOrganization(org);
		List<User> usersTobeAdded = new ArrayList<User>();
		for (String i : projectDTO.getUsers()) {
			usersTobeAdded.add(userService.getUserByEmail(i));
		}
		updateProject.setUser(usersTobeAdded);

		Project project = adminService.updateProject(projectId, updateProject);
		ProjectDTO projectMap = dataModelToDTOConversionService.convertProject(project);

		logger.debug("Projects are updated successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, projectMap,
				SuccessHandler.UPDATE_PROJECT_SUCCESS);

		return projectReponse;
	}
}
