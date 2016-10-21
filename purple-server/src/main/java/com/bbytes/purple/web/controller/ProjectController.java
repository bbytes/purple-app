package com.bbytes.purple.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.purple.auth.jwt.TokenAuthenticationProvider;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.ProjectDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.service.DataModelToDTOConversionService;
import com.bbytes.purple.service.EmailService;
import com.bbytes.purple.service.ProjectService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.SuccessHandler;

/**
 * The Project Controller
 * 
 * @author akshay
 *
 */
@RestController
public class ProjectController {

	private static final Logger logger = LoggerFactory.getLogger(StatusController.class);

	@Autowired
	private ProjectService projectService;

	@Autowired
	private DataModelToDTOConversionService dataModelToDTOConversionService;

	@Autowired
	protected TokenAuthenticationProvider tokenAuthenticationProvider;

	@Autowired
	private UserService userService;

	@Autowired
	private EmailService emailService;

	@Value("${base.url}")
	private String baseUrl;

	@Value("${email.invite.project.subject}")
	private String projectInviteSubject;

	/**
	 * The create project method is used to add project in to tenant
	 * 
	 * @param projectDTO
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/project/create", method = RequestMethod.POST)
	public RestResponse createProject(@RequestBody ProjectDTO projectDTO) throws PurpleException {

		final String template = GlobalConstants.EMAIL_INVITE_PROJECT_TEMPLATE;
		DateFormat dateFormat = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);

		// we assume angular layer will do empty checks for project
		User loggedInUser = userService.getLoggedInUser();
		Organization org = loggedInUser.getOrganization();
		Project addProject = new Project(projectDTO.getProjectName());
		addProject.setOrganization(org);
		addProject.setProjectOwner(loggedInUser);

		Set<User> usersTobeAdded = new HashSet<User>();
		for (String i : projectDTO.getUsers()) {
			usersTobeAdded.add(userService.getUserByEmail(i));
		}
		addProject.setUser(usersTobeAdded);
		Project project = projectService.createProject(addProject, usersTobeAdded);

		String postDate = dateFormat.format(new Date());
		long currentDate = new Date().getTime();

		for (User addedUser : usersTobeAdded) {
			if (userService.isActiveUser(addedUser)) {
				final String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(addedUser.getEmail(), 168);
				Map<String, Object> emailBody = new HashMap<>();
				List<String> emailList = new ArrayList<String>();
				emailList.add(addedUser.getEmail());
				emailBody.put(GlobalConstants.PROJECT_NAME, projectDTO.getProjectName());
				emailBody.put(GlobalConstants.USER_NAME, addedUser.getName());
				emailBody.put(GlobalConstants.SUBSCRIPTION_DATE, postDate);
				emailBody.put(GlobalConstants.ACTIVATION_LINK,
						baseUrl + GlobalConstants.STATUS_URL + xauthToken + GlobalConstants.STATUS_DATE + currentDate);

				emailService.sendEmail(emailList, emailBody, projectInviteSubject, template);
			}
		}
		ProjectDTO projectMap = dataModelToDTOConversionService.convertProject(project);

		logger.debug("User with email  '" + projectDTO.getProjectName() + "' are added successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, projectMap,
				SuccessHandler.ADD_PROJECT_SUCCESS);

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
	@RequestMapping(value = "/api/v1/project/update/{projectid}", method = RequestMethod.PUT)
	public RestResponse updateProject(@PathVariable("projectid") String projectId, @RequestBody ProjectDTO projectDTO)
			throws PurpleException {

		final String template = GlobalConstants.EMAIL_INVITE_PROJECT_TEMPLATE;
		DateFormat dateFormat = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);

		// we assume angular layer will do null checks for project object
		User loggedInuser = userService.getLoggedInUser();
		Organization org = loggedInuser.getOrganization();
		Project updateProject = new Project(projectDTO.getProjectName());
		updateProject.setOrganization(org);
		Set<User> usersTobeAdded = new HashSet<User>();
		for (String i : projectDTO.getUsers()) {
			usersTobeAdded.add(userService.getUserByEmail(i));
		}
		Set<User> usersFromProject = projectService.findByProjectId(projectId).getUser();

		List<User> updateUserList = new LinkedList<User>();
		for (User user : usersTobeAdded) {

			if (!usersFromProject.contains(user))
				updateUserList.add(user);
		}
		updateProject.setUser(usersTobeAdded);
		Project project = projectService.updateProject(projectId, updateProject);

		String postDate = dateFormat.format(new Date());
		long currentDate = new Date().getTime();

		// Here get newly added user to project.
		for (User sendMailtoUpdatedUser : updateUserList) {
			if (userService.isActiveUser(sendMailtoUpdatedUser)) {
				final String xauthToken = tokenAuthenticationProvider
						.getAuthTokenForUser(sendMailtoUpdatedUser.getEmail(), 168);
				List<String> emailList = new LinkedList<String>();
				emailList.add(sendMailtoUpdatedUser.getEmail());
				Map<String, Object> emailBody = new HashMap<>();
				emailBody.put(GlobalConstants.PROJECT_NAME, projectDTO.getProjectName());
				emailBody.put(GlobalConstants.USER_NAME, sendMailtoUpdatedUser.getName());
				emailBody.put(GlobalConstants.SUBSCRIPTION_DATE, postDate);
				emailBody.put(GlobalConstants.ACTIVATION_LINK,
						baseUrl + GlobalConstants.STATUS_URL + xauthToken + GlobalConstants.STATUS_DATE + currentDate);

				emailService.sendEmail(emailList, emailBody, projectInviteSubject, template);
			}
		}
		ProjectDTO projectMap = dataModelToDTOConversionService.convertProject(project);

		logger.debug("Projects are updated successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, projectMap,
				SuccessHandler.UPDATE_PROJECT_SUCCESS);

		return projectReponse;
	}

	/**
	 * The addUserToProject method is used to add the list of users into project
	 * 
	 * @param projectId
	 * @param projectDTO
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/project/{projectId}/adduser", method = RequestMethod.POST)
	public RestResponse addUsersToProject(@PathVariable("projectId") String projectId,
			@RequestBody ProjectDTO projectDTO) throws PurpleException {

		Set<User> usersTobeAdded = new HashSet<User>();
		for (String i : projectDTO.getUsers()) {
			if (!userService.userEmailExist(i))
				throw new PurpleException("Error while adding users", ErrorHandler.ADD_USER_FAILED);
			usersTobeAdded.add(userService.getUserByEmail(i));
		}

		Project project = projectService.addUsers(projectId, usersTobeAdded);

		logger.debug(usersTobeAdded.size() + "' users are added successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, project,
				SuccessHandler.ADD_PROJECT_SUCCESS);

		return projectReponse;
	}

	/**
	 * The deleteUsers method is used to delete list of users from project
	 * 
	 * @param projectId
	 * @param projectDTO
	 * @return
	 * @throws PurpleException
	 */

	@RequestMapping(value = "/api/v1/project/{projectId}/deleteuser", method = RequestMethod.DELETE)
	public RestResponse deleteUsers(@PathVariable("projectId") String projectId, @RequestBody List<String> emailList)
			throws PurpleException {

		Project project = projectService.deleteUsers(projectId, emailList);

		logger.debug(project.getUser().size() + "' users are deleted successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, project,
				SuccessHandler.DELETE_STATUS_SUCCESS);

		return projectReponse;
	}

	/**
	 * The changeProjectOwner method is used to change/assign owner to project
	 * 
	 * @param projectId
	 * @return
	 * @throws PurpleException
	 */

	@RequestMapping(value = "/api/v1/project/{projectId}/changeowner/{ownerId}", method = RequestMethod.PUT)
	public RestResponse changeProjectOwner(@PathVariable("projectId") String projectId,
			@PathVariable("ownerId") String ownerId) throws PurpleException {

		User newOwner = userService.getUserById(ownerId);
		Project project = projectService.changeProjectOwner(projectId, newOwner);
		ProjectDTO projectMap = dataModelToDTOConversionService.convertProject(project);

		logger.debug("Owner with name '" + newOwner.getName() + "' of project '" + project.getProjectName()
				+ "' is changed successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, projectMap);

		return projectReponse;
	}

	/**
	 * The getUsersOfProject method is used to get all users associated with
	 * project
	 * 
	 * @param projectId
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/project/{projectId}/user", method = RequestMethod.GET)
	public RestResponse getUsersOfProject(@PathVariable("projectId") String projectId) throws PurpleException {

		Set<User> users = projectService.getAllUsersByProject(projectId);

		logger.debug(users.size() + "' users are fetched successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, users, SuccessHandler.GET_USER_SUCCESS);

		return projectReponse;
	}

	/**
	 * The getUsersToAssignProject method is used to get all managers and Admin
	 * role users change owner of project
	 * 
	 * @param projectId
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/assignproject/{projectId}/users", method = RequestMethod.GET)
	public RestResponse getUsersToAssignProject(@PathVariable("projectId") String projectId) throws PurpleException {

		List<User> users = projectService.getUsersToAssignProject(projectId);
		Map<String, Object> usersMap = dataModelToDTOConversionService
				.getResponseMapWithGridDataAndUserStatusCount(users);

		logger.debug(users.size() + "' users are fetched successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, usersMap, SuccessHandler.GET_USER_SUCCESS);

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
	@RequestMapping(value = "/api/v1/project/delete/{projectid}", method = RequestMethod.DELETE)
	public RestResponse deleteProject(@PathVariable("projectid") String projectId) throws PurpleException {

		projectService.deleteProject(projectId);

		logger.debug("Project with Id  '" + projectId + "' is deleted successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, SuccessHandler.DELETE_PROJECT_SUCCESS);

		return projectReponse;
	}

	/**
	 * The get all projects method is used to fetched all projects from tenant
	 * 
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/project", method = RequestMethod.GET)
	public RestResponse getAllProject() throws PurpleException {

		// get loggedIn user object
		User user = userService.getLoggedInUser();

		List<Project> projects = projectService.getAllProjects(user);

		Map<String, Object> projectsMap = dataModelToDTOConversionService
				.getResponseMapWithGridDataAndProjectCount(projects);

		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, projectsMap,
				SuccessHandler.GET_PROJECT_SUCCESS);

		return projectReponse;
	}

	@RequestMapping(value = "/api/v1/project/{projectid}", method = RequestMethod.GET)
	public RestResponse getProject(@PathVariable("projectid") String projectId) throws PurpleException {

		Project project = projectService.getProject(projectId);
		ProjectDTO projectMap = dataModelToDTOConversionService.convertProject(project);

		logger.debug("Project with Id  '" + projectId + "' is getting successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, projectMap,
				SuccessHandler.GET_PROJECT_SUCCESS);

		return projectReponse;
	}
}
