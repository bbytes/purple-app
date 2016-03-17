package com.bbytes.purple.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.PasswordDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.service.SettingService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.SuccessHandler;

@RestController
public class SettingController {

	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	private SettingService settingService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/api/v1/user/setting/password", method = RequestMethod.POST)
	public RestResponse resetPassword(@RequestBody PasswordDTO passwordDTO, HttpServletRequest request)
			throws PurpleException {

		// we assume angular side will take care for password validation
		User user = userService.getRequestUser(request);
		settingService.resetPassword(passwordDTO, user);

		logger.debug("User with email  '" + user.getEmail() + "' is reset password successfully");
		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, user, SuccessHandler.RESET_PASSWORD);

		return userReponse;
	}

	@RequestMapping(value = "/api/v1/admin/setting/timezone", method = RequestMethod.POST)
	public RestResponse resetPassword(@RequestParam String timeZone, HttpServletRequest request)
			throws PurpleException {

		User user = userService.getRequestUser(request);
		settingService.updateTimeZone(timeZone, user);

		logger.debug(timeZone + " is updated successfully");
		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, user, SuccessHandler.RESET_PASSWORD);

		return userReponse;
	}
}
