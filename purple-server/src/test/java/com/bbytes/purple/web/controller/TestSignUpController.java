package com.bbytes.purple.web.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.Test;

import com.bbytes.purple.PurpleWebBaseApplicationTests;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.rest.dto.models.SignUpRequestDTO;
import com.bbytes.purple.utils.TenancyContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class TestSignUpController extends PurpleWebBaseApplicationTests {

	@Test
	public void testSignUpFailed() throws Exception {
		mockMvc.perform(post("/auth/signup")).andExpect(status().is4xxClientError()).andDo(print());
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

		String orgName = "testOrgSignUp";
		TenancyContextHolder.setTenant(orgName);
		organizationService.deleteAll();
		userService.deleteAll();

		SignUpRequestDTO requestDTO = new SignUpRequestDTO();
		requestDTO.setBusinessArea("IT");
		requestDTO.setEmail("test@anc.com");
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
}
