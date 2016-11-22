package com.bbytes.purple.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Comment;
import com.bbytes.purple.domain.ConfigSetting;
import com.bbytes.purple.domain.Holiday;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.ProjectUserCountStats;
import com.bbytes.purple.domain.Reply;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.TaskItem;
import com.bbytes.purple.domain.TaskList;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.enums.TaskState;
import com.bbytes.purple.rest.dto.models.BaseDTO;
import com.bbytes.purple.rest.dto.models.CommentDTO;
import com.bbytes.purple.rest.dto.models.ConfigSettingResponseDTO;
import com.bbytes.purple.rest.dto.models.HolidayDTO;
import com.bbytes.purple.rest.dto.models.ProjectDTO;
import com.bbytes.purple.rest.dto.models.ProjectUserCountStatsDTO;
import com.bbytes.purple.rest.dto.models.ReplyDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.rest.dto.models.StatusDTO;
import com.bbytes.purple.rest.dto.models.StatusResponseDTO;
import com.bbytes.purple.rest.dto.models.TaskItemDTO;
import com.bbytes.purple.rest.dto.models.TaskListDTO;
import com.bbytes.purple.rest.dto.models.TaskListResponseDTO;
import com.bbytes.purple.rest.dto.models.UserDTO;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.StringUtils;

@Service
public class DataModelToDTOConversionService {

	public static final String JOINED_USERS_COUNT = "joined_count";
	public static final String PENDING_USERS_COUNT = "pending_count";
	public static final String PROJECT_COUNT = "project_count";
	public static final String COMMENT_COUNT = "comment_count";
	public static final String REPLY_COUNT = "reply_count";
	public static final String STATUS_HOURS = "Status Hours";

	public BaseDTO convertToBaseDTO(String value) {
		BaseDTO baseDTO = new BaseDTO();
		baseDTO.setId(value);
		baseDTO.setValue(value);
		return baseDTO;
	}

	public BaseDTO convertToBaseDTOidValue(String value, String id) {
		BaseDTO baseDTO = new BaseDTO();
		baseDTO.setId(id);
		baseDTO.setValue(value);
		return baseDTO;
	}

	public List<BaseDTO> convertRolesToEntityDTOList(List<String> values) {
		List<BaseDTO> baseDTOList = new ArrayList<BaseDTO>();
		for (String value : values) {
			baseDTOList.add(convertToBaseDTO(value));
		}
		return baseDTOList;
	}

	/**
	 * <code>convertRolesToEntityDTOList</code> method returns id and value in
	 * list.
	 * 
	 * @param mapValues
	 * @return
	 */
	public List<BaseDTO> convertRolesToEntityDTOList(Map<String, String> mapValues) {
		List<BaseDTO> baseDTOList = new ArrayList<BaseDTO>();

		for (Map.Entry<String, String> entry : mapValues.entrySet()) {
			baseDTOList.add(convertToBaseDTOidValue(entry.getValue(), entry.getKey()));
		}
		return baseDTOList;
	}

	public List<UserDTO> convertUsers(List<User> users) {
		List<UserDTO> userDTOList = new ArrayList<UserDTO>();
		for (User user : users) {
			userDTOList.add(convertUser(user));
		}
		return userDTOList;
	}

	public UserDTO convertUser(User user) {
		UserDTO userDTO = new UserDTO();
		userDTO.setId(user.getUserId());
		userDTO.setEmail(user.getEmail());
		userDTO.setUserName(user.getName());
		userDTO.setStatus(user.getStatus());
		userDTO.setAccountInitialise(user.isAccountInitialise());
		userDTO.setUserRole(convertToBaseDTO(user.getUserRole().getRoleName()));
		userDTO.setTimePreference(user.getTimePreference());
		userDTO.setEmailNotificationState(user.isEmailNotificationState());
		userDTO.setTimeZone(user.getTimeZone());
		userDTO.setDisableState(user.isDisableState());
		userDTO.setMarkDeleteState(user.isMarkDelete());
		return userDTO;
	}

	public ProjectDTO convertProject(Project project) {

		List<UserDTO> userDTOList = new ArrayList<UserDTO>();
		for (User user : project.getUser()) {
			UserDTO userDTO = new UserDTO();
			userDTO.setEmail(user.getEmail());
			userDTO.setUserName(user.getName());
			userDTOList.add(userDTO);
		}
		ProjectDTO projectDTO = new ProjectDTO();
		projectDTO.setProjectId(project.getProjectId());
		projectDTO.setProjectName(project.getProjectName());
		projectDTO.setUserList(userDTOList);
		projectDTO.setUsersCount(project.getUser().size());
		if (project.getProjectOwner() != null)
			projectDTO.setProjectOwner(project.getProjectOwner().getName());
		return projectDTO;
	}

	public ProjectDTO convertProjectList(Project project) {

		ProjectDTO projectDTO = new ProjectDTO();
		projectDTO.setProjectId(project.getProjectId());
		projectDTO.setProjectName(project.getProjectName());
		return projectDTO;
	}

	public StatusDTO convertStatus(Status status, String statusTime) {

		StatusDTO statusDTO = new StatusDTO();
		statusDTO.setStatusId(status.getStatusId());
		statusDTO.setProjectId(status.getProject().getProjectId());
		statusDTO.setProjectName(status.getProject().getProjectName());
		statusDTO.setUserName(status.getUser().getName());
		statusDTO.setWorkedOn(status.getWorkedOn());
		statusDTO.setWorkingOn(status.getWorkingOn());
		statusDTO.setHours(status.getHours());
		statusDTO.setBlockers(status.getBlockers());
		statusDTO.setTime(statusTime);
		statusDTO.setCommentCount(status.getCommentCount());
		statusDTO.setTaskDataMap(status.getTaskDataMap());
		return statusDTO;
	}

	public CommentDTO convertComment(Comment comment) {
		CommentDTO commentDTO = new CommentDTO();
		commentDTO.setCommentId(comment.getCommentId());
		commentDTO.setCommentDesc(comment.getCommentDesc());
		commentDTO.setUserName(comment.getUser().getName());
		commentDTO.setReplyCount(comment.getReplies().size());
		return commentDTO;
	}

	public ReplyDTO convertReply(Reply reply) {
		ReplyDTO replyDTO = new ReplyDTO();
		replyDTO.setReplyId(reply.getReplyId().toString());
		replyDTO.setUserName(reply.getUser().getName());
		replyDTO.setReplyDesc(reply.getReplyDesc());
		return replyDTO;
	}

	public Map<String, Object> getResponseMapWithGridDataAndUserStatusCount(List<User> users) {
		List<UserDTO> userDTOList = new ArrayList<UserDTO>();
		long joinedCount = 0;
		long pendingCount = 0;
		for (User user : users) {
			if (User.PENDING.equalsIgnoreCase(user.getStatus())) {
				pendingCount++;
			} else if (User.JOINED.equalsIgnoreCase(user.getStatus())) {
				joinedCount++;
			}
			userDTOList.add(convertUser(user));
		}
		return getResponseMapWithGridDataAndUserStatusCount(joinedCount, pendingCount, userDTOList);
	}

	public Map<String, Object> getResponseMapWithGridDataAndComment(List<Comment> comments) {
		List<CommentDTO> commentDTOList = new ArrayList<CommentDTO>();
		long commentCount = 0;
		commentCount = comments.size();
		for (Comment comment : comments) {
			commentDTOList.add(convertComment(comment));
		}
		return getResponseMapWithGridDataAndComment(commentCount, commentDTOList);
	}

	public Map<String, Object> getResponseMapWithGridDataAndReply(Comment comment) {
		List<ReplyDTO> ReplyDTOList = new ArrayList<ReplyDTO>();
		long replyCount = 0;
		replyCount = comment.getReplies().size();
		for (Reply reply : comment.getReplies()) {
			ReplyDTOList.add(convertReply(reply));
		}
		return getResponseMapWithGridDataAndReply(replyCount, ReplyDTOList);
	}

	public ConfigSettingResponseDTO getResponseMapWithGridDataAndNotification(ConfigSetting notificationSetting) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GlobalConstants.DATE_HOLIDAY_FORMAT);
		List<HolidayDTO> holidays = new ArrayList<HolidayDTO>();

		ConfigSettingResponseDTO notificationResponseDTO = new ConfigSettingResponseDTO();
		notificationResponseDTO.setCaptureHours(notificationSetting.isCaptureHours());
		notificationResponseDTO.setWeekendNotification(notificationSetting.isWeekendNotification());
		notificationResponseDTO.setStatusEnable(notificationSetting.getStatusEnable());

		for (Holiday holiday : notificationSetting.getHolidays()) {
			HolidayDTO holidayDTO = new HolidayDTO();
			holidayDTO.setHolidayId(holiday.getHolidayId().toString());
			holidayDTO.setHolidayDate(simpleDateFormat.format(holiday.getHolidayDate()).toString());
			holidays.add(holidayDTO);
		}
		notificationResponseDTO.setHolidayList(holidays);
		return notificationResponseDTO;

	}

	public Map<String, Object> getResponseMapWithGridDataAndProjectCount(List<Project> projects) {
		List<ProjectDTO> projectDTOList = new ArrayList<>();
		long projectCount = 0;
		projectCount = projects.size();
		for (Project project : projects) {
			projectDTOList.add(convertProject(project));
		}
		return getResponseMapWithGridDataAndProjectCount(projectCount, projectDTOList);
	}

	public Map<String, Object> getResponseMapWithGridDataAndProjectList(List<Project> projects) {
		List<ProjectDTO> projectDTOList = new ArrayList<>();
		long projectCount = 0;
		projectCount = projects.size();
		for (Project project : projects) {
			projectDTOList.add(convertProjectList(project));
		}
		return getResponseMapWithGridDataAndProjectCount(projectCount, projectDTOList);
	}

	private Map<String, Object> getResponseMapWithGridDataAndUserStatusCount(long joinedCount, long pendingCount,
			List<UserDTO> gridData) {
		Map<String, Object> responseData = new LinkedHashMap<String, Object>();
		responseData.put(JOINED_USERS_COUNT, joinedCount);
		responseData.put(PENDING_USERS_COUNT, pendingCount);
		responseData.put(RestResponse.GRID_DATA, gridData);
		return responseData;
	}

	private Map<String, Object> getResponseMapWithGridDataAndProjectCount(long projectCount,
			List<ProjectDTO> gridData) {
		Map<String, Object> responseData = new LinkedHashMap<String, Object>();
		responseData.put(PROJECT_COUNT, projectCount);
		responseData.put(RestResponse.GRID_DATA, gridData);
		return responseData;
	}

	private Map<String, Object> getResponseMapWithGridDataAndComment(long commentCount, List<CommentDTO> gridData) {
		Map<String, Object> responseData = new LinkedHashMap<String, Object>();
		responseData.put(COMMENT_COUNT, commentCount);
		responseData.put(RestResponse.GRID_DATA, gridData);
		return responseData;
	}

	private Map<String, Object> getResponseMapWithGridDataAndReply(long replyCount, List<ReplyDTO> gridData) {
		Map<String, Object> responseData = new LinkedHashMap<String, Object>();
		responseData.put(REPLY_COUNT, replyCount);
		responseData.put(RestResponse.GRID_DATA, gridData);
		return responseData;
	}

	public ProjectUserCountStatsDTO getResponseMapWithStatusAnalyticsbyProject(
			List<ProjectUserCountStats> projectUserCountStatsList, String groupBy, String aggrType) {

		ProjectUserCountStatsDTO projectUserCountStatsDTO = new ProjectUserCountStatsDTO();
		List<String> labels = new LinkedList<>();
		List<String> series = new LinkedList<>();

		Map<String, String> mapProjectDateToStatusHrCount = new HashMap<>();
		if (aggrType.equals("day")) {
			for (ProjectUserCountStats projectUsesrCountStats : projectUserCountStatsList) {
				if (!labels.contains(projectUsesrCountStats.getDate()))
					labels.add(projectUsesrCountStats.getDate());

				if (!series.contains(projectUsesrCountStats.getProject().getProjectName()))
					series.add(projectUsesrCountStats.getProject().getProjectName());

				if (groupBy.equals(STATUS_HOURS)) {
					mapProjectDateToStatusHrCount.put(projectUsesrCountStats.getProject().getProjectName() + ":"
							+ projectUsesrCountStats.getDate(), projectUsesrCountStats.getHours().toString());
				} else {
					mapProjectDateToStatusHrCount.put(
							projectUsesrCountStats.getProject().getProjectName() + ":"
									+ projectUsesrCountStats.getDate(),
							projectUsesrCountStats.getStatusCount().toString());
				}
			}
		} else {
			for (ProjectUserCountStats projectUsesrCountStats : projectUserCountStatsList) {
				if (!labels.contains(projectUsesrCountStats.getMonth().toString()))
					labels.add(projectUsesrCountStats.getMonth().toString());

				if (!series.contains(projectUsesrCountStats.getProject().getProjectName()))
					series.add(projectUsesrCountStats.getProject().getProjectName());

				if (groupBy.equals(STATUS_HOURS)) {
					mapProjectDateToStatusHrCount.put(projectUsesrCountStats.getProject().getProjectName() + ":"
							+ projectUsesrCountStats.getMonth(), projectUsesrCountStats.getHours().toString());
				} else {
					mapProjectDateToStatusHrCount.put(
							projectUsesrCountStats.getProject().getProjectName() + ":"
									+ projectUsesrCountStats.getMonth(),
							projectUsesrCountStats.getStatusCount().toString());
				}
			}
		}

		String[][] data = new String[series.size()][labels.size()];
		int i = 0;
		for (String projectName : series) {
			int j = 0;
			for (String date : labels) {
				data[i][j] = mapProjectDateToStatusHrCount.get(projectName + ":" + date);
				j++;
			}
			i++;
		}
		projectUserCountStatsDTO.setData(data);
		projectUserCountStatsDTO.setSeries(series.toArray(new String[series.size()]));

		if (aggrType.equals("month")) {
			labels = convertIntMonthToStringMonthLabels(labels);
		}

		projectUserCountStatsDTO.setLabels(labels.toArray(new String[labels.size()]));

		return projectUserCountStatsDTO;
	}

	public ProjectUserCountStatsDTO getResponseMapWithStatusAnalyticsbyUser(
			List<ProjectUserCountStats> projectUserCountStatsList, String groupBy, String aggrType) {

		ProjectUserCountStatsDTO projectUserCountStatsDTO = new ProjectUserCountStatsDTO();
		List<String> labels = new LinkedList<>();
		List<String> series = new LinkedList<>();

		Map<String, String> mapProjectDateToStatusHrCount = new HashMap<>();

		if (aggrType.equals("day")) {
			for (ProjectUserCountStats projectUsesrCountStats : projectUserCountStatsList) {
				if (!labels.contains(projectUsesrCountStats.getDate()))
					labels.add(projectUsesrCountStats.getDate());

				if (!series.contains(projectUsesrCountStats.getUser().getName()))
					series.add(projectUsesrCountStats.getUser().getName());

				if (groupBy.equals(STATUS_HOURS)) {
					mapProjectDateToStatusHrCount.put(
							projectUsesrCountStats.getUser().getName() + ":" + projectUsesrCountStats.getDate(),
							projectUsesrCountStats.getHours().toString());
				} else {
					mapProjectDateToStatusHrCount.put(
							projectUsesrCountStats.getUser().getName() + ":" + projectUsesrCountStats.getDate(),
							projectUsesrCountStats.getStatusCount().toString());
				}
			}
		} else {
			for (ProjectUserCountStats projectUsesrCountStats : projectUserCountStatsList) {
				if (!labels.contains(projectUsesrCountStats.getMonth().toString()))
					labels.add(projectUsesrCountStats.getMonth().toString());

				if (!series.contains(projectUsesrCountStats.getUser().getName()))
					series.add(projectUsesrCountStats.getUser().getName());

				if (groupBy.equals(STATUS_HOURS)) {
					mapProjectDateToStatusHrCount.put(
							projectUsesrCountStats.getUser().getName() + ":" + projectUsesrCountStats.getMonth(),
							projectUsesrCountStats.getHours().toString());
				} else {
					mapProjectDateToStatusHrCount.put(
							projectUsesrCountStats.getUser().getName() + ":" + projectUsesrCountStats.getMonth(),
							projectUsesrCountStats.getStatusCount().toString());
				}
			}
		}

		String[][] data = new String[series.size()][labels.size()];
		int i = 0;
		for (String userName : series) {
			int j = 0;
			for (String date : labels) {
				data[i][j] = mapProjectDateToStatusHrCount.get(userName + ":" + date);
				j++;
			}
			i++;
		}
		projectUserCountStatsDTO.setData(data);
		projectUserCountStatsDTO.setSeries(series.toArray(new String[series.size()]));

		if (aggrType.equals("month")) {
			labels = convertIntMonthToStringMonthLabels(labels);
		}

		projectUserCountStatsDTO.setLabels(labels.toArray(new String[labels.size()]));

		return projectUserCountStatsDTO;
	}

	private List<String> convertIntMonthToStringMonthLabels(List<String> labels) {
		List<String> monthlabels = new LinkedList<>();
		for (String month : labels) {
			monthlabels.add(getMonthName(Integer.parseInt(month)));
		}

		return monthlabels;
	}

	private String getMonthName(Integer month) {
		return DateTime.now().withMonthOfYear(month).toString("MMM");
	}

	public Map<String, Object> getResponseMapWithGridDataAndStatus(List<Status> statusses, User user)
			throws ParseException {

		String date = null;
		String time = null;
		StatusDTO statusDTO = null;
		Map<String, List<StatusDTO>> statusMap = new LinkedHashMap<String, List<StatusDTO>>();

		for (Status status : statusses) {

			if (user.getTimeZone() != null) {
				Date statuDate = StringUtils.getDateByTimezone(status.getDateTime(), user.getTimeZone());
				date = new SimpleDateFormat(GlobalConstants.DATE_FORMAT).format(statuDate);
				time = new SimpleDateFormat(GlobalConstants.TIME_FORMAT).format(statuDate);
			} else {
				date = new SimpleDateFormat(GlobalConstants.DATE_FORMAT).format(status.getDateTime());
				time = new SimpleDateFormat(GlobalConstants.TIME_FORMAT).format(status.getDateTime());
			}

			if (status.getUser() != null) {
				statusDTO = convertStatus(status, time);
			}

			if (statusMap.containsKey(date)) {
				statusMap.get(date).add(statusDTO);
			} else {
				List<StatusDTO> statusDTOList = new LinkedList<StatusDTO>();
				statusDTOList.add(statusDTO);
				statusMap.put(date, statusDTOList);
			}
		}
		Map<String, Object> responseData = new LinkedHashMap<String, Object>();
		responseData.put(RestResponse.GRID_DATA, getStatusResponse(statusMap));
		return responseData;
	}

	public List<StatusResponseDTO> getStatusResponse(Map<String, List<StatusDTO>> statusMap) {
		List<StatusResponseDTO> statusResponseDTOList = new LinkedList<StatusResponseDTO>();
		Set<String> keys = statusMap.keySet();
		for (String key : keys) {
			StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
			statusResponseDTO.setDate(key);
			statusResponseDTO.setStatusList(statusMap.get(key));
			statusResponseDTOList.add(statusResponseDTO);
		}
		return statusResponseDTOList;
	}

	public List<TaskItemDTO> convertTaskItem(List<TaskItem> taskItems) {
		List<TaskItemDTO> taskItemDTOList = new LinkedList<TaskItemDTO>();
		for (TaskItem item : taskItems) {
			if (item != null) {
				TaskItemDTO itemDTO = new TaskItemDTO();
				itemDTO.setTaskItemId(item.getTaskItemId());
				itemDTO.setDesc(item.getDesc());
				itemDTO.setDueDate(item.getDueDate());
				itemDTO.setEstimatedHours(item.getEstimatedHours());
				itemDTO.setName(item.getName());
				itemDTO.setUsers(convertUsers(new ArrayList<>(item.getUsers())));
				itemDTO.setSpendHours(item.getSpendHours());
				itemDTO.setState(item.getState().getDisplayName());
				taskItemDTOList.add(itemDTO);
			}
		}
		return taskItemDTOList;
	}

	public List<TaskListResponseDTO> convertTaskListItem(List<TaskList> taskList) {
		List<TaskListResponseDTO> taskListDTOList = new LinkedList<TaskListResponseDTO>();
		for (TaskList task : taskList) {
			for (TaskItem item : task.getTaskItems()) {
				if (!TaskState.COMPLETED.equals(item.getState())) {
					TaskListResponseDTO taskListResponseDTO = new TaskListResponseDTO();
					taskListResponseDTO.setTaskItemId(item.getTaskItemId());
					taskListResponseDTO.setTaskListId(task.getTaskListId());
					taskListResponseDTO.setTaskListName(task.getName());
					taskListResponseDTO.setTaskItemName(item.getName());
					taskListResponseDTO.setDesc(item.getDesc());
					taskListResponseDTO.setDueDate(item.getDueDate());
					taskListResponseDTO.setEstimatedHours(item.getEstimatedHours());
					taskListResponseDTO.setSpendHours(item.getSpendHours());
					taskListDTOList.add(taskListResponseDTO);
				}
			}
		}
		return taskListDTOList;

	}

	public TaskItemDTO convertTaskItem(TaskItem taskItem) {
		TaskItemDTO itemDTO = new TaskItemDTO();
		itemDTO.setTaskItemId(taskItem.getTaskItemId());
		itemDTO.setDesc(taskItem.getDesc());
		itemDTO.setDueDate(taskItem.getDueDate());
		itemDTO.setEstimatedHours(taskItem.getEstimatedHours());
		itemDTO.setName(taskItem.getName());
		itemDTO.setUsers(convertUsers(new ArrayList<>(taskItem.getUsers())));
		itemDTO.setSpendHours(taskItem.getSpendHours());
		itemDTO.setState(taskItem.getState().getDisplayName());
		return itemDTO;
	}

	public List<TaskListDTO> convertTaskLists(List<TaskList> taskLists) {
		List<TaskListDTO> taskListDtos = new ArrayList<TaskListDTO>();
		for (TaskList taskList : taskLists) {
			taskListDtos.add(convertTaskList(taskList));
		}
		return taskListDtos;
	}

	public TaskListDTO convertTaskList(TaskList taskList) {
		TaskListDTO taskListDto = new TaskListDTO();
		taskListDto.setName(taskList.getName());
		taskListDto.setTaskListId(taskList.getTaskListId());
		taskListDto.setSpendHours(taskList.getSpendHours());
		taskListDto.setEstimatedHours(taskList.getEstimatedHours());
		taskListDto.setProjectId(taskList.getProject().getProjectId());
		taskListDto.setTaskItems(convertTaskItem(new ArrayList<>(taskList.getTaskItems())));
		return taskListDto;
	}

}
