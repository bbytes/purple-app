package com.bbytes.purple.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bbytes.purple.PurpleWebBaseApplicationTests;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.rest.dto.models.StatusDTO;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.TenancyContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Test cases for Status Controller
 * 
 * @author akshay
 *
 */
public class TestStatusController extends PurpleWebBaseApplicationTests {

	Organization org;
	User adminUser, normalUser;
	Project project;
	Status status1, status2;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).dispatchOptions(true).addFilters(filterChainProxy)
				.build();

		org = new Organization("test", "test-org");
		adminUser = new User("admin-user", "test@gmail.com");
		adminUser.setUserRole(UserRole.ADMIN_USER_ROLE);
		adminUser.setOrganization(org);

		project = new Project("purple", "4pm");
		project.setOrganization(org);

		TenancyContextHolder.setTenant(org.getOrgId());
		userService.deleteAll();
		organizationRepository.deleteAll();
		projectRepository.deleteAll();
		statusRepository.deleteAll();

		TenancyContextHolder.setTenant(org.getOrgId());
		organizationRepository.save(org);
		userService.save(adminUser);
		projectService.save(project);
		userService.updatePassword("test123", adminUser);
	}

	// Test Cases for add status on project

	@Test
	public void testAddStatusFailed() throws Exception {

		mockMvc.perform(post("/api/v1/status/add")).andExpect(status().is4xxClientError()).andDo(print());

	}

	@Test
	public void testAddStatusPasses() throws Exception {

		normalUser = new User("akshay", "akshay@gmail.com");
		normalUser.setOrganization(org);
		userService.save(normalUser);
		userService.updatePassword("test123", normalUser);

		String id = projectService.findByProjectName("purple").getProjectId();

		StatusDTO requestStatusDTO = new StatusDTO();
		requestStatusDTO.setProjectId(id);
		requestStatusDTO.setHours(4);
		requestStatusDTO.setWorkedOn("Testing");
		requestStatusDTO.setWorkingOn("Will do testing");
		requestStatusDTO.setBlockers("Testing issue");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestStatusDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 1);
		mockMvc.perform(post("/api/v1/status/add").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	@Test
	public void testAddStatusFailedWithNullProject() throws Exception {

		normalUser = new User("akshay", "akshay@gmail.com");
		normalUser.setOrganization(org);
		userService.save(normalUser);
		userService.updatePassword("test123", normalUser);

		String id = null;

		StatusDTO requestStatusDTO = new StatusDTO();
		requestStatusDTO.setProjectId(id);
		requestStatusDTO.setHours(4);
		requestStatusDTO.setWorkedOn("Testing");
		requestStatusDTO.setWorkingOn("Will do testing");
		requestStatusDTO.setBlockers("Testing issue");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestStatusDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 1);
		mockMvc.perform(post("/api/v1/status/add").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().is5xxServerError())
				.andDo(print()).andExpect(content().string(containsString("{\"success\":false")));

	}

	@Test
	public void testAddStatusFailedWithNullStatus() throws Exception {

		normalUser = new User("akshay", "akshay@gmail.com");
		normalUser.setOrganization(org);
		userService.save(normalUser);
		userService.updatePassword("test123", normalUser);

		StatusDTO requestStatusDTO = new StatusDTO();

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestStatusDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 1);
		mockMvc.perform(post("/api/v1/status/add").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().is5xxServerError())
				.andDo(print()).andExpect(content().string(containsString("{\"success\":false")));

	}

	@Test
	public void testAddStatusFailedWithExistNotProject() throws Exception {

		normalUser = new User("akshay", "akshay@gmail.com");
		normalUser.setOrganization(org);
		userService.save(normalUser);
		userService.updatePassword("test123", normalUser);

		StatusDTO requestStatusDTO = new StatusDTO();
		requestStatusDTO.setProjectId("sdfdvfdf555522");
		requestStatusDTO.setHours(4);
		requestStatusDTO.setWorkedOn("Testing");
		requestStatusDTO.setWorkingOn("Will do testing");
		requestStatusDTO.setBlockers("Testing issue");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestStatusDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 1);
		mockMvc.perform(post("/api/v1/status/add").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().is5xxServerError())
				.andDo(print()).andExpect(content().string(containsString("{\"success\":false")));

	}

	// Test Cases for get status of project

	@Test
	public void testGetStatusFailed() throws Exception {

		String id = "rfrefj232432";
		mockMvc.perform(get("/api/v1/status/{statusid}", id)).andExpect(status().is4xxClientError()).andDo(print());

	}

	@Test
	public void testGetStatusPasses() throws Exception {

		normalUser = new User("akshay", "akshay@gmail.com");
		normalUser.setOrganization(org);
		userService.save(normalUser);
		userService.updatePassword("test123", normalUser);

		Status status = new Status("test", "test", 3, new DateTime());
		status.setProject(project);
		status.setUser(normalUser);
		status = statusService.save(status);

		String id = status.getStatusId();

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 1);
		mockMvc.perform(get("/api/v1/status/{statusid}", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	@Test
	public void testGetStatusFailedWithNullId() throws Exception {

		normalUser = new User("akshay", "akshay@gmail.com");
		normalUser.setOrganization(org);
		userService.save(normalUser);
		userService.updatePassword("test123", normalUser);

		String id = "null";

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 1);
		mockMvc.perform(get("/api/v1/status/{statusid}", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
				.andExpect(status().is5xxServerError()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":false")));

	}

	@Test
	public void testGetStatusFailedWithNotExistStatus() throws Exception {

		normalUser = new User("akshay", "akshay@gmail.com");
		normalUser.setOrganization(org);
		userService.save(normalUser);
		userService.updatePassword("test123", normalUser);

		String id = "cdscsdsd6666";

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 1);
		mockMvc.perform(get("/api/v1/status/{statusid}", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
				.andExpect(status().is5xxServerError()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":false")));

	}

	// Test Cases for get all status of project

	@Test
	public void testGetAllStatusFailed() throws Exception {

		mockMvc.perform(get("/api/v1/status")).andExpect(status().is4xxClientError()).andDo(print());

	}

	@Test
	public void testGetAllStatusPasses() throws Exception {

		normalUser = new User("akshay", "akshay@gmail.com");
		normalUser.setOrganization(org);
		userService.save(normalUser);
		userService.updatePassword("test123", normalUser);

		Status status = new Status("test", "test", 3, new DateTime());
		status.setProject(project);
		status.setUser(normalUser);
		statusService.save(status);

		Status status1 = new Status("test1", "test1", 4, new DateTime());
		status1.setProject(project);
		status1.setUser(adminUser);
		statusService.save(status1);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 1);
		mockMvc.perform(get("/api/v1/status").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	// Test cases for delete status

	@Test
	public void testDeleteStatusFailed() throws Exception {

		String id = "cdscddf";
		mockMvc.perform(delete("/api/v1/status/{statusid}", id)).andExpect(status().is4xxClientError()).andDo(print());

	}

	@Test
	public void testDeleteStatusPasses() throws Exception {

		normalUser = new User("akshay", "akshay@gmail.com");
		normalUser.setOrganization(org);
		userService.save(normalUser);
		userService.updatePassword("test123", normalUser);

		Status status = new Status("test", "test", 3, new DateTime());
		status.setProject(project);
		status.setUser(normalUser);
		status = statusService.save(status);

		Status status1 = new Status("test1", "test1", 3, new DateTime());
		status1.setProject(project);
		status1.setUser(normalUser);
		status1 = statusService.save(status1);

		String id = status.getStatusId();

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 1);
		mockMvc.perform(delete("/api/v1/status/{statusid}", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	@Test
	public void testDeleteStatusFailedWithNullId() throws Exception {

		normalUser = new User("akshay", "akshay@gmail.com");
		normalUser.setOrganization(org);
		userService.save(normalUser);
		userService.updatePassword("test123", normalUser);

		String id = "null";

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 1);
		mockMvc.perform(delete("/api/v1/status/{statusid}", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
				.andExpect(status().is5xxServerError()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":false")));

	}

	@Test
	public void testDeleteStatusFailedWithNotExistStatus() throws Exception {

		normalUser = new User("akshay", "akshay@gmail.com");
		normalUser.setOrganization(org);
		userService.save(normalUser);
		userService.updatePassword("test123", normalUser);

		String id = "456454cdscsdcds";

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 1);
		mockMvc.perform(delete("/api/v1/status/{statusid}", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
				.andExpect(status().is5xxServerError()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":false")));

	}

	// Test cases for update status

	@Test
	public void testUpdateStatusFailed() throws Exception {

		String id = "cdscddf";
		mockMvc.perform(delete("/api/v1/status/{statusid}", id)).andExpect(status().is4xxClientError()).andDo(print());

	}

	@Test
	public void testUpdateStatusPasses() throws Exception {

		normalUser = new User("akshay", "akshay@gmail.com");
		normalUser.setOrganization(org);
		userService.save(normalUser);
		userService.updatePassword("test123", normalUser);

		Status status = new Status("test", "test", 3, new DateTime());
		status.setProject(project);
		status.setUser(normalUser);
		status = statusService.save(status);

		String id = status.getStatusId();

		StatusDTO requestStatusDTO = new StatusDTO();
		requestStatusDTO.setHours(4);
		requestStatusDTO.setWorkedOn("Testing");
		requestStatusDTO.setWorkingOn("Will do testing");
		requestStatusDTO.setBlockers("Testing issue");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestStatusDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 1);
		mockMvc.perform(
				put("/api/v1/status/update/{statusid}", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
						.contentType(APPLICATION_JSON_UTF8).content(requestJson))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	@Test
	public void testUpdateStatusWithNullStatusId() throws Exception {

		normalUser = new User("akshay", "akshay@gmail.com");
		normalUser.setOrganization(org);
		userService.save(normalUser);
		userService.updatePassword("test123", normalUser);

		String id = "null";
		StatusDTO requestStatusDTO = new StatusDTO();
		requestStatusDTO.setHours(4);
		requestStatusDTO.setWorkedOn("Testing");
		requestStatusDTO.setWorkingOn("Will do testing");
		requestStatusDTO.setBlockers("Testing issue");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestStatusDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 1);
		mockMvc.perform(
				put("/api/v1/status/update/{statusid}", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
						.contentType(APPLICATION_JSON_UTF8).content(requestJson))
				.andExpect(status().is5xxServerError()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":false")));

	}

	@Test
	public void testUpdateStatusWithExistStatus() throws Exception {

		normalUser = new User("akshay", "akshay@gmail.com");
		normalUser.setOrganization(org);
		userService.save(normalUser);
		userService.updatePassword("test123", normalUser);

		String id = "ferverfre55555";
		StatusDTO requestStatusDTO = new StatusDTO();
		requestStatusDTO.setHours(4);
		requestStatusDTO.setWorkedOn("Testing");
		requestStatusDTO.setWorkingOn("Will do testing");
		requestStatusDTO.setBlockers("Testing issue");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestStatusDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 1);
		mockMvc.perform(
				put("/api/v1/status/update/{statusid}", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
						.contentType(APPLICATION_JSON_UTF8).content(requestJson))
				.andExpect(status().is5xxServerError()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":false")));

	}
}
