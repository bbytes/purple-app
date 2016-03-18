package com.bbytes.purple.web.controller;

import java.util.List;

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
import com.bbytes.purple.service.ProjectService;
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

	/**
	 * The addUserToProject method is used to add the list of users into project
	 * 
	 * @param projectId
	 * @param projectDTO
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/project//{projectid}/adduser", method = RequestMethod.POST)
	public RestResponse addUsersToProject(@PathVariable("projectid") String projectId,
			@RequestBody ProjectDTO projectDTO) throws PurpleException {

		List<User> users = projectDTO.getUsers();
		Project project = projectService.addUsers(projectId, users);

		logger.debug(users.size() + "' users are added successfully");
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

	@RequestMapping(value = "/api/v1/project//{projectid}/deleteuser", method = RequestMethod.DELETE)
	public RestResponse deleteUsers(@PathVariable("projectid") String projectId, @RequestBody ProjectDTO projectDTO)
			throws PurpleException {

		List<User> users = projectDTO.getUsers();
		Project project = projectService.deleteUsers(projectId, users);

		logger.debug(users.size() + "' users are deleted successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, project,
				SuccessHandler.DELETE_STATUS_SUCCESS);

		return projectReponse;
	}

}
