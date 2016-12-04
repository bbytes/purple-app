package com.bbytes.purple.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.bbytes.purple.auth.jwt.TokenAuthenticationProvider;
import com.bbytes.purple.domain.ConfigSetting;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.ProjectUserCountStats;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.StatusTaskEvent;
import com.bbytes.purple.domain.TaskItem;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.enums.TaskState;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.repository.StatusRepository;
import com.bbytes.purple.repository.UserRepository;
import com.bbytes.purple.rest.dto.models.StatusDTO;
import com.bbytes.purple.rest.dto.models.TaskItemDTO;
import com.bbytes.purple.rest.dto.models.UsersAndProjectsDTO;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.GlobalConstants;

@Service
public class StatusService extends AbstractService<Status, String> {

	private StatusRepository statusRepository;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private StatusTaskEventService statusTaskEventService;

	@Autowired
	private UserService userService;

	@Autowired
	protected TokenAuthenticationProvider tokenAuthenticationProvider;

	@Autowired
	private TaskItemService taskItemService;

	@Autowired
	private TaskListService taskListService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private ConfigSettingService configSettingService;

	@Value("${base.url}")
	private String baseUrl;

	@Autowired
	public StatusService(StatusRepository statusRepository) {
		super(statusRepository);
		this.statusRepository = statusRepository;
	}

	public Status getStatusbyId(String statusId) {
		return statusRepository.findOne(statusId);
	}

	public List<Status> getStatusByProject(Project project) {
		return statusRepository.findByProject(project);
	}

	public List<Status> getStatusByUser(User user) {
		return statusRepository.findByUser(user);
	}

	public List<Status> findByProjectAndUser(Project project, User user) {
		return statusRepository.findByProjectAndUser(project, user);
	}

	public boolean statusIdExist(String projectId) {
		boolean state = statusRepository.findOne(projectId) == null ? false : true;
		return state;
	}

	public double findStatusHours(User user, Date dateTime) {
		double hours = 0;
		Date startDate = new DateTime(dateTime).withTime(0, 0, 0, 0).toDate();
		Date endDate = new DateTime(dateTime).withTime(23, 59, 59, 999).toDate();
		List<Status> statusList = statusRepository.findByDateTimeBetweenAndUser(startDate, endDate, user);
		if (statusList == null || statusList.isEmpty())
			return hours;

		for (Status status : statusList) {
			hours = hours + status.getHours();
		}

		return hours;
	}

	public Status create(StatusDTO statusDTO, List<Object> taskItemList, User loggedInUser)
			throws PurpleException, ParseException {
		Status savedStatus = null;
		SimpleDateFormat formatter = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);

		cleanUpStatusText(statusDTO);

		ConfigSetting configSetting = configSettingService.getConfigSetting(loggedInUser.getOrganization());
		String statusEnableDate = configSetting.getStatusEnable();

		if (statusDTO != null && statusDTO.getProjectId() != null && !statusDTO.getProjectId().isEmpty()) {
			if (!projectService.projectIdExist(statusDTO.getProjectId()))
				throw new PurpleException("Error while adding status", ErrorHandler.PROJECT_NOT_FOUND);

			if (statusDTO.getDateTime() == null || statusDTO.getDateTime().isEmpty()) {
				savedStatus = new Status(statusDTO.getWorkingOn(), statusDTO.getWorkedOn(), statusDTO.getHours(),
						new Date());
			} else {
				Date statusDate = formatter.parse(statusDTO.getDateTime());
				Calendar cal = Calendar.getInstance();
				cal.setTime(statusDate);
				Date newTime = new DateTime(new Date())
						.withDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
						.toDate();

				Date backDate = new DateTime(new Date()).minusDays(Integer.parseInt(statusEnableDate))
						.withTime(0, 0, 0, 0).toDate();
				if (statusDate.before(backDate))
					throw new PurpleException("Cannot add status past " + statusEnableDate + " days",
							ErrorHandler.PASS_DUEDATE_STATUS_EDIT);
				if (statusDate.after(new Date()))
					throw new PurpleException("Cannot add status for future date",
							ErrorHandler.FUTURE_DATE_STATUS_EDIT);
				savedStatus = new Status(statusDTO.getWorkingOn(), statusDTO.getWorkedOn(), statusDTO.getHours(),
						newTime);
			}

			Project project = projectService.findByProjectId(statusDTO.getProjectId());

			double hours = findStatusHours(loggedInUser, new Date());
			double newHours = hours + statusDTO.getHours();
			if (newHours > 24)
				throw new PurpleException("Exceeded the status hours", ErrorHandler.HOURS_EXCEEDED);

			savedStatus.setProject(project);
			savedStatus.setUser(loggedInUser);
			savedStatus.addMentionUser(statusDTO.getMentionUser());
			savedStatus.setBlockers(statusDTO.getBlockers());
			savedStatus.setTaskDataMap(statusDTO.getTaskDataMap());
			try {
				savedStatus = statusRepository.save(savedStatus);
				// looping taskDTO to save statusTaskEvent for given spend hours
				// and do the respective calculation
				if (taskItemList != null && !taskItemList.isEmpty()) {
					for (Object taskItemObject : taskItemList) {
						TaskItemDTO taskItemDTO = (TaskItemDTO) taskItemObject;
						TaskItem taskItem = taskItemService.findOne(taskItemDTO.getTaskItemId());
						StatusTaskEvent statusTaskEvent = new StatusTaskEvent(taskItem, savedStatus, loggedInUser);
						statusTaskEvent.setSpendHours(taskItemDTO.getSpendHours());
						statusTaskEvent.setRemainingHours(taskItem.getEstimatedHours() - taskItemDTO.getSpendHours());
						statusTaskEvent.setState(TaskState.IN_PROGRESS);
						statusTaskEventService.save(statusTaskEvent);

					}
				}
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.ADD_STATUS_FAILED, e);
			}
		} else
			throw new PurpleException("Cannot add status with empty project", ErrorHandler.PROJECT_NOT_FOUND);
		return savedStatus;
	}

	/**
	 * Strip Leading and Trailing Spaces From String
	 * 
	 * @param statusDTO
	 */
	private void cleanUpStatusText(StatusDTO statusDTO) {
		if (statusDTO != null) {
			if (statusDTO.getWorkedOn() != null)
				statusDTO.setWorkedOn(statusDTO.getWorkedOn().trim());

			if (statusDTO.getWorkingOn() != null)
				statusDTO.setWorkingOn(statusDTO.getWorkingOn().trim());

			if (statusDTO.getBlockers() != null)
				statusDTO.setBlockers(statusDTO.getBlockers().trim());
		}

	}

	public Status getStatus(String statusId) throws PurpleException {
		Status getStatus = null;
		if (!statusIdExist(statusId))
			throw new PurpleException("Error while getting status", ErrorHandler.STATUS_NOT_FOUND);
		try {
			getStatus = statusRepository.findOne(statusId);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_STATUS_FAILED);
		}
		return getStatus;
	}

	public List<Status> getAllStatus(User user, int timePeriod) throws PurpleException {
		List<Status> statuses = new ArrayList<Status>();
		try {
			Date[] startEndDates = getStartDateEndDate(timePeriod);
			Date startDate = startEndDates[0];
			Date endDate = startEndDates[1];

			statuses = statusRepository.findByDateTimeBetweenAndUser(startDate, endDate, user);
			Collections.sort(statuses, Collections.reverseOrder());

		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_STATUS_FAILED);
		}

		return statuses;
	}

	/**
	 * Return all statues by user
	 * 
	 * @param userId
	 * @return
	 * @throws PurpleException
	 */
	public List<Status> getAllStatusByUserforCSVDownload(String userId) throws PurpleException {
		List<Status> statuses = new ArrayList<Status>();
		try {

			User user = userService.findOne(userId);
			statuses = getStatusByUser(user);
			Collections.sort(statuses, Collections.reverseOrder());

		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_STATUS_FAILED);
		}

		return statuses;
	}

	public void deleteStatus(String statusId) throws PurpleException {
		if (!statusIdExist(statusId))
			throw new PurpleException("Error while deleting status", ErrorHandler.STATUS_NOT_FOUND);
		try {
			Status status = statusRepository.findOne(statusId);
			statusRepository.delete(status);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_STATUS_FAILED);
		}
	}

	public Status updateStatus(String statusId, StatusDTO statusDTO, User loggedInUser) throws PurpleException {

		Status newStatus = null;
		if ((!statusIdExist(statusId) || (statusDTO.getProjectId() == null || statusDTO.getProjectId().isEmpty())))
			throw new PurpleException("Error while updating status", ErrorHandler.STATUS_NOT_FOUND);

		cleanUpStatusText(statusDTO);

		Project project = projectService.findByProjectId(statusDTO.getProjectId());
		Status updateStatus = getStatusbyId(statusId);

		Date statusDate = updateStatus.getDateTime();
		double hours = findStatusHours(loggedInUser, statusDate);
		double newHours = hours + (statusDTO.getHours() - updateStatus.getHours());
		if (newHours > 24)
			throw new PurpleException("Exceeded the status hours", ErrorHandler.HOURS_EXCEEDED);

		updateStatus.setWorkedOn(statusDTO.getWorkedOn());
		updateStatus.setWorkingOn(statusDTO.getWorkingOn());
		updateStatus.setBlockers(statusDTO.getBlockers());
		updateStatus.setHours(statusDTO.getHours());
		updateStatus.setProject(project);
		updateStatus.setMentionUser(statusDTO.getMentionUser());
		updateStatus.setTaskDataMap(statusDTO.getTaskDataMap());
		try {
			newStatus = statusRepository.save(updateStatus);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.UPDATE_STATUS_FAILED, e);
		}

		return newStatus;
	}

	public List<Status> getAllStatusByProjectAndUser(UsersAndProjectsDTO userAndProject, User currentUser,
			Integer timePeriodValue) throws PurpleException {
		List<Status> result = new ArrayList<Status>();
		List<Project> currentUserProjectList = userService.getProjects(currentUser);
		List<String> projectIdStringQueryList = userAndProject.getProjectList();
		List<String> userQueryEmailList = userAndProject.getUserList();
		List<User> userQueryList = userRepository.findByEmailIn(userQueryEmailList);

		Date[] startEndDates = getStartDateEndDate(timePeriodValue);
		Date startDate = startEndDates[0];
		Date endDate = startEndDates[1];

		List<Project> projectQueryList = new ArrayList<>();
		// apply current user project list match filter to requested project
		// list
		if (currentUserProjectList != null) {
			if (!projectIdStringQueryList.isEmpty()) {
				for (Project project : currentUserProjectList) {
					if (projectIdStringQueryList.contains(project.getProjectId())) {
						projectQueryList.add(project);
					}
				}
			} else {
				// if the project list in request empty then the user has
				// selected 'All' option in ui so add all the
				// currentUserProjectList to projectQueryList
				projectQueryList = currentUserProjectList;
			}

		}

		// both empty
		if ((userQueryList == null || userQueryList.isEmpty())
				&& (projectQueryList == null || projectQueryList.isEmpty())) {
			return result;
		}
		// project list empty
		else if (userQueryList != null && !userQueryList.isEmpty()
				&& (projectQueryList == null || projectQueryList.isEmpty())) {
			result = statusRepository.findByDateTimeBetweenAndUserIn(startDate, startDate, userQueryList);
		}
		// user list empty
		else if (projectQueryList != null && !projectQueryList.isEmpty()
				&& (userQueryList == null || userQueryList.isEmpty())) {
			result = statusRepository.findByDateTimeBetweenAndProjectIn(startDate, endDate, projectQueryList);
		}
		// both the list not empty
		else {
			result = statusRepository.findByDateTimeBetweenAndProjectInAndUserIn(startDate, endDate, projectQueryList,
					userQueryList);
		}

		try {

		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_STATUS_FAILED);
		}

		Collections.sort(result, Collections.reverseOrder());

		return result;
	}

	/**
	 * @param noOfDays
	 * @return
	 */
	public Date[] getStartDateEndDate(Integer noOfDays) {
		Date[] startEndDates = new Date[2];

		// start date set
		startEndDates[0] = new DateTime(new Date()).minusDays(noOfDays).withTimeAtStartOfDay().toDate();

		// end date set
		startEndDates[1] = DateTime.now().toDate();

		// in case of yesterday (noOfDays is 1) we set end date as today start
		// datetime midnight 12
		if (noOfDays == 1)
			startEndDates[1] = DateTime.now().withTimeAtStartOfDay().toDate();

		return startEndDates;
	}

	/**
	 * getUserofStatus method is used to pull the users from status.
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Iterable<ProjectUserCountStats> getUserofStatus(Date startDate, Date endDate) {

		TypedAggregation<ProjectUserCountStats> aggregation = newAggregation(ProjectUserCountStats.class,
				match(Criteria.where("dateTime").gte(startDate).lte(endDate)), project().and("user").as("user"));

		AggregationResults<ProjectUserCountStats> result = mongoTemplate.aggregate(aggregation, Status.class,
				ProjectUserCountStats.class);
		return result;
	}

	public Map<String, List<Object>> checkMentionUserAndTask(StatusDTO statusDTO, String statusId, User loggedInUser)
			throws PurpleException {

		Matcher mentionWorkedOnMatcher, taskListWorkedOnMatcher = null;
		Matcher mentionWorkingOnMatcher, taskListWorkingOnMatcher = null;
		Matcher mentionBlockerOnMatcher, taskListBlockerOnMatcher = null;

		Map<String, List<Object>> statusTaskEventMap = new LinkedHashMap<String, List<Object>>();

		String mentionRegexPattern = GlobalConstants.MENTION_REGEX_PATTERN;
		String taskListRegexPattern = GlobalConstants.TASKLIST_REGEX_PATTERN;

		// Create a Pattern object
		Pattern mentionPatternObj = Pattern.compile(mentionRegexPattern);
		Pattern taskListPatternObj = Pattern.compile(taskListRegexPattern);

		Set<String> emailTagList = new LinkedHashSet<String>();

		if (statusDTO.getWorkedOn() != null && !statusDTO.getWorkedOn().isEmpty()) {
			// Now create matcher object for worked on.
			mentionWorkedOnMatcher = mentionPatternObj.matcher(statusDTO.getWorkedOn());
			taskListWorkedOnMatcher = taskListPatternObj.matcher(statusDTO.getWorkedOn());
			// looping all @mention users, adding into emailList and storing
			// into db
			while (mentionWorkedOnMatcher.find()) {
				emailTagList.add(mentionWorkedOnMatcher.group(1));
				User mentionUser = userService.getUserByEmail(mentionWorkedOnMatcher.group(1));
				if (mentionUser != null) {
					statusDTO.addMentionUser(mentionUser);
					// replacing @mention pattern with @username
					String str = statusDTO.getWorkedOn().replaceFirst(GlobalConstants.MENTION_REGEX_PATTERN,
							"<span style='color:#3b73af;font-weight: bold;'>@" + mentionUser.getName() + "</span>")
							.trim();
					statusDTO.setWorkedOn(str);
				}
			}
			// looping all #taskItems
			while (taskListWorkedOnMatcher.find()) {
				TaskItem taskItem = null;
				// getting key from substring before character "-"
				String taskItemKey = StringUtils.substringBefore(taskListWorkedOnMatcher.group(1), "-");
				String taskItemValue = null;
				if (statusDTO.getTaskDataMap() != null && !statusDTO.getTaskDataMap().isEmpty()) {
					// getting value from map by key
					taskItemValue = statusDTO.getTaskDataMap().get("workedOn").get(taskItemKey);
				}
				// getting key from substring after character "id:"
				if (taskItemValue != null && !taskItemValue.isEmpty())
					taskItem = taskItemService.findOne(StringUtils.substringAfter(taskItemValue, "id:"));
				if (taskItem != null) {
					// getting hours from status while entering from user
					double spendHoursOnTaskFromStatus = Double.parseDouble(StringUtils
							.substringAfter(taskListWorkedOnMatcher.group(1), GlobalConstants.HOURS_PATTERN));
					if (statusId == null) {
						// adding taskDTO in map which will use in create status
						// method
						if (statusTaskEventMap.containsKey("taskItem")) {
							TaskItemDTO taskItemDTO = new TaskItemDTO();
							taskItemDTO.setTaskItemId(taskItem.getTaskItemId());
							taskItemDTO.setSpendHours(spendHoursOnTaskFromStatus);
							statusTaskEventMap.get("taskItem").add(taskItemDTO);
						} else {
							List<Object> taskItemList = new LinkedList<>();
							TaskItemDTO taskItemDTO = new TaskItemDTO();
							taskItemDTO.setTaskItemId(taskItem.getTaskItemId());
							taskItemDTO.setSpendHours(spendHoursOnTaskFromStatus);
							taskItemList.add(taskItemDTO);
							statusTaskEventMap.put("taskItem", taskItemList);
						}
						taskItem.addSpendHours(spendHoursOnTaskFromStatus);
						taskItem.getTaskList().addSpendHours(spendHoursOnTaskFromStatus);
					} else {
						taskItem = updateStatusTaskEvent(statusId, spendHoursOnTaskFromStatus, taskItem, loggedInUser);
					}
					taskItem.setState(TaskState.IN_PROGRESS);
					taskItem = taskItemService.save(taskItem);
					taskListService.save(taskItem.getTaskList());
				}
			}
		}
		if (statusDTO.getWorkingOn() != null && !statusDTO.getWorkingOn().isEmpty()) {
			// Now create matcher object working on.
			mentionWorkingOnMatcher = mentionPatternObj.matcher(statusDTO.getWorkingOn());
			taskListWorkingOnMatcher = taskListPatternObj.matcher(statusDTO.getWorkingOn());
			// looping all @mention users, adding into emailList and storing
			// into db
			while (mentionWorkingOnMatcher.find()) {
				emailTagList.add(mentionWorkingOnMatcher.group(1));
				User mentionUser = userService.getUserByEmail(mentionWorkingOnMatcher.group(1));
				if (mentionUser != null) {
					statusDTO.addMentionUser(mentionUser);
					// replacing @mention pattern with @username
					String str = statusDTO.getWorkingOn().replaceFirst(GlobalConstants.MENTION_REGEX_PATTERN,
							"<span style='color:#3b73af;font-weight: bold;'>@" + mentionUser.getName() + "</span>")
							.trim();
					statusDTO.setWorkingOn(str);
				}
			}
			// looping all #taskItems
			while (taskListWorkingOnMatcher.find()) {
				TaskItem taskItem = null;
				// getting key from substring before character "-"
				String taskItemKey = StringUtils.substringBefore(taskListWorkingOnMatcher.group(1), "-");
				String taskItemValue = null;
				if (statusDTO.getTaskDataMap() != null && !statusDTO.getTaskDataMap().isEmpty()) {
					taskItemValue = statusDTO.getTaskDataMap().get("workingOn").get(taskItemKey);
				}
				// getting key from substring after character "id:"
				if (taskItemValue != null && !taskItemValue.isEmpty())
					taskItem = taskItemService.findOne(StringUtils.substringAfter(taskItemValue, "id:"));
				/*
				 * commenting below code since we are not considering the spend
				 * hours here
				 */
				// if (taskItem != null) {
				// taskItem.setSpendHours(Double.parseDouble(StringUtils
				// .substringAfter(taskListWorkingOnMatcher.group(1),
				// GlobalConstants.HOURS_PATTERN)));
				// taskItem = taskItemService.save(taskItem);
				// }
			}

		}
		if (statusDTO.getBlockers() != null && !statusDTO.getBlockers().isEmpty()) {
			// Now create matcher object blockers.
			mentionBlockerOnMatcher = mentionPatternObj.matcher(statusDTO.getBlockers());
			taskListBlockerOnMatcher = taskListPatternObj.matcher(statusDTO.getBlockers());
			// looping all @mention users, adding into emailList and storing
			// into db
			while (mentionBlockerOnMatcher.find()) {
				emailTagList.add(mentionBlockerOnMatcher.group(1));
				User mentionUser = userService.getUserByEmail(mentionBlockerOnMatcher.group(1));
				if (mentionUser != null) {
					statusDTO.addMentionUser(mentionUser);
					// replacing @mention pattern with @username
					String str = statusDTO.getBlockers().replaceFirst(GlobalConstants.MENTION_REGEX_PATTERN,
							"<span style='color:#3b73af;font-weight: bold;'>@" + mentionUser.getName() + "</span>")
							.trim();
					statusDTO.setBlockers(str);
				}
			}
			// looping all #taskItems
			while (taskListBlockerOnMatcher.find()) {
				TaskItem taskItem = null;
				// getting key from substring before character "-"
				String taskItemKey = StringUtils.substringBefore(taskListBlockerOnMatcher.group(1), "-");
				String taskItemValue = null;
				if (statusDTO.getTaskDataMap() != null && !statusDTO.getTaskDataMap().isEmpty()) {
					taskItemValue = statusDTO.getTaskDataMap().get("blockers").get(taskItemKey);
				}
				// getting key from substring after character "id:"
				if (taskItemValue != null && !taskItemValue.isEmpty())
					taskItem = taskItemService.findOne(StringUtils.substringAfter(taskItemValue, "id:"));
				/*
				 * commenting below code since we are not considering the spend
				 * hours here
				 */
				// if (taskItem != null) {
				// taskItem.setSpendHours(Double.parseDouble(StringUtils
				// .substringAfter(taskListBlockerOnMatcher.group(1),
				// GlobalConstants.HOURS_PATTERN)));
				// taskItem = taskItemService.save(taskItem);
				// }
			}
		}
		List<Object> statusList = new ArrayList<>();
		statusList.add(statusDTO);
		statusTaskEventMap.put("status", statusList);

		return statusTaskEventMap;
	}

	/**
	 * This method is basically use for updating statusTaksevent by spend hours
	 * and other doing other calculation and it will return the updated taskItem
	 * Object
	 * 
	 * @param statusId
	 * @param spendHoursOnTaskFromStatus
	 * @param taskItem
	 * @param loggedInUser
	 * @return
	 * @throws PurpleException
	 */
	private TaskItem updateStatusTaskEvent(String statusId, double spendHoursOnTaskFromStatus, TaskItem taskItem,
			User loggedInUser) throws PurpleException {
		if (!statusIdExist(statusId))
			throw new PurpleException("Error while updating status", ErrorHandler.STATUS_NOT_FOUND);
		Status statusFromDb = findOne(statusId);
		double totalSpendHours = 0;
		double totalSpendHoursToTaskList = 0;
		List<StatusTaskEvent> statusTaskEventList = statusTaskEventService.findByTaskItem(taskItem);
		for (StatusTaskEvent statusTaskEvent : statusTaskEventList) {
			totalSpendHours = totalSpendHours + statusTaskEvent.getSpendHours();
		}
		StatusTaskEvent statusTaskEventFromDB = statusTaskEventService.findByStatusAndTaskItem(findOne(statusId),
				taskItem);
		totalSpendHours = totalSpendHours - (statusTaskEventFromDB == null ? 0 : statusTaskEventFromDB.getSpendHours());
		taskItem.setSpendHours(spendHoursOnTaskFromStatus + totalSpendHours);
		totalSpendHoursToTaskList = spendHoursOnTaskFromStatus
				- (statusTaskEventFromDB == null ? 0 : statusTaskEventFromDB.getSpendHours());
		taskItem.getTaskList().addSpendHours(totalSpendHoursToTaskList);
		StatusTaskEvent statusTaskEventByStatusAndTaskItem = statusTaskEventService
				.findByStatusAndTaskItem(statusFromDb, taskItem);
		// checking statusTaskEvent exist by given statusAndTaskItem, if there
		// updating new spend hours and if not then creating new statusTaskEvent
		if (statusTaskEventByStatusAndTaskItem != null) {
			statusTaskEventByStatusAndTaskItem.setSpendHours(spendHoursOnTaskFromStatus);
			statusTaskEventByStatusAndTaskItem
					.setRemainingHours(taskItem.getEstimatedHours() - spendHoursOnTaskFromStatus);
			statusTaskEventService.save(statusTaskEventByStatusAndTaskItem);
		} else {
			StatusTaskEvent statusTaskEvent = new StatusTaskEvent(taskItem, statusFromDb, loggedInUser);
			statusTaskEvent.setSpendHours(spendHoursOnTaskFromStatus);
			statusTaskEvent.setRemainingHours(taskItem.getEstimatedHours() - spendHoursOnTaskFromStatus);
			statusTaskEvent.setState(taskItem.getState());
			statusTaskEventService.save(statusTaskEvent);
		}
		return taskItem;
	}

	/**
	 * Return the snippet url with xAuthToken and statusId along with baseUrl
	 * 
	 * @param statusId
	 * @return
	 */
	public String statusSnippetUrl(Status status, User user) {
		final String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(user.getEmail(), 24);
		String snippetUrl = baseUrl + GlobalConstants.STATUS_SNIPPET_URL + xauthToken + GlobalConstants.STATUS_ID_PARAM
				+ status.getStatusId();
		return snippetUrl;
	}
	
	/**
	 * getDefaulterUsers method is used to pull the all users who didn't fill
	 * the status
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws PurpleException
	 */
	public List<User> getDefaulterUsers(Date startDate, Date endDate) throws PurpleException {

		List<User> allUsers = userService.getAllUsers();
		Iterable<ProjectUserCountStats> result = getUserofStatus(startDate, endDate);
		Set<User> userList = new LinkedHashSet<User>();
		for (Iterator<ProjectUserCountStats> iterator = result.iterator(); iterator.hasNext();) {
			ProjectUserCountStats projectUserCountStats = (ProjectUserCountStats) iterator.next();
			userList.add(projectUserCountStats.getUser());
		}
		allUsers.removeAll(userList);
		return allUsers;
	}

}
