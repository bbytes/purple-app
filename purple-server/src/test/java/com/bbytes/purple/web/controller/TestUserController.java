package com.bbytes.purple.web.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
public class TestUserController extends PurpleWebBaseApplicationTests {

	Organization org;
	User adminUser, normalUser, user1, user2;
	Project project1, project2;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).dispatchOptions(true).addFilters(filterChainProxy)
				.build();

		org = new Organization("test", "test-org");
		adminUser = new User("admin-user", "test@gmail.com");
		adminUser.setUserRole(UserRole.ADMIN_USER_ROLE);
		adminUser.setOrganization(org);

		normalUser = new User("akshay", "akshay@gmail.com");
		normalUser.setOrganization(org);

		project1 = new Project("ABC");
		project1.setOrganization(org);

		project2 = new Project("XYZ");
		project2.setOrganization(org);

		user1 = new User("john", "john@gmail.com");
		user1.setOrganization(org);

		user2 = new User("ricky", "ricky@gmail.com");
		user2.setOrganization(org);

		TenancyContextHolder.setTenant(org.getOrgId());
		userService.deleteAll();
		organizationRepository.deleteAll();
		projectRepository.deleteAll();

		TenancyContextHolder.setTenant(org.getOrgId());
		organizationRepository.save(org);
		userService.save(adminUser);
		userService.save(normalUser);
		userService.save(user1);
		userService.save(user2);
		userService.updatePassword("test123", normalUser);
		userService.updatePassword("test123", adminUser);
	}

	// Test cases for get all projects by logged in user

	@Test
	public void testGetAllProjectFailed() throws Exception {

		mockMvc.perform(get("/api/v1/user/projects")).andExpect(status().is4xxClientError()).andDo(print());

	}

	@Test
	public void testGetAllProjectByUser() throws Exception {

		Set<User> userList = new HashSet<User>();
		userList.add(normalUser);
		userList.add(user1);

		Project project1 = new Project("web");
		project1.setOrganization(org);
		project1.setUser(userList);
		projectService.save(project1);

		Project project2 = new Project("reveal");
		project2.setOrganization(org);
		project2.setUser(userList);
		projectService.save(project2);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 30);

		mockMvc.perform(get("/api/v1/user/projects").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true")));
	}

	// Test cases for get all users by projects Map

	@Test
	public void testGetAllUsersByProjectsMapFailed() throws Exception {

		mockMvc.perform(get("/api/v1/projects/users/all/map")).andExpect(status().is4xxClientError()).andDo(print());

	}

	@Test
	public void testGetAllUsersByProjectsMapPasses() throws Exception {

		Set<User> userList1 = new HashSet<User>();
		userList1.add(user1);
		userList1.add(user2);

		Set<User> userList2 = new HashSet<User>();
		userList2.add(user2);

		project1.setUser(userList1);
		projectService.save(project1);

		project2.setUser(userList2);
		projectService.save(project2);

		List<String> projectList = new ArrayList<String>();
		projectList.add(projectService.findByProjectId(project1.getProjectId()).getProjectId());
		projectList.add(projectService.findByProjectId(project2.getProjectId()).getProjectId());

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(projectList);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 30);
		mockMvc.perform(post("/api/v1/projects/users/all/map").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	@Test
	public void testGetAllUsersByProjectsFailed() throws Exception {

		mockMvc.perform(get("/api/v1/projects/users/all")).andExpect(status().is4xxClientError()).andDo(print());

	}

	@Test
	public void testGetAllUsersByProjectsPasses() throws Exception {

		Set<User> userList1 = new HashSet<User>();
		userList1.add(user1);
		userList1.add(user2);

		Set<User> userList2 = new HashSet<User>();
		userList2.add(user2);

		project1.setUser(userList1);
		projectService.save(project1);

		project2.setUser(userList2);
		projectService.save(project2);

		List<String> projectList = new ArrayList<String>();
		projectList.add(projectService.findByProjectId(project1.getProjectId()).getProjectId());
		projectList.add(projectService.findByProjectId(project2.getProjectId()).getProjectId());

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(projectList);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 30);
		mockMvc.perform(post("/api/v1/projects/users/all").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	// Test cases for update user

	@Test
	public void testUpdateUserPasses() throws Exception {

		String userName = "Updated Name";

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 30);
		mockMvc.perform(put("/api/v1/user/update").param("userName", userName)
				.header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken).contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	@Test
	public void testaddDeviceTokenPasses() throws Exception {

		String deviceToken = "cdjcdndsjc";

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 30);
		mockMvc.perform(put("/api/v1/user/devicetoken/add").param("deviceToken", deviceToken)
				.header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken).contentType(APPLICATION_JSON_UTF8))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	@Test
	public void testisDeviceTokenPasses() throws Exception {

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 30);
		mockMvc.perform(get("/api/v1/user/devicetoken").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}
}
