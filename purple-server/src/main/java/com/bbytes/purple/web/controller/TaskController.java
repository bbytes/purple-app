package com.bbytes.purple.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ApplicationPath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.TaskItem;
import com.bbytes.purple.domain.TaskList;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.enums.TaskState;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.rest.dto.models.TaskItemDTO;
import com.bbytes.purple.rest.dto.models.TaskListDTO;
import com.bbytes.purple.service.DataModelToDTOConversionService;
import com.bbytes.purple.service.ProjectService;
import com.bbytes.purple.service.TaskItemService;
import com.bbytes.purple.service.TaskListService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.SuccessHandler;

@RestController
public class TaskController {
	private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

	@Autowired
	private TaskListService taskListService;

	@Autowired
	private TaskItemService taskItemService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private UserService userService;

	@Autowired
	private DataModelToDTOConversionService dataModelToDTOConversionService;

	@RequestMapping(value = "/api/v1/tasklist/{state}", method = RequestMethod.GET)
	public RestResponse getTaskListForState(@PathVariable String state) throws PurpleException {
		TaskState taskState = TaskState.valueOf(state);
		User user = userService.getLoggedInUser();
		RestResponse response = new RestResponse(RestResponse.SUCCESS, taskListService.findByStateAndUsers(taskState, user));
		return response;
	}

	@RequestMapping(value = "/api/v1/tasklist/{projectId}", method = RequestMethod.GET)
	public RestResponse getTaskListForProject(@PathVariable String projectId) throws PurpleException {
		Project project = projectService.findOne(projectId);
		User user = userService.getLoggedInUser();
		RestResponse response = new RestResponse(RestResponse.SUCCESS, taskListService.findByProjectAndUsers(project, user));
		return response;
	}

	@RequestMapping(value = "/api/v1/tasklist/{projectId}/{state}", method = RequestMethod.GET)
	public RestResponse getTaskListForProjectAndState(@PathVariable String projectId, @PathVariable String state) throws PurpleException {
		Project project = projectService.findOne(projectId);
		User user = userService.getLoggedInUser();
		TaskState taskState = TaskState.valueOf(state);
		RestResponse response = new RestResponse(RestResponse.SUCCESS,
				taskListService.findByProjectAndStateAndUsers(project, taskState, user));
		return response;
	}

	@RequestMapping(value = "/api/v1/tasklist/create", method = RequestMethod.POST)
	public RestResponse saveTaskList(@RequestBody TaskListDTO taskListDTO) throws PurpleException {

		TaskList taskList = null;

		if (taskListDTO.getTaskListId() != null) {
			taskList = taskListService.findOne(taskListDTO.getTaskListId());
		}

		if (taskList == null) {
			taskList = new TaskList(taskListDTO.getName());
			User user = userService.getLoggedInUser();
			taskList.setOwner(user);
		}

		taskList.setName(taskListDTO.getName());
		Project project = projectService.findOne(taskListDTO.getProjectId());

		if (project == null)
			throw new PurpleException("Project with id " + taskListDTO.getProjectId() + " not found", ErrorHandler.PROJECT_NOT_FOUND);

		taskList.setProject(project);
		taskList = taskListService.save(taskList);

		logger.debug("Task list with name '" + taskList.getName() + "' added successfully");
		RestResponse response = new RestResponse(RestResponse.SUCCESS, taskList, SuccessHandler.ADD_TASK_LIST_SUCCESS);

		return response;
	}

	@RequestMapping(value = "/api/v1/tasklist/{taskListId}", method = RequestMethod.DELETE)
	public RestResponse deleteTaskList(@PathVariable String taskListId) throws PurpleException {

		taskListService.delete(taskListId);

		logger.debug("Task list with id '" + taskListId + "' deleted successfully");
		RestResponse response = new RestResponse(RestResponse.SUCCESS, "Task list with id '" + taskListId + "' deleted successfully");

		return response;
	}

	@RequestMapping(value = "/api/v1/taskitem/{taskItemId}", method = RequestMethod.DELETE)
	public RestResponse deleteTaskItem(@PathVariable String taskItemId) throws PurpleException {

		taskItemService.delete(taskItemId);

		logger.debug("Task Item with id '" + taskItemId + "' deleted successfully");
		RestResponse response = new RestResponse(RestResponse.SUCCESS, "Task Item with id '" + taskItemId + "' deleted successfully");

		return response;
	}

	@RequestMapping(value = "/api/v1/taskitem/{taskListId}", method = RequestMethod.POST)
	public RestResponse addTaskItem(@PathVariable String taskListId, @RequestBody TaskItemDTO taskItemDTO) throws PurpleException {

		TaskItem taskItem = saveTaskItem(taskListId, taskItemDTO);
		RestResponse response = new RestResponse(RestResponse.SUCCESS, taskItem, SuccessHandler.ADD_TASK_ITEM_SUCCESS);
		return response;
	}

	@RequestMapping(value = "/api/v1/taskitems/{taskListId}", method = RequestMethod.POST)
	public RestResponse addTaskItems(@PathVariable String taskListId, @RequestBody List<TaskItemDTO> taskListDTOs) throws PurpleException {

		List<TaskItem> taskItems = new ArrayList<>();

		for (TaskItemDTO taskItemDTO : taskListDTOs) {
			TaskItem taskItem = saveTaskItem(taskListId, taskItemDTO);
			taskItems.add(taskItem);
		}

		RestResponse response = new RestResponse(RestResponse.SUCCESS, taskItems, SuccessHandler.ADD_TASK_ITEM_SUCCESS);
		return response;
	}

	private TaskItem saveTaskItem(String taskListId, TaskItemDTO taskItemDTO) throws PurpleException {
		TaskList taskList = taskListService.findOne(taskListId);
		if (taskList == null)
			throw new PurpleException("Task List with id " + taskListId + " not found", ErrorHandler.TASK_LIST_NOT_FOUND);

		TaskItem taskItem = new TaskItem(taskList, taskItemDTO.getName(), taskItemDTO.getDesc(), taskItemDTO.getEstimatedHours(),
				taskItemDTO.getDueDate());
		taskItem.setProject(taskList.getProject());
		User user = userService.getLoggedInUser();
		taskItem.setOwner(user);

		taskItem = taskItemService.save(taskItem);

		logger.debug("Task item with name '" + taskItem.getName() + "' added successfully");
		return taskItem;
	}

}
