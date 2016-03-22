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

import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.ProjectDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.rest.dto.models.UserDTO;
import com.bbytes.purple.service.AdminService;
import com.bbytes.purple.service.DataModelToDTOConversionService;
import com.bbytes.purple.service.OrganizationService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.SuccessHandler;
import com.bbytes.purple.utils.TenancyContextHolder;

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
	private OrganizationService organizationService;

	@Autowired
	private UserService userService;

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

		Organization org = organizationService.findByOrgId(TenancyContextHolder.getTenant());
		User addUser = new User(userDTO.getUserName(), userDTO.getEmail());
		addUser.setOrganization(org);
		addUser.setStatus(User.PENDING);

		User user = adminService.addUsers(addUser);
		UserDTO responseDTO = dataModelToDTOConversionService.convertUser(user);
		logger.debug("User with email  '" + userDTO.getEmail() + "' are added successfully");
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
	public RestResponse getUsers() throws PurpleException {

		List<User> users = adminService.getAllUsers();
		Map<String, Object> usersMap = dataModelToDTOConversionService
				.getResponseMapWithGridDataAndUserStatusCount(users);
		logger.debug("Users are fetched successfully");
		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, usersMap, SuccessHandler.GET_PROJECT_SUCCESS);

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
		Organization org = organizationService.findByOrgId(TenancyContextHolder.getTenant());
		Project addProject = new Project(projectDTO.getProjectName(), projectDTO.getTimePreference());
		addProject.setOrganization(org);
		List<User> usersTobeAdded = new ArrayList<User>();
		for (String i : projectDTO.getUsers()) {
			usersTobeAdded.add(userService.getUserByEmail(i));
		}
		addProject.setUser(usersTobeAdded);
		Project project = adminService.createProject(addProject);

		logger.debug("User with email  '" + projectDTO.getProjectName() + "' are added successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, project,
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

		logger.debug("Projects are fetched successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, project,
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
		Organization org = organizationService.findByOrgId(TenancyContextHolder.getTenant());
		Project updateProject = new Project(projectDTO.getProjectName(), projectDTO.getTimePreference());
		updateProject.setOrganization(org);
		List<User> usersTobeAdded = new ArrayList<User>();
		for (String i : projectDTO.getUsers()) {
			usersTobeAdded.add(userService.getUserByEmail(i));
		}
		updateProject.setUser(usersTobeAdded);

		Project projects = adminService.updateProject(projectId, updateProject);

		logger.debug("Projects are updated successfully");
		RestResponse projectReponse = new RestResponse(RestResponse.SUCCESS, projects,
				SuccessHandler.UPDATE_PROJECT_SUCCESS);

		return projectReponse;
	}
}
