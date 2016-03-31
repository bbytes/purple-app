package com.bbytes.purple.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.ProjectDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.service.DataModelToDTOConversionService;
import com.bbytes.purple.service.ProjectService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.ErrorHandler;
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
	private UserService userService;

	@Autowired
	private DataModelToDTOConversionService dataModelToDTOConversionService;

	/**
	 * The addUserToProject method is used to add the list of users into project
	 * 
	 * @param projectId
	 * @param projectDTO
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/project/{projectid}/adduser", method = RequestMethod.POST)
	public RestResponse addUsersToProject(@PathVariable("projectid") String projectId,
			@RequestBody ProjectDTO projectDTO) throws PurpleException {

		List<User> usersTobeAdded = new ArrayList<User>();
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

	@RequestMapping(value = "/api/v1/project/{projectid}/deleteuser", method = RequestMethod.DELETE)
	public RestResponse deleteUsers(@PathVariable("projectid") String projectId, @RequestBody List<String> emailList)
			throws PurpleException {

		Project project = projectService.deleteUsers(projectId, emailList);

		logger.debug(project.getUser().size() + "' users are deleted successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, project,
				SuccessHandler.DELETE_STATUS_SUCCESS);

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
	@RequestMapping(value = "/api/v1/project/{projectid}/users", method = RequestMethod.GET)
	public RestResponse getUsersOfProject(@PathVariable("projectid") String projectId) throws PurpleException {

		List<User> users = projectService.getAllUsers(projectId);

		logger.debug(users.size() + "' users are fetched successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, users, SuccessHandler.GET_USER_SUCCESS);

		return projectReponse;
	}

	/**
	 * The getAllProjectsByUser method is used to fetched all projects by logged
	 * in user
	 * 
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/user/allproject", method = RequestMethod.GET)
	public RestResponse getAllProjectsByUser() throws PurpleException {

		User user = userService.getLoggedinUser();
		List<Project> projects = projectService.getProjects(user);
		Map<String, Object> projectsMap = dataModelToDTOConversionService
				.getResponseMapWithGridDataAndProjectList(projects);
		logger.debug("Projects are fetched successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, projectsMap,
				SuccessHandler.GET_PROJECT_SUCCESS);

		return projectReponse;
	}
}
