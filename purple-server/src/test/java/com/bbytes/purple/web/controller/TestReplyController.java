package com.bbytes.purple.web.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bbytes.purple.PurpleWebBaseApplicationTests;
import com.bbytes.purple.domain.Comment;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.Reply;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.rest.dto.models.ReplyDTO;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.TenancyContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Test cases for Reply Controller
 * 
 * @author akshay
 *
 */
public class TestReplyController extends PurpleWebBaseApplicationTests {

	Organization org;
	User adminUser, normalUser;
	Project project;
	Status status1;
	Comment comment;

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

		project = new Project("purple", "4pm");
		project.setOrganization(org);

		status1 = new Status("Test1", "Test2", 3, new Date());
		status1.setProject(project);
		status1.setUser(normalUser);

		comment = new Comment("hello this is comment", normalUser, status1);

		TenancyContextHolder.setTenant(org.getOrgId());
		userService.deleteAll();
		organizationRepository.deleteAll();
		projectRepository.deleteAll();
		statusRepository.deleteAll();
		commentRepository.deleteAll();

		TenancyContextHolder.setTenant(org.getOrgId());
		organizationRepository.save(org);
		userService.save(adminUser);
		userService.save(normalUser);
		projectService.save(project);
		statusService.save(status1);
		commentService.save(comment);
		userService.updatePassword("test123", adminUser);
		userService.updatePassword("test123", normalUser);
	}

	/**
	 * Test Cases for add reply on comment
	 */

	@Test
	public void testAddReplyFailed() throws Exception {

		String id = "sdcsdcsdcsdc";
		mockMvc.perform(post("/api/v1/comment/{commentid}/reply", id)).andExpect(status().is4xxClientError())
				.andDo(print());

	}

	@Test
	public void testAddReplyPasses() throws Exception {

		String id = comment.getCommentId();

		List<Reply> replyList = new ArrayList<Reply>();
		Reply reply = new Reply("previous reply");
		reply.setUser(normalUser);
		replyList.add(reply);
		comment.setReplies(replyList);
		commentService.save(comment);

		ReplyDTO requestReplyDTO = new ReplyDTO();
		requestReplyDTO.setReplyDesc("Reply1");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestReplyDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(
				post("/api/v1/comment/{commentid}/reply", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
						.contentType(APPLICATION_JSON_UTF8).content(requestJson))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	@Test
	public void testAddReplyFailedWithInvalidComment() throws Exception {

		String id = "cdscsdc45454545";
		ReplyDTO requestReplyDTO = new ReplyDTO();
		requestReplyDTO.setReplyDesc("reply1");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestReplyDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 1);
		mockMvc.perform(
				post("/api/v1/comment/{commentid}/reply", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
						.contentType(APPLICATION_JSON_UTF8).content(requestJson))
				.andExpect(status().is5xxServerError()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":false")));

	}

	/**
	 * Test Cases for delete reply on comment
	 */

	@Test
	public void testDeleteReplyFailed() throws Exception {

		String commentId = "sdcsdcs";
		String replyId = "sdcsdcsdcsdc";

		mockMvc.perform(post("/api/v1/comment/{commentid}/reply/{replyid}", commentId, replyId))
				.andExpect(status().is4xxClientError()).andDo(print());

	}

	@Test
	public void testDeleteReplyPasses() throws Exception {

		String commentId = comment.getCommentId();

		List<Reply> reply = new ArrayList<Reply>();
		reply.add(new Reply("it nice"));
		reply.add(new Reply("awesome"));
		reply.add(new Reply("good job"));

		comment.setReplies(reply);
		commentRepository.save(comment);

		String replyId = comment.getReplies().get(0).getReplyId().toString();

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 1);
		mockMvc.perform(delete("/api/v1/comment/{commentid}/reply/{replyid}", commentId, replyId)
				.header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)).andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}

	@Test
	public void testDeleteReplyFailedWithInvalidComment() throws Exception {

		String commentId = "dscsdcdscds555";

		List<Reply> reply = new ArrayList<Reply>();
		reply.add(new Reply("it nice"));
		reply.add(new Reply("awesome"));
		reply.add(new Reply("good job"));

		comment.setReplies(reply);
		commentRepository.save(comment);

		String replyId = comment.getReplies().get(0).getReplyId().toString();
		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 1);
		mockMvc.perform(delete("/api/v1/comment/{commentid}/reply/{replyid}", commentId, replyId)
				.header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)).andExpect(status().is5xxServerError())
				.andDo(print()).andExpect(content().string(containsString("{\"success\":false")))
				.andExpect(status().is5xxServerError());

	}

	/**
	 * Test cases for getting all replies for comment
	 */

	@Test
	public void testGetReplyFailed() throws Exception {

		String commentId = "sdcsdcs";

		mockMvc.perform(get("/api/v1/comment/{commentid}/reply/all", commentId)).andExpect(status().is4xxClientError())
				.andDo(print());
	}

	@Test
	public void testGetReplyPasses() throws Exception {

		String id = comment.getCommentId();

		List<Reply> replyList = new ArrayList<Reply>();

		Reply reply1 = new Reply("previous reply");
		reply1.setUser(adminUser);

		Reply reply2 = new Reply("now reply");
		reply2.setUser(normalUser);

		Reply reply3 = new Reply(" reply");
		reply3.setUser(normalUser);

		replyList.add(reply1);
		replyList.add(reply2);
		replyList.add(reply3);

		comment.setReplies(replyList);
		commentService.save(comment);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(normalUser.getEmail(), 1);
		mockMvc.perform(
				get("/api/v1/comment/{commentid}/reply/all", id).header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(content().string(containsString("{\"success\":true"))).andExpect(status().isOk());

	}
}
