package com.bbytes.purple.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bbytes.purple.PurpleWebBaseApplicationTests;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.rest.dto.models.ConfigSettingDTO;
import com.bbytes.purple.rest.dto.models.PasswordDTO;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.TenancyContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class TestSettingController extends PurpleWebBaseApplicationTests {

	Organization org;
	User adminUser;

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

		TenancyContextHolder.setTenant(org.getOrgId());
		organizationRepository.save(org);
		userService.save(adminUser);
		userService.updatePassword("test123", adminUser);

	}

	@Test
	public void testResetPassword_password_Pass() throws Exception {

		PasswordDTO pwdDTO = new PasswordDTO();
		pwdDTO.setOldPassword("test123");
		pwdDTO.setNewPassword("test1235");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(pwdDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);

		mockMvc.perform(post("/api/v1/user/setting/password").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().is2xxSuccessful())
				.andDo(print());

	}

	@Test
	public void testResetPassword_password_fail() throws Exception {

		PasswordDTO pwdDTO = new PasswordDTO();
		pwdDTO.setOldPassword("test1");
		pwdDTO.setNewPassword("test1235");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(pwdDTO);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);

		mockMvc.perform(post("/api/v1/user/setting/password").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().is5xxServerError())
				.andDo(print());
	}

	@Test
	public void testReset_Passsword_fail() throws Exception {
		mockMvc.perform(post("/api/v1/user/setting/password")).andExpect(status().is4xxClientError()).andDo(print());
	}

	/**
	 * Test cases for config setting
	 */

	@Test
	public void testNotificationSettingFailed() throws Exception {
		mockMvc.perform(post("/api/v1/admin/configSetting/update")).andExpect(status().is4xxClientError())
				.andDo(print());
	}

	@Test
	public void testNotificationSettingPasses() throws Exception {

		List<String> holidayDates = new ArrayList<String>();
		holidayDates.add("16/04/2016");
		holidayDates.add("02/10/2016");
		holidayDates.add("30/11/2016");
		holidayDates.add("15/06/2016");

		ConfigSettingDTO requestNotification = new ConfigSettingDTO();
		requestNotification.setCaptureHours(false);
		requestNotification.setWeekendNotification(true);
		requestNotification.setStatusEnable("3 Days");
		requestNotification.setHolidayDate(holidayDates);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(requestNotification);

		String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(adminUser.getEmail(), 1);
		mockMvc.perform(post("/api/v1/admin/configSetting/update").header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken)
				.contentType(APPLICATION_JSON_UTF8).content(requestJson)).andExpect(status().is2xxSuccessful())
				.andDo(print());
	}
}
