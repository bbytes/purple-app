package com.bbytes.purple.web.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bbytes.purple.PurpleWebBaseApplicationTests;
import com.bbytes.purple.domain.Comment;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.TaskItem;
import com.bbytes.purple.domain.TaskList;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.enums.TaskState;
import com.bbytes.purple.rest.dto.models.TaskItemDTO;
import com.bbytes.purple.rest.dto.models.TaskListDTO;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.TenancyContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;


public class TestTaskController extends PurpleWebBaseApplicationTests {

	Comment comment;
	Organization org;
	Status status;
	User user;
	Project project;

	@Before
	public void setUp() {

		mockMvc = MockMvcBuilders.webAppContextSetup(context).dispatchOptions(true).addFilters(filterChainProxy).build();

		org = new Organization("Accenture", "Accenture IT Services");
		project = new Project("purple");
		project.setOrganization(org);
		status = new Status("purple", "reveal", 40, new Date());

		TenancyContextHolder.setTenant(org.getOrgId());

		organizationRepository.save(org);
		user = new User("aditya", "aditya@bbytes.co.in");
		user.setOrganization(org);
		user = userRepository.save(user);
		project =projectService.save(project);
		status.setUser(user);

		userService.updatePassword("test123", user);
		status = statusService.save(status);

	}

	@After
	public void cleanUp() {
		organizationRepository.deleteAll();
		commentRepository.deleteAll();
		projectRepository.deleteAll();
		statusRepository.deleteAll();
		userRepository.deleteAll();
		taskListRepository.deleteAll();
		taskItemRepository.deleteAll();
	}

	@Test
	public void testGetTaskStates() throws Exception {
		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(user.getEmail(), 1);
		mockMvc.perform(get("/api/v1/task/taskStates").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());
	}
	
	@Test
	public void testGetTaskItemByStates() throws Exception {
		TaskList taskList = new TaskList("test sample");
		taskList.setOwner(user);
		taskList.setProject(project);
		taskList = taskListRepository.save(taskList);
		
		TaskItem taskItem1 = new TaskItem(taskList, "test", "test desc", 20, DateTime.now().plusDays(2).toDate());
		TaskItem taskItem2 = new TaskItem(taskList, "test2", "test desc2", 10, DateTime.now().plusDays(2).toDate());
		taskItem1.setOwner(user);
		taskItem2.setOwner(user);
		taskList.addTaskItem(taskItem1);
		taskList.addTaskItem(taskItem2);
		taskItemRepository.save(taskItem1);
		taskItemRepository.save(taskItem2);
		
		taskList = taskListRepository.save(taskList);
		
		
		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(user.getEmail(), 1);
		mockMvc.perform(get("/api/v1/task/taskList/state/"+TaskState.YET_TO_START.toString()).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());
	}

	
	/**
	 * TestCase for Save Comment
	 */

	@Test
	public void testSaveTaskList() throws Exception {

		TaskListDTO taskListDTO = new TaskListDTO();
		taskListDTO.setName("Test task list");
		taskListDTO.setProjectId(project.getProjectId());

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(taskListDTO);
		System.out.println(requestJson);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(user.getEmail(), 1);
		mockMvc.perform(post("/api/v1/task/taskList").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	@Test
	public void testSaveTaskItems() throws Exception {

		TaskListDTO taskListDTO = new TaskListDTO();
		taskListDTO.setName("Test task list");
		taskListDTO.setProjectId(project.getProjectId());

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(taskListDTO);
		
		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(user.getEmail(), 1);
		mockMvc.perform(post("/api/v1/task/taskList/").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

		TaskList taskList = taskListRepository.findAll().get(0);
		TaskItemDTO taskItemDTO = new TaskItemDTO();
		taskItemDTO.setName("sample task item");
		taskItemDTO.setDesc("big text shouldb ecome here with lot of data");
		taskItemDTO.setDueDate(DateTime.now().plusDays(2).toDate());
		taskItemDTO.setEstimatedHours(25);

		requestJson = ow.writeValueAsString(taskItemDTO);
		System.out.println(requestJson);


		mockMvc.perform(post("/api/v1/task/taskItem/" + taskList.getTaskListId()).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	@Test
	public void testDeleteTaskList() throws Exception {

		TaskListDTO taskListDTO = new TaskListDTO();
		taskListDTO.setName("Test task list");
		taskListDTO.setProjectId(project.getProjectId());

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(taskListDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(user.getEmail(), 1);
		mockMvc.perform(post("/api/v1/task/taskList/").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

		
		TaskList taskList = taskListRepository.findAll().get(0);

		mockMvc.perform(delete("/api/v1/task/taskList/" + taskList.getTaskListId()).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
				.andExpect(status().isOk()).andDo(print()).andExpect(content().string(containsString("{\"success\":true")))
				.andExpect(status().isOk());

	}
	
	@Test
	public void testDeleteTaskItem() throws Exception {

		TaskListDTO taskListDTO = new TaskListDTO();
		taskListDTO.setName("Test task list");
		taskListDTO.setProjectId(project.getProjectId());

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(taskListDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(user.getEmail(), 1);
		mockMvc.perform(post("/api/v1/task/taskList").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

		TaskList taskList = taskListRepository.findAll().get(0);
		TaskItemDTO taskItemDTO = new TaskItemDTO();
		taskItemDTO.setName("sample task item");
		taskItemDTO.setDesc("big text shouldb ecome here with lot of data");
		taskItemDTO.setDueDate(DateTime.now().plusDays(2).toDate());
		taskItemDTO.setEstimatedHours(25);

		requestJson = ow.writeValueAsString(taskItemDTO);

		mockMvc.perform(post("/api/v1/task/taskItem/" + taskList.getTaskListId()).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

		
		TaskItem taskItem = taskItemRepository.findAll().get(0);

		mockMvc.perform(delete("/api/v1/task/taskItem/" + taskItem.getTaskItemId()).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
				.andExpect(status().isOk()).andDo(print()).andExpect(content().string(containsString("{\"success\":true")))
				.andExpect(status().isOk());

	}


}
