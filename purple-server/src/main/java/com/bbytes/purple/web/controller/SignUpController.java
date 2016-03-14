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
import com.bbytes.purple.utils.SuccessHandler;

/**
 * Sign-up controller
 * 
 * @author akshay
 */
@RestController
public class SignUpController {

	private static final Logger logger = LoggerFactory.getLogger(SignUpController.class);

	@Autowired
	private RegistrationService registrationService;

	/**
	 * The Sign up method is used to register organization and user
	 * @param signUpRequestDTO
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/auth/signup", method = RequestMethod.POST)
	public RestResponse signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) throws PurpleException {

		final String SIGN_UP_SUCCESS_MSG = "Successfully signed up";
		// we assume the angular layer will do empty/null org name , user email
		// etc.. validation
		String orgId = signUpRequestDTO.getOrgName().replaceAll("\\s+", "_").trim();

		Organization organization = new Organization(orgId, signUpRequestDTO.getOrgName().trim());
		organization.setBusinessArea(signUpRequestDTO.getBusinessArea());

		User user = new User(orgId, signUpRequestDTO.getEmail());
		user.setEmail(signUpRequestDTO.getEmail());
		user.setPassword(signUpRequestDTO.getPassword());
		user.setUserRole(UserRole.ADMIN_USER_ROLE);
		user.setOrganization(organization);

		registrationService.signUp(organization, user);

		RestResponse signUpResponse = new RestResponse(RestResponse.SUCCESS, SIGN_UP_SUCCESS_MSG,
				SuccessHandler.SIGN_UP_SUCCESS);

		return signUpResponse;

	}
}
