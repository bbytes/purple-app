package com.bbytes.purple.web.controller;

import static org.hamcrest.Matchers.containsString;
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

	Organization org;
	User user;
	Comment comment;
	Status status;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).dispatchOptions(true).addFilters(filterChainProxy)
				.build();

		org = new Organization("abc", "abc@org");
		organizationService.save(org);

		TenancyContextHolder.setTenant(org.getOrgId());
	}

	@Test
	public void addCommentTest() throws Exception {
		mockMvc.perform(post("/api/v1/comment/add")).andExpect(status().is4xxClientError()).andDo(print());

	}

	@Test
	public void saveComment() throws Exception {
		user = new User("user1", "user1@abc.org");
		user.setOrganization(org);
		userService.save(user);

		CommentDTO requestCommentDTO = new CommentDTO();
		requestCommentDTO.setComment("YO YO PURPLE");
		requestCommentDTO.setStatus(new Status("Purple", "Revil", 8, new DateTime()));
		requestCommentDTO.setUser(user);

		organizationService.save(org);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestCommentDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(requestCommentDTO.getUser().getEmail(), 1);
		mockMvc.perform(post("/api/v1/comment/add").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().is5xxServerError())
				.andDo(print()).andExpect(content().string(containsString("{\"success\":false")));
	}

}
