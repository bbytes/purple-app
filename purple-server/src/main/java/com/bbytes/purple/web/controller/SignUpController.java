package com.bbytes.purple.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.rest.dto.models.SignUpRequestDTO;
import com.bbytes.purple.service.RegistrationService;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.SuccessHandler;

/**
 * Sign-up controller
 * 
 * @author akshay
 */
@RestController
public class SignUpController {

	@Autowired
	private RegistrationService regService;

	public final Logger logger = LoggerFactory.getLogger(SignUpController.class);

	@RequestMapping(value = "/auth/signup", method = RequestMethod.POST)
	public RestResponse signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) throws PurpleException {

		final String SIGN_UP_SUCCESS_MSG = "Successfully signed up";
		final String SIGN_UP_ERROR_MSG = "Sign up failed";

		RestResponse signUpResponse;
		// we assume the angular layer will do empty/null org name , user email etc
		// validation 
		String orgId = signUpRequestDTO.getOrgName().replace("\\s", "").trim();

		try {
			Organization organization = new Organization(orgId, signUpRequestDTO.getOrgName());
			organization.setBusinessArea(signUpRequestDTO.getBusinessArea());

			User user = new User(orgId, signUpRequestDTO.getEmail());
			user.setEmail(signUpRequestDTO.getEmail());
			user.setPassword(signUpRequestDTO.getPassword());
			user.setUserRole(UserRole.ADMIN_USER_ROLE);
			user.setOrganization(organization);

			regService.signUp(organization, user);

			signUpResponse = new RestResponse(RestResponse.SUCCESS, SIGN_UP_SUCCESS_MSG,
					SuccessHandler.SIGN_UP_SUCCESS);

			return signUpResponse;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			signUpResponse = new RestResponse(RestResponse.FAILED, SIGN_UP_ERROR_MSG, ErrorHandler.SIGN_UP_FAILED);
			return signUpResponse;
		}
	}
}
