package com.bbytes.purple.web.controller;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.ProjectUserCountStats;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.TimePeriod;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.ProjectUserCountStatsDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.rest.dto.models.StatusDTO;
import com.bbytes.purple.rest.dto.models.UsersAndProjectsDTO;
import com.bbytes.purple.service.DataModelToDTOConversionService;
import com.bbytes.purple.service.ProjectService;
import com.bbytes.purple.service.StatusAnalyticsService;
import com.bbytes.purple.service.StatusService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.service.UtilityService;
import com.bbytes.purple.utils.SuccessHandler;

/**
 * Status Controller
 * 
 * @author akshay
 *
 */
@RestController
public class StatusController {

	private static final Logger logger = LoggerFactory.getLogger(StatusController.class);

	private static final String PROJECT = "Project";

	private static final String USER = "User";

	@Autowired
	private StatusService statusService;

	@Autowired
	private StatusAnalyticsService statusAnalyticsService;

	@Autowired
	private UserService userService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private UtilityService utilityService;

	@Autowired
	private DataModelToDTOConversionService dataModelToDTOConversionService;

	/**
	 * The add status method is used to save the status for project
	 * 
	 * @param statusDTO
	 * @return
	 * @throws PurpleException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/api/v1/status/add", method = RequestMethod.POST)
	public RestResponse addStatus(@RequestBody StatusDTO statusDTO) throws PurpleException, ParseException {

		// We will get current logged in user
		User user = userService.getLoggedInUser();
		Status status = statusService.create(statusDTO, user);
		List<Status> statusList = new ArrayList<Status>();
		statusList.add(status);
		Map<String, Object> statusMap = dataModelToDTOConversionService.getResponseMapWithGridDataAndStatus(statusList,
				user);

		logger.debug("Status for project  '" + status.getProject().getProjectName() + "' is added successfully");
		RestResponse statusReponse = new RestResponse(RestResponse.SUCCESS, statusMap,
				SuccessHandler.ADD_STATUS_SUCCESS);

		return statusReponse;
	}

	/**
	 * The get status method is used to get status for project
	 * 
	 * @param statusId
	 * @return
	 * @throws PurpleException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/api/v1/status/{statusid}", method = RequestMethod.GET)
	public RestResponse getStatus(@PathVariable("statusid") String statusId) throws PurpleException, ParseException {

		// We will get current logged in user
		User user = userService.getLoggedInUser();
		Status status = statusService.getStatus(statusId);
		List<Status> statusList = new ArrayList<Status>();
		statusList.add(status);
		Map<String, Object> statusMap = dataModelToDTOConversionService.getResponseMapWithGridDataAndStatus(statusList,
				user);

		logger.debug("Status for project  '" + status.getProject().getProjectName() + "' is getting successfully");
		RestResponse statusReponse = new RestResponse(RestResponse.SUCCESS, statusMap,
				SuccessHandler.GET_STATUS_SUCCESS);

		return statusReponse;
	}

	/**
	 * The get all status method is used to get all status related to project
	 * 
	 * @return
	 * @throws PurpleException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/api/v1/status", method = RequestMethod.GET)
	public RestResponse getAllStatus(@RequestParam("timePeriod") String timePeriod)
			throws PurpleException, ParseException {

		// We will get current logged in user
		Integer timePeriodValue = TimePeriod.valueOf(timePeriod).getDays();
		User user = userService.getLoggedInUser();
		List<Status> statusList = statusService.getAllStatus(user, timePeriodValue);
		Map<String, Object> statusMap = dataModelToDTOConversionService.getResponseMapWithGridDataAndStatus(statusList,
				user);
		logger.debug("All status are fetched successfully");
		RestResponse statusReponse = new RestResponse(RestResponse.SUCCESS, statusMap,
				SuccessHandler.GET_STATUS_SUCCESS);

		return statusReponse;
	}

	/**
	 * The get csv for status by project and user
	 * 
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/status/project/user/csv", method = RequestMethod.POST, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public FileSystemResource getCSVForStatusByProjectAndUser(@RequestBody UsersAndProjectsDTO usersAndProjectsDTO,
			@RequestParam("timePeriod") String timePeriod, HttpServletResponse response) throws PurpleException {

		// We will get current logged in user
		User user = userService.getLoggedInUser();
		Integer timePeriodValue = TimePeriod.valueOf(timePeriod).getDays();
		List<Status> statusList = statusService.getAllStatusByProjectAndUser(usersAndProjectsDTO, user,
				timePeriodValue);
		response.setContentType("text/csv");
		String csvFileName = "status" + "_" + timePeriodValue + "_" + DateTime.now().toString("yyyy-MM-dd HH-mm-ss")
				+ ".csv";
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", csvFileName));
		response.setHeader("purple-file-name", csvFileName);

		File csv = utilityService.getCSV(csvFileName, statusList);
		return new FileSystemResource(csv);
	}

	/**
	 * The get csv for all status related to project for current user
	 * 
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/status/csv", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public FileSystemResource getCSVForAllStatus(@RequestParam("timePeriod") String timePeriod,
			HttpServletResponse response) throws PurpleException {

		// We will get current logged in user
		Integer timePeriodValue = TimePeriod.valueOf(timePeriod).getDays();
		User user = userService.getLoggedInUser();
		List<Status> statusList = statusService.getAllStatus(user, timePeriodValue);
		response.setContentType("text/csv");
		String csvFileName = "status" + "_" + timePeriodValue + "_" + DateTime.now().toString("yyyy-MM-dd HH-mm-ss")
				+ ".csv";
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", csvFileName));
		response.setHeader("purple-file-name", csvFileName);

		File csv = utilityService.getCSV(csvFileName, statusList);
		return new FileSystemResource(csv);
	}

	/**
	 * The delete status method is used to delete particular status
	 * 
	 * @param statusId
	 * @param request
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/status/{statusid}", method = RequestMethod.DELETE)
	public RestResponse deleteStatus(@PathVariable("statusid") String statusId) throws PurpleException {
		final String DELETE_STATUS_SUCCESS_MSG = "Successfully deleted status";

		statusService.deleteStatus(statusId);

		logger.debug("Status with status id  '" + statusId + "' is deleted successfully");
		RestResponse statusReponse = new RestResponse(RestResponse.SUCCESS, DELETE_STATUS_SUCCESS_MSG,
				SuccessHandler.DELETE_STATUS_SUCCESS);

		return statusReponse;
	}

	/**
	 * The updateStatus method is used to update the status for particular
	 * project
	 * 
	 * @param statusId
	 * @param statusDTO
	 * @return
	 * @throws PurpleException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/api/v1/status/update/{statusid}", method = RequestMethod.PUT)
	public RestResponse updateStatus(@PathVariable("statusid") String statusId, @RequestBody StatusDTO statusDTO)
			throws PurpleException, ParseException {

		// We will get current logged in user
		User user = userService.getLoggedInUser();
		Status status = statusService.updateStatus(statusId, statusDTO, user);
		List<Status> statusList = new ArrayList<Status>();
		statusList.add(status);
		Map<String, Object> statusMap = dataModelToDTOConversionService.getResponseMapWithGridDataAndStatus(statusList,
				user);

		logger.debug("Projects are fetched successfully");
		RestResponse statusReponse = new RestResponse(RestResponse.SUCCESS, statusMap,
				SuccessHandler.UPDATE_STATUS_SUCCESS);

		return statusReponse;
	}

	/**
	 * The get all status method is used to get all status related to project
	 * 
	 * @return
	 * @throws PurpleException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/api/v1/status/project/user", method = RequestMethod.POST)
	public RestResponse getAllStatusByProjectAndUser(@RequestBody UsersAndProjectsDTO usersAndProjectsDTO,
			@RequestParam("timePeriod") String timePeriod) throws PurpleException, ParseException {

		User user = userService.getLoggedInUser();
		Integer timePeriodValue = TimePeriod.valueOf(timePeriod).getDays();
		List<Status> statusList = statusService.getAllStatusByProjectAndUser(usersAndProjectsDTO, user,
				timePeriodValue);
		Map<String, Object> statusMap = dataModelToDTOConversionService.getResponseMapWithGridDataAndStatus(statusList,
				user);
		logger.debug("All status are fetched successfully");
		RestResponse statusReponse = new RestResponse(RestResponse.SUCCESS, statusMap,
				SuccessHandler.GET_STATUS_SUCCESS);

		return statusReponse;
	}

	/**
	 * The get status analytics for project and user.
	 * 
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "api/v1/status/analytics", method = RequestMethod.POST)
	public RestResponse getStatusAnalytics(@RequestBody UsersAndProjectsDTO usersAndProjectsDTO,
			@RequestParam("timePeriod") String timePeriod) throws PurpleException {

		User user = userService.getLoggedInUser();
		Integer timePeriodValue = TimePeriod.valueOf(timePeriod).getDays();
		String aggrType = TimePeriod.valueOf(timePeriod).getAggrType();

		Date[] startEndDates = statusService.getStartDateEndDate(timePeriodValue);
		Date startDate = startEndDates[0];
		Date endDate = startEndDates[1];
		ProjectUserCountStatsDTO projectUserCountStatsDTO = null;

		Set<User> users = userService.getUsersbyProjects(user.getProjects());
		List<User> allUsers = new ArrayList<User>();
		allUsers.addAll(users);

		Iterable<ProjectUserCountStats> result = null;

		if (usersAndProjectsDTO.getProjectList().isEmpty() && usersAndProjectsDTO.getProjectUser().equals(PROJECT)) {
			if (aggrType.equals("day")) {
				result = statusAnalyticsService.getProjectPerDayCountHours(user.getProjects(), startDate, endDate);
			} else {
				result = statusAnalyticsService.getProjectPerMonthCountHours(user.getProjects(), startDate, endDate);
			}
			List<ProjectUserCountStats> statusAnalyticsList = new ArrayList<ProjectUserCountStats>();
			if (result != null) {
				for (Iterator<ProjectUserCountStats> iterator = result.iterator(); iterator.hasNext();) {
					ProjectUserCountStats projectUserCountStats = (ProjectUserCountStats) iterator.next();
					statusAnalyticsList.add(projectUserCountStats);
				}
			}
			projectUserCountStatsDTO = dataModelToDTOConversionService.getResponseMapWithStatusAnalyticsbyProject(
					statusAnalyticsList, usersAndProjectsDTO.getCountHours(), aggrType);
		} else if (usersAndProjectsDTO.getUserList().isEmpty() && usersAndProjectsDTO.getProjectUser().equals(USER)) {

			if (aggrType.equals("day")) {
				result = statusAnalyticsService.getUserPerDayCountHours(allUsers, startDate, endDate);
			} else {
				result = statusAnalyticsService.getUserPerMonthCountHours(allUsers, startDate, endDate);
			}

			List<ProjectUserCountStats> statusAnalyticsList = new ArrayList<ProjectUserCountStats>();
			if (result != null) {
				for (Iterator<ProjectUserCountStats> iterator = result.iterator(); iterator.hasNext();) {
					ProjectUserCountStats projectUserCountStats = (ProjectUserCountStats) iterator.next();
					statusAnalyticsList.add(projectUserCountStats);
				}
			}
			projectUserCountStatsDTO = dataModelToDTOConversionService.getResponseMapWithStatusAnalyticsbyUser(
					statusAnalyticsList, usersAndProjectsDTO.getCountHours(), aggrType);
		} else if (usersAndProjectsDTO.getProjectList().get(0) != null) {

			Project project = projectService.findByProjectId(usersAndProjectsDTO.getProjectList().get(0));
			List<Project> projectList = new ArrayList<Project>();
			projectList.add(project);

			if (aggrType.equals("day")) {
				result = statusAnalyticsService.getProjectPerDayCountHours(projectList, startDate, endDate);
			} else {
				result = statusAnalyticsService.getProjectPerMonthCountHours(projectList, startDate, endDate);
			}

			List<ProjectUserCountStats> statusAnalyticsList = new ArrayList<ProjectUserCountStats>();
			if (result != null) {
				for (Iterator<ProjectUserCountStats> iterator = result.iterator(); iterator.hasNext();) {
					ProjectUserCountStats projectUserCountStats = (ProjectUserCountStats) iterator.next();
					statusAnalyticsList.add(projectUserCountStats);
				}
			}
			projectUserCountStatsDTO = dataModelToDTOConversionService.getResponseMapWithStatusAnalyticsbyProject(
					statusAnalyticsList, usersAndProjectsDTO.getCountHours(), aggrType);
		} else {

			User getUser = userService.getUserByEmail(usersAndProjectsDTO.getUserList().get(0));
			List<User> userList = new ArrayList<User>();
			userList.add(getUser);

			if (aggrType.equals("day")) {
				result = statusAnalyticsService.getUserPerDayCountHours(userList, startDate, endDate);
			} else {
				result = statusAnalyticsService.getUserPerMonthCountHours(userList, startDate, endDate);
			}
			List<ProjectUserCountStats> statusAnalyticsList = new ArrayList<ProjectUserCountStats>();
			if (result != null) {
				for (Iterator<ProjectUserCountStats> iterator = result.iterator(); iterator.hasNext();) {
					ProjectUserCountStats projectUserCountStats = (ProjectUserCountStats) iterator.next();
					statusAnalyticsList.add(projectUserCountStats);
				}
			}
			projectUserCountStatsDTO = dataModelToDTOConversionService.getResponseMapWithStatusAnalyticsbyUser(
					statusAnalyticsList, usersAndProjectsDTO.getCountHours(), aggrType);
		}
		logger.debug("All Status Analytics are fetched successfully");
		RestResponse statusReponse = new RestResponse(RestResponse.SUCCESS, projectUserCountStatsDTO,
				SuccessHandler.GET_STATUS_ANALYTICS_SUCCESS);

		return statusReponse;
	}

}
