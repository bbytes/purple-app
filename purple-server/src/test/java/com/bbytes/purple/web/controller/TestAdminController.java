package com.bbytes.purple.web.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bbytes.purple.PurpleWebBaseApplicationTests;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.rest.dto.models.ProjectDTO;
import com.bbytes.purple.rest.dto.models.UserDTO;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.TenancyContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Test cases for Admin Controller
 * 
 * @author akshay
 *
 */
public class TestAdminController extends PurpleWebBaseApplicationTests {

	Organization org;
	User adminUser, normalUser, normalUser1;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).dispatchOptions(true).addFilters(filterChainProxy)
				.build();

		org = new Organization("test", "test-org");
		adminUser = new User("admin-user", "test@gmail.com");
		adminUser.setUserRole(UserRole.ADMIN_USER_ROLE);
		adminUser.setOrganization(org);

		TenancyContextHolder.setTenant(org.getOrgId());
		userService.deleteAll();
		organizationRepository.deleteAll();
		projectRepository.deleteAll();

		TenancyContextHolder.setTenant(org.getOrgId());
		organizationRepository.save(org);
		userService.save(adminUser);
		userService.updatePassword("test123", adminUser);
	}

	// Test cases for add users

	@Test
	public void testAddUsersFailed() throws Exception {
		mockMvc.perform(post("/api/v1/admin/user/add")).andExpect(status().is4xxClientError()).andDo(print());
	}

	@Test
	public void testAddUsersPasses() throws Exception {

		UserDTO requestUserDTO = new UserDTO();
		requestUserDTO.setUserName("user1");
		requestUserDTO.setEmail("abc@gmail");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestUserDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);

		mockMvc.perform(post("/api/v1/admin/user/add").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	@Test
	public void testAddUserDueToSameEmail() throws Exception {

		UserDTO requestUserDTO = new UserDTO();
		requestUserDTO.setUserName("user1");
		requestUserDTO.setEmail("test@gmail.com");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestUserDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(post("/api/v1/admin/user/add").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().is5xxServerError())
				.andDo(print()).andExpect(content().string(containsString("{\"success\":false")));
	}

	@Test
	public void testAddUserFailed() throws Exception {

		TenancyContextHolder.setTenant(org.getOrgId());
		organizationRepository.deleteAll();

		UserDTO requestUserDTO = new UserDTO();
		requestUserDTO.setUserName("user1");
		requestUserDTO.setEmail("aaa@gmail.com");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestUserDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(post("/api/v1/admin/user/add").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().is5xxServerError())
				.andDo(print()).andExpect(content().string(containsString("{\"success\":false")));
	}

	// Test cases for delete user

	@Test
	public void testDeleteUsersFailed() throws Exception {

		String email = "abc@gmail";
		mockMvc.perform(post("/api/v1/admin/user/delete/{email}", email)).andExpect(status().is4xxClientError())
				.andDo(print());

	}

	@Test
	public void testDeleteUserPasses() throws Exception {

		String email = "test@gmail.com";

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(delete("/api/v1/admin/user/delete/{email:.+}", email).header(GlobalConstants.HEADER_AUTH_TOKEN,
				xauthToken)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	@Test
	public void testDeleteUserFailed() throws Exception {

		String email = "abb@gmail.com";

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(delete("/api/v1/admin/user/delete/{email:.+}", email).header(GlobalConstants.HEADER_AUTH_TOKEN,
				xauthToken)).andExpect(status().is5xxServerError()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":false")));
	}

	@Test
	public void testDeleteNullUser() throws Exception {

		String email = "null";

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(delete("/api/v1/admin/user/delete/{email:.+}", email).header(GlobalConstants.HEADER_AUTH_TOKEN,
				xauthToken)).andExpect(status().is5xxServerError()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":false")));
	}

	// Test cases for get all users

	@Test
	public void testGetAllUsersFailed() throws Exception {

		mockMvc.perform(get("/api/v1/admin/user")).andExpect(status().is4xxClientError()).andDo(print());

	}

	@Test
	public void testGetAllUserPasses() throws Exception {

		normalUser = new User("normal-user", "normal@gmail.com");
		normalUser.setOrganization(org);
		userService.save(normalUser);

		normalUser1 = new User("normal-user2", "normal2@gmail.com");
		normalUser1.setOrganization(org);
		userService.save(normalUser1);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(get("/api/v1/admin/user").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	// Test cases for add project

	@Test
	public void testCreateProjectFailed() throws Exception {

		mockMvc.perform(post("/api/v1/admin/project/create")).andExpect(status().is4xxClientError()).andDo(print());

	}

	@Test
	public void testCreateProjectPasses() throws Exception {

		User user1 = new User("akshay", "akshay@gmail.com");
		user1.setOrganization(org);
		userService.save(user1);

		User user2 = new User("sample", "sample@gmail.com");
		user2.setOrganization(org);
		userService.save(user2);

		List<String> userList = new ArrayList<String>();
		userList.add(user1.getEmail());
		userList.add(user2.getEmail());

		ProjectDTO requestProjectDTO = new ProjectDTO();
		requestProjectDTO.setProjectName("purple");
		requestProjectDTO.setTimePreference("6.00 pm");
		requestProjectDTO.setUsers(userList);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestProjectDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);

		mockMvc.perform(post("/api/v1/admin/project/create").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	@Test
	public void testCreateProjectFailedwithSameName() throws Exception {

		Project project = new Project("purple", "4pm");
		project.setOrganization(org);
		projectService.save(project);

		User user1 = new User("akshay", "akshay@gmail.com");
		user1.setOrganization(org);
		userService.save(user1);

		List<String> userList = new ArrayList<String>();
		userList.add(user1.getEmail());

		ProjectDTO requestProjectDTO = new ProjectDTO();
		requestProjectDTO.setProjectName("purple");
		requestProjectDTO.setTimePreference("6.00 pm");
		requestProjectDTO.setUsers(userList);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestProjectDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);

		mockMvc.perform(post("/api/v1/admin/project/create").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().is5xxServerError())
				.andDo(print()).andExpect(content().string(containsString("{\"success\":false")));

	}

	// Test cases for delete project

	@Test
	public void testDeleteProjectFailed() throws Exception {

		String id = "asks455csdc";
		mockMvc.perform(delete("/api/v1/admin/project/delete/{projectid}", id)).andExpect(status().is4xxClientError())
				.andDo(print());

	}

	@Test
	public void testDeleteProjectPasses() throws Exception {

		Project project = new Project("purple", "4pm");
		project.setOrganization(org);
		projectService.save(project);

		String id = projectService.findByProjectName("purple").getProjectId();

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(delete("/api/v1/admin/project/delete/{projectid}", id).header(GlobalConstants.HEADER_AUTH_TOKEN,
				xauthToken)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	@Test
	public void testDeleteProjectFailedwithNull() throws Exception {

		String id = "null";

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(delete("/api/v1/admin/project/delete/{projectid}", id).header(GlobalConstants.HEADER_AUTH_TOKEN,
				xauthToken)).andExpect(content().string(containsString("{\"success\":false")))
				.andExpect(status().is5xxServerError());

	}

	@Test
	public void testFailedwithNotExitProject() throws Exception {

		String id = "reveal";

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(delete("/api/v1/admin/project/delete/{projectid}", id).header(GlobalConstants.HEADER_AUTH_TOKEN,
				xauthToken)).andExpect(content().string(containsString("{\"success\":false")))
				.andExpect(status().is5xxServerError()).andDo(print());

	}

	// Test cases for get all projects

	@Test
	public void testGetAllProjectFailed() throws Exception {

		mockMvc.perform(get("/api/v1/admin/project")).andExpect(status().is4xxClientError()).andDo(print());

	}

	@Test
	public void testGetAllProjectsPasses() throws Exception {
		
		User user1 = new User("akshay", "akshay@gmail.com");
		user1.setOrganization(org);
		userService.save(user1);

		User user2 = new User("sample", "sample@gmail.com");
		user2.setOrganization(org);
		userService.save(user2);

		List<User> userList = new ArrayList<User>();
		userList.add(user1);
		userList.add(user2);
		
		Project project1 = new Project("purple", "4pm");
		project1.setOrganization(org);
		project1.setUser(userList);
		projectService.save(project1);

		Project project2 = new Project("reveal", "4pm");
		project2.setOrganization(org);
		projectService.save(project2);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(get("/api/v1/admin/project").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	// Test cases for get one project

	@Test
	public void testGetOneProjectFailed() throws Exception {

		String id = "cecec65656";
		mockMvc.perform(get("/api/v1/admin/project/{projectid}", id)).andExpect(status().is4xxClientError())
				.andDo(print());

	}

	@Test
	public void testGetOneProjectsPasses() throws Exception {

		Project project1 = new Project("purple", "4pm");
		project1.setOrganization(org);
		projectService.save(project1);

		String id = projectService.findByProjectName("purple").getProjectId();

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(
				get("/api/v1/admin/project/{projectid}", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	@Test
	public void testFailedwitNotExistProjectID() throws Exception {

		String id = "cdscdsc666";

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(
				get("/api/v1/admin/project/{projectid}", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
				.andExpect(status().is5xxServerError()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":false")));

	}

	// Test cases for update project

	@Test
	public void testUpdateProjectFailed() throws Exception {

		String id = "cecec65656";
		mockMvc.perform(put("/api/v1/admin/project/update/{projectid}", id)).andExpect(status().is4xxClientError())
				.andDo(print());

	}

	@Test
	public void testUpdateProjectsPasses() throws Exception {

		Project project1 = new Project("purple", "4pm");
		project1.setOrganization(org);
		projectService.save(project1);

		String id = projectService.findByProjectName("purple").getProjectId();

		User user1 = new User("akshay", "akshay@gmail.com");
		user1.setOrganization(org);
		userService.save(user1);

		List<String> userList = new ArrayList<String>();
		userList.add(user1.getEmail());

		ProjectDTO requestProjectDTO = new ProjectDTO();
		requestProjectDTO.setProjectName("web");
		requestProjectDTO.setTimePreference("6.00 pm");
		requestProjectDTO.setUsers(userList);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestProjectDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(put("/api/v1/admin/project/update/{projectid}", id)
				.header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken).contentType(APPLICATION_JSON_UTF8)
				.content(requestJson)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	@Test
	public void testUpdateProjectFailedwithSameName() throws Exception {

		Project project1 = new Project("purple", "4pm");
		project1.setOrganization(org);
		projectService.save(project1);

		String id = projectService.findByProjectName("purple").getProjectId();

		User user1 = new User("akshay", "akshay@gmail.com");
		user1.setOrganization(org);
		userService.save(user1);

		List<String> userList = new ArrayList<String>();
		userList.add(user1.getEmail());

		ProjectDTO requestProjectDTO = new ProjectDTO();
		requestProjectDTO.setProjectName("purple");
		requestProjectDTO.setTimePreference("6.00 pm");
		requestProjectDTO.setUsers(userList);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestProjectDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(put("/api/v1/admin/project/update/{projectid}", id)
				.header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken).contentType(APPLICATION_JSON_UTF8)
				.content(requestJson)).andExpect(status().is5xxServerError()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":false")));

	}

	@Test
	public void testUpdateProjectFailedwithNull() throws Exception {

		String id = "null";

		User user1 = new User("akshay", "akshay@gmail.com");
		user1.setOrganization(org);
		userService.save(user1);

		List<String> userList = new ArrayList<String>();
		userList.add(user1.getEmail());

		ProjectDTO requestProjectDTO = new ProjectDTO();
		requestProjectDTO.setProjectName("purple");
		requestProjectDTO.setTimePreference("6.00 pm");
		requestProjectDTO.setUsers(userList);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestProjectDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(put("/api/v1/admin/project/update/{projectid}", id)
				.header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken).contentType(APPLICATION_JSON_UTF8)
				.content(requestJson)).andExpect(status().is5xxServerError()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":false")));

	}
}
