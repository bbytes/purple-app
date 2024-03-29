package com.bbytes.purple.web.controller;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.bbytes.purple.domain.User;
import com.bbytes.purple.enums.TimePeriod;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.ProjectUserCountStatsDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.rest.dto.models.StatusDTO;
import com.bbytes.purple.rest.dto.models.UsersAndProjectsDTO;
import com.bbytes.purple.service.DataModelToDTOConversionService;
import com.bbytes.purple.service.NotificationService;
import com.bbytes.purple.service.ProjectService;
import com.bbytes.purple.service.StatusAnalyticsService;
import com.bbytes.purple.service.StatusService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.service.UtilityService;
import com.bbytes.purple.utils.GlobalConstants;
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

	@Autowired
	private NotificationService notificationService;

	@Value("${email.tag.subject}")
	private String tagSubject;

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
		User loggedInUser = userService.getLoggedInUser();
		Map<String, List<Object>> statusTaskEventMap = statusService.checkMentionUserAndTask(statusDTO, null,
				loggedInUser);
		StatusDTO updatedStatusDTO = (StatusDTO) statusTaskEventMap.get("status").iterator().next();
		List<Object> taskItemList = statusTaskEventMap.get("taskItem");

		Status status = statusService.create(updatedStatusDTO, taskItemList, loggedInUser);

		notifyMentionUsers(loggedInUser, status);

		List<Status> statusList = new ArrayList<Status>();
		statusList.add(status);
		Map<String, Object> statusMap = dataModelToDTOConversionService.getResponseMapWithGridDataAndStatus(statusList,
				loggedInUser);

		logger.debug("Status for project  '" + status.getProject().getProjectName() + "' is added successfully");
		RestResponse statusReponse = new RestResponse(RestResponse.SUCCESS, statusMap,
				SuccessHandler.ADD_STATUS_SUCCESS);

		return statusReponse;
	}

	private void notifyMentionUsers(User loggedInUser, Status status) {
		// sending email to all tagged users with status details
		List<String> emailList = new ArrayList<String>();

		for (User mentionUser : status.getMentionUser()) {
			emailList.add(mentionUser.getEmail());
		}

		final String template = GlobalConstants.MENTION_EMAIL_TEMPLATE;
		final String subject = loggedInUser.getName() + " " + tagSubject;
		DateFormat dateFormat = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);
		String postDate = dateFormat.format(status.getDateTime());

		Map<String, Object> emailBody = new HashMap<>();
		emailBody.put(GlobalConstants.USER_NAME, loggedInUser.getName());
		emailBody.put(GlobalConstants.SUBSCRIPTION_DATE, postDate);
		emailBody.put(GlobalConstants.WORKED_ON, status.getWorkedOn() == null ? "" : status.getWorkedOn());
		emailBody.put(GlobalConstants.WORKING_ON, status.getWorkingOn() == null ? "" : status.getWorkingOn());
		emailBody.put(GlobalConstants.BLOCKERS, status.getBlockers() == null ? "" : status.getBlockers());

		for (User mentionUser : status.getMentionUser()) {
			notificationService.sendSlackMessage(mentionUser,
					"@" + loggedInUser.getName() + " mentioned you in Statusnap status",
					statusService.statusSnippetUrl(status, mentionUser));
		}

		if (emailList != null && !emailList.isEmpty()) {
			notificationService.sendTemplateEmail(emailList, subject, template, emailBody);
		}

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
	public FileSystemResource getCSVForAllStatus(
			@RequestParam(value = "timePeriod", required = false) String timePeriod,
			@RequestParam(value = "userId", required = false) String userId, HttpServletResponse response)
			throws PurpleException {

		Integer timePeriodValue = null;
		List<Status> statusList = null;
		if (userId != null && !userId.isEmpty()) {
			statusList = statusService.getAllStatusByUserforCSVDownload(userId);
		} else {
			// We will get current logged in user
			User user = userService.getLoggedInUser();
			timePeriodValue = TimePeriod.valueOf(timePeriod).getDays();
			statusList = statusService.getAllStatus(user, timePeriodValue);
		}

		response.setContentType("text/csv");
		String csvFileName = "status" + "_" + DateTime.now().toString("yyyy-MM-dd HH-mm-ss") + ".csv";
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
		User loggedInUser = userService.getLoggedInUser();
		Map<String, List<Object>> statusTaskEventMap = statusService.checkMentionUserAndTask(statusDTO, statusId,
				loggedInUser);
		StatusDTO updatedStatusDTO = (StatusDTO) statusTaskEventMap.get("status").iterator().next();
		Status status = statusService.updateStatus(statusId, updatedStatusDTO, loggedInUser);

		notifyMentionUsers(loggedInUser, status);

		List<Status> statusList = new ArrayList<Status>();
		statusList.add(status);
		Map<String, Object> statusMap = dataModelToDTOConversionService.getResponseMapWithGridDataAndStatus(statusList,
				loggedInUser);

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

		List<Project> projectOfUser = userService.getProjects(user);
		Set<User> users = userService.getUsersbyProjects(new HashSet<Project>(projectOfUser));
		List<User> allUsers = new ArrayList<User>();
		allUsers.addAll(users);

		Iterable<ProjectUserCountStats> result = null;

		if (usersAndProjectsDTO.getProjectList().isEmpty() && usersAndProjectsDTO.getProjectUser().equals(PROJECT)) {
			if (aggrType.equals("day")) {
				result = statusAnalyticsService.getProjectPerDayCountHours(new HashSet<Project>(projectOfUser),
						startDate, endDate);
			} else {
				result = statusAnalyticsService.getProjectPerMonthCountHours(new HashSet<Project>(projectOfUser),
						startDate, endDate);
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
			Set<Project> projectList = new HashSet<Project>();
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
