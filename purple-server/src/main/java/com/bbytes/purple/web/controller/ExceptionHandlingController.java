package com.bbytes.purple.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.ErrorHandler;

@ControllerAdvice
public class ExceptionHandlingController {

	@Autowired
	private UserService userService;

	public final Logger logger = LoggerFactory.getLogger(ExceptionHandlingController.class);

	@ExceptionHandler(PurpleException.class)
	@ResponseBody
	public ResponseEntity<Object> handleError(HttpServletRequest req, PurpleException ex) {

		String userInfoStr = getUserInfo();

		String erroMessage = "Request: " + req.getRequestURL() + " raised - " + ex.getMessage() + " - Error Constant - "
				+ ex.getErrConstant() + userInfoStr;
		logger.error(erroMessage, ex);
		RestResponse errorResponse = new RestResponse(RestResponse.FAILED, ex.getMessage(), ex.getErrConstant());
		return new ResponseEntity<Object>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ResponseEntity<Object> handleError(HttpServletRequest req, Exception ex) {
		
		String userInfoStr = getUserInfo();

		String erroMessage = "Request: " + req.getRequestURL() + " raised - " + ex.getMessage() + userInfoStr;
		logger.error(erroMessage, ex);
		RestResponse errorResponse = new RestResponse(RestResponse.FAILED, ex.getMessage(), ErrorHandler.SERVER_ERROR);
		return new ResponseEntity<Object>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private String getUserInfo() {

		User user = userService.getLoggedInUser();

		if(user == null)
			return "";
		
		String userInfo = " - by Logged in User - " + user.getName() + " with Email : " + user.getEmail() + " of "
				+ user.getOrganization().getOrgName() + " - organization";

		return userInfo;
	}
}