package com.bbytes.purple.web.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bbytes.purple.PurpleWebBaseApplicationTests;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.rest.dto.models.ProjectDTO;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.TenancyContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Test cases for Project Controller
 * 
 * @author akshay
 *
 */
public class TestProjectController extends PurpleWebBaseApplicationTests {

	Organization org;
	User adminUser, normalUser, normalUser1;
	Project project1;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).dispatchOptions(true).addFilters(filterChainProxy)
				.build();

		org = new Organization("test", "test-org");
		adminUser = new User("admin-user", "test@gmail.com");
		adminUser.setUserRole(UserRole.ADMIN_USER_ROLE);
		adminUser.setOrganization(org);

		project1 = new Project("purple");
		project1.setOrganization(org);

		TenancyContextHolder.setTenant(org.getOrgId());
		userService.deleteAll();
		organizationRepository.deleteAll();
		projectRepository.deleteAll();

		TenancyContextHolder.setTenant(org.getOrgId());
		organizationRepository.save(org);
		userService.save(adminUser);
		projectService.save(project1);
		userService.updatePassword("test123", adminUser);
	}

	// Test Cases for get all users from project

	@Test
	public void testGetAllUsersFailed() throws Exception {

		String id = "dccfdfd1213";
		mockMvc.perform(get("/api/v1/project/{projectid}/users", id)).andExpect(status().is4xxClientError())
				.andDo(print());

	}

	@Test
	public void testGetAllUsersPasses() throws Exception {

		normalUser = new User("akshay", "akshay@gmail.com");
		normalUser.setOrganization(org);
		userService.save(normalUser);

		normalUser1 = new User("aditya", "aditya@gmail.com");
		normalUser1.setOrganization(org);
		userService.save(normalUser1);

		List<User> userList = userService.getUsersByRole(UserRole.NORMAL_USER_ROLE);
		Set<User> projectSet = new HashSet<User>(userList);
		project1.setUsers(projectSet);
		projectService.save(project1);

		String id = projectService.findByProjectName("purple").getProjectId();

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(
				get("/api/v1/project/{projectid}/users", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	@Test
	public void testGetAllUsersFailedWithNull() throws Exception {

		normalUser = new User("akshay", "akshay@gmail.com");
		normalUser.setOrganization(org);
		userService.save(normalUser);

		normalUser1 = new User("aditya", "aditya@gmail.com");
		normalUser1.setOrganization(org);
		userService.save(normalUser1);

		String id = "null";

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(
				get("/api/v1/project/{projectid}/users", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
				.andExpect(status().is5xxServerError()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":false")));

	}

	// Test cases for add users in existing project

	@Test
	public void testAddUsersFailed() throws Exception {

		String id = "dccfdfd1213";
		mockMvc.perform(get("/api/v1/project/{projectid}/adduser", id)).andExpect(status().is4xxClientError())
				.andDo(print());

	}

	@Test
	public void testAddUsersPasses() throws Exception {

		normalUser = new User("akshay", "akshay@gmail.com");
		normalUser.setOrganization(org);
		userService.save(normalUser);

		normalUser1 = new User("aditya", "aditya@gmail.com");
		normalUser1.setOrganization(org);
		userService.save(normalUser1);

		List<String> userList = new ArrayList<String>();
		userList.add(normalUser.getEmail());
		userList.add(normalUser1.getEmail());

		ProjectDTO requestProjectDTO = new ProjectDTO();
		requestProjectDTO.setUsers(userList);

		String id = projectService.findByProjectName("purple").getProjectId();

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestProjectDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(
				post("/api/v1/project/{projectid}/adduser", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
						.contentType(APPLICATION_JSON_UTF8).content(requestJson))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	@Test
	public void testAddUsersFailedWithNotExistEmail() throws Exception {

		normalUser = new User("akshay", "akshay@gmail.com");
		normalUser.setOrganization(org);
		userService.save(normalUser);

		List<String> userList = new ArrayList<String>();
		userList.add(normalUser.getEmail());
		userList.add("adv@gmail.com");

		ProjectDTO requestProjectDTO = new ProjectDTO();
		requestProjectDTO.setUsers(userList);

		String id = projectService.findByProjectName("purple").getProjectId();

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestProjectDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(
				post("/api/v1/project/{projectid}/adduser", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
						.contentType(APPLICATION_JSON_UTF8).content(requestJson))
				.andExpect(status().is5xxServerError()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":false")));

	}

	@Test
	public void testAddUsersFailedWithNotExistId() throws Exception {

		normalUser = new User("akshay", "akshay@gmail.com");
		normalUser.setOrganization(org);
		userService.save(normalUser);

		List<String> userList = new ArrayList<String>();
		userList.add(normalUser.getEmail());

		ProjectDTO requestProjectDTO = new ProjectDTO();
		requestProjectDTO.setUsers(userList);

		String id = "57hghgug";

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestProjectDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(
				post("/api/v1/project/{projectid}/adduser", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
						.contentType(APPLICATION_JSON_UTF8).content(requestJson))
				.andExpect(status().is5xxServerError()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":false")));

	}

	@Test
	public void testAddUsersFailedWithNullUsers() throws Exception {

		List<String> userList = new ArrayList<String>();

		ProjectDTO requestProjectDTO = new ProjectDTO();
		requestProjectDTO.setUsers(userList);

		String id = projectService.findByProjectName("purple").getProjectId();

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestProjectDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(
				post("/api/v1/project/{projectid}/adduser", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
						.contentType(APPLICATION_JSON_UTF8).content(requestJson))
				.andExpect(status().is5xxServerError()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":false")));

	}

	// Test cases for delete users from existing project

	@Test
	public void testDeleteUsersFailed() throws Exception {

		String id = "dccfdfd1213";
		mockMvc.perform(delete("/api/v1/project/{projectid}/deleteuser", id)).andExpect(status().is4xxClientError())
				.andDo(print());

	}

	@Test
	public void testDeleteUsersPasses() throws Exception {

		normalUser = new User("akshay", "akshay@gmail.com");
		normalUser.setOrganization(org);
		userService.save(normalUser);

		normalUser1 = new User("aditya", "aditya@gmail.com");
		normalUser1.setOrganization(org);
		userService.save(normalUser1);

		List<User> userListWithNormalRole = userService.getUsersByRole(UserRole.NORMAL_USER_ROLE);
		Set<User> projectSet = new HashSet<User>(userListWithNormalRole);
		
		project1.setUsers(projectSet);
		projectService.save(project1);

		List<String> userList = new ArrayList<String>();
		userList.add(normalUser.getEmail());

		String id = projectService.findByProjectName("purple").getProjectId();

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(userList);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(delete("/api/v1/project/{projectid}/deleteuser", id)
				.header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken).contentType(APPLICATION_JSON_UTF8)
				.content(requestJson)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	@Test
	public void testDeleteUsersFailedWithNotExistIdAndUser() throws Exception {

		List<String> userList = new ArrayList<String>();

		String id = "dscdscds";

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(userList);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(delete("/api/v1/project/{projectid}/deleteuser", id)
				.header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken).contentType(APPLICATION_JSON_UTF8)
				.content(requestJson)).andExpect(status().is5xxServerError()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":false")));

	}

}
