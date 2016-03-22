package com.bbytes.purple.web.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bbytes.purple.PurpleWebBaseApplicationTests;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.rest.dto.models.SignUpRequestDTO;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.TenancyContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Test Cases for Sign up controller
 * 
 * @author akshay
 *
 */
public class TestSignUpController extends PurpleWebBaseApplicationTests {

	Organization org;
	User adminUser;
	
	@Before
	public void setUp()
	{
		mockMvc = MockMvcBuilders.webAppContextSetup(context).dispatchOptions(true).addFilters(filterChainProxy)
				.build();
		org = new Organization("test", "test-org");
		adminUser = new User("admin-user", "test@gmail.com");
		adminUser.setUserRole(UserRole.ADMIN_USER_ROLE);
		adminUser.setOrganization(org);
		
		TenancyContextHolder.setTenant(org.getOrgId());
		userService.deleteAll();
		organizationRepository.deleteAll();
	}
	
	@Test
	public void testSignUpFailed() throws Exception {
		mockMvc.perform(post("/auth/signup")).andExpect(status().is5xxServerError()).andDo(print());
	}

	@Test
	public void testSignUpFailedDueToDuplicateName() throws Exception {

		String orgName = "testOrgSignUp";
		TenancyContextHolder.setTenant(orgName);
		organizationService.deleteAll();

		SignUpRequestDTO requestDTO = new SignUpRequestDTO();
		requestDTO.setBusinessArea("IT");
		requestDTO.setEmail("test@anc.com");
		requestDTO.setPassword("Test123");
		requestDTO.setOrgName(orgName);

		organizationService.save(new Organization(orgName, orgName));

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestDTO);

		mockMvc.perform(post("/auth/signup").contentType(APPLICATION_JSON_UTF8).content(requestJson))
				.andExpect(status().is5xxServerError()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":false")));
	}

	@Test
	public void testSignUpPasses() throws Exception {

		String orgName = "test";
		TenancyContextHolder.setTenant(orgName);
		organizationService.deleteAll();
		userService.deleteAll();

		SignUpRequestDTO requestDTO = new SignUpRequestDTO();
		requestDTO.setBusinessArea("IT");
		requestDTO.setEmail("akshay.nag@beyondbytes.co.in");
		requestDTO.setPassword("Test123");
		requestDTO.setOrgName(orgName);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestDTO);

		mockMvc.perform(post("/auth/signup").contentType(APPLICATION_JSON_UTF8).content(requestJson))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true")));

	}
	
	@Test
	public void testActivateAccountFailed() throws Exception {
		mockMvc.perform(get("api/v1/admin/activateAccount")).andExpect(status().is4xxClientError()).andDo(print());
	}
	
	@Test
	public void testActivateAccountPasses() throws Exception {

		TenancyContextHolder.setTenant(org.getOrgId());
		organizationRepository.save(org);
		userService.save(adminUser);
		userService.updatePassword("test123", adminUser);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(get("/api/v1/admin/activateAccount").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
		.andExpect(status().isOk()).andDo(print())
		.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}
	
	@Test
	public void testActivateAccountFailedWithInvalidToken() throws Exception {

		TenancyContextHolder.setTenant(org.getOrgId());
		organizationRepository.save(org);
		userService.save(adminUser);
		userService.updatePassword("test123", adminUser);

		String xauthToken = "fbvhfdvbjfdvfdjvfdjvfdvfdvfdjvn455552";
		mockMvc.perform(get("/api/v1/admin/activateAccount").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
		.andExpect(status().is4xxClientError()).andDo(print())
		.andExpect(content().string(containsString("{\"success\":false"))).andExpect(status().is4xxClientError());

	}
	
}
