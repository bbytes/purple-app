package com.bbytes.purple.web.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bbytes.purple.PurpleWebBaseApplicationTests;
import com.bbytes.purple.domain.Comment;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.rest.dto.models.CommentDTO;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.TenancyContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author aditya
 *
 */

public class TestCommentController extends PurpleWebBaseApplicationTests {

	Comment comment;
	Organization org;
	Status status;
	User user;
	Project project;

	@Before
	public void setUp() {

		mockMvc = MockMvcBuilders.webAppContextSetup(context).dispatchOptions(true).addFilters(filterChainProxy)
				.build();

		org = new Organization("BBYTES", "BEYONDBYTES");
		project = new Project("purple", "4pm");
		project.setOrganization(org);
		status = new Status("purple", "reveal", 40, new DateTime());
		status.setUser(user);

		TenancyContextHolder.setTenant(org.getOrgId());
		userRepository.deleteAll();
		statusRepository.deleteAll();
		projectRepository.deleteAll();
		organizationService.deleteAll();

		TenancyContextHolder.setTenant(org.getOrgId());
		organizationRepository.save(org);
		user = new User("aditya", "aditya@bbytes.co.in");
		user.setOrganization(org);
		userRepository.save(user);
		projectService.save(project);
		userService.updatePassword("test123", user);
		status = statusService.save(status);

	}

	@Test
	public void testSaveComment() throws Exception {

		String id = status.getStatusId();

		CommentDTO requestComment = new CommentDTO();
		requestComment.setCommentDesc("Hello this is my first comment");
		requestComment.setStatusId(id);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestComment);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(user.getEmail(), 1);
		mockMvc.perform(post("/api/v1/comment/add").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}
	
	
}
