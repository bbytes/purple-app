package com.bbytes.purple.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.rest.dto.models.UserDTO;
import com.bbytes.purple.service.DataModelToDTOConversionService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.ErrorHandler;
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
	private DataModelToDTOConversionService dataModelToDTOConversionService;

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
