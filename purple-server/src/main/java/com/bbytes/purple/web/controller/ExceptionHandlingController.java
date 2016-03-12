package com.bbytes.purple.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.utils.ErrorHandler;

@ControllerAdvice
public class ExceptionHandlingController {

	public final Logger logger = LoggerFactory.getLogger(SignUpController.class);

	@ExceptionHandler(PurpleException.class)
	@ResponseBody
	public ResponseEntity<Object> handleError(HttpServletRequest req, PurpleException ex) {
		logger.error("Request: " + req.getRequestURL() + " raised " + ex);
		RestResponse errorResponse = new RestResponse(RestResponse.FAILED, ex.getMessage(), ex.getErrConstant());
		return new ResponseEntity<Object>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ResponseEntity<Object> handleError(HttpServletRequest req, Exception ex) {
		logger.error("Request: " + req.getRequestURL() + " raised " + ex);
		RestResponse errorResponse = new RestResponse(RestResponse.FAILED, ex.getMessage(), ErrorHandler.SERVER_ERROR);
		return new ResponseEntity<Object>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}