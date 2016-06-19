package com.bbytes.purple.web.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bbytes.purple.PurpleWebBaseApplicationTests;
import com.bbytes.purple.domain.Integration;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.rest.dto.models.IntegrationRequestDTO;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.TenancyContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Test cases for Integration Controller
 * 
 * @author akshay
 *
 */
public class TestIntegrationController extends PurpleWebBaseApplicationTests {

	Organization org;
	User normalUser;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).dispatchOptions(true).addFilters(filterChainProxy)
				.build();

		org = new Organization("test", "test-org");
		normalUser = new User("admin-user", "test@gmail.com");
		normalUser.setUserRole(UserRole.ADMIN_USER_ROLE);
		normalUser.setOrganization(org);

		TenancyContextHolder.setTenant(org.getOrgId());
		organizationRepository.save(org);
		userService.save(normalUser);
		userService.updatePassword("test123", normalUser);
	}

	// Test Cases for connecting to JIRA (saving basic auth header)

	@Test
	public void testConnectToJIRAPasses() throws Exception {

		IntegrationRequestDTO requestDTO = new IntegrationRequestDTO();
		requestDTO.setJiraBaseUrl("https://beyondbytes.atlassian.net/");
		requestDTO.setUserName("akshay.nag");
		requestDTO.setPassword("akshay123");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 1);

		mockMvc.perform(
				post("/api/v1/integration/jira/addAuthentication").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
						.contentType(APPLICATION_JSON_UTF8).content(requestJson))
				.andExpect(status().is5xxServerError()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":false")))
				.andExpect(status().is5xxServerError());
	}

	// Test Cases for validating basic auth header for user

	@Test
	public void testgetJIRAConnection() throws Exception {

		Integration integration = new Integration();
		integration.setJiraBaseURL("https://beyondbytes.atlassian.net/");
		;
		integration.setJiraBasicAuthHeader("Basic YWtzaGF5Lm5hZzpha3NoYXkxMjM=");
		integration.setUser(normalUser);
		integrationService.save(integration);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 1);

		mockMvc.perform(
				get("/api/v1/integration/jira/getAuthentication").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":false")))
				.andExpect(status().isOk());
	}

	// Test Case for getting project from JIRA by logged in user.

	@Test
	public void testgetJIRAProjects() throws Exception {

		Project project1 = new Project("Purple");
		project1.setOrganization(org);
		projectService.save(project1);

		Integration integration = new Integration();
		integration.setJiraBaseURL("https://beyondbytes.atlassian.net");
		;
		integration.setJiraBasicAuthHeader("Basic YWtzaGF5Lm5hZzpha3NoYXluYWcxOTA0");
		integration.setUser(normalUser);
		integrationService.save(integration);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 1);

		mockMvc.perform(
				get("/api/v1/integration/jira/getprojects").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());
	}

}
