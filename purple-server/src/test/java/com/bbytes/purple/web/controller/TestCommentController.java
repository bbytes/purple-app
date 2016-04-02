package com.bbytes.purple.web.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.After;
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

		org = new Organization("Accenture", "Accenture IT Services");
		project = new Project("purple", "4pm");
		project.setOrganization(org);
		status = new Status("purple", "reveal", 40, new Date());
		status.setUser(user);

		TenancyContextHolder.setTenant(org.getOrgId());

		organizationRepository.save(org);
		user = new User("aditya", "aditya@bbytes.co.in");
		user.setOrganization(org);
		userRepository.save(user);
		projectService.save(project);

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
	}

	/**
	 * TestCase for Save Comment
	 */

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

	/**
	 * TestCase for Delete Comment
	 */

	@Test
	public void testDeleteComment() throws Exception {

		Comment comment = new Comment("Project is simple", user, status);
		commentRepository.save(comment);

		String id = comment.getCommentId();

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(user.getEmail(), 1);
		mockMvc.perform(
				delete("/api/v1/comment/delete/{commentid}", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	/**
	 * TestCase for Update Comment
	 */
	@Test
	public void testUpdateComment() throws Exception {

		Comment comment = new Comment("YO Yo", user, status);
		commentRepository.save(comment);

		String id = comment.getCommentId();

		CommentDTO requestComment = new CommentDTO();
		requestComment.setCommentDesc("Hello World is awesome");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestComment);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(user.getEmail(), 1);

		mockMvc.perform(
				put("/api/v1/comment/update/{commentid}", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
						.contentType(APPLICATION_JSON_UTF8).content(requestJson))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true")));
	}

	/**
	 * TestCase for Get Comment
	 */

	@Test
	public void testGetAllComment() throws Exception {

		Comment comment1 = new Comment("One Comment", user, status);
		commentRepository.save(comment1);

		Comment comment2 = new Comment("Comment two", user, status);
		commentRepository.save(comment2);

		Status newStatus = new Status("new status", "new status", 40, new Date());
		status.setUser(user);
		statusService.save(newStatus);

		Comment comment3 = new Comment("New Comment", user, newStatus);
		commentRepository.save(comment3);

		String statusId = status.getStatusId();

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(user.getEmail(), 1);

		mockMvc.perform(get("/api/v1/comments").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken).param("statusId",
				statusId)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());
	}

}
