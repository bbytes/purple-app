package com.bbytes.purple.controller;

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
import com.bbytes.purple.rest.dto.models.SignupDTO;
import com.bbytes.purple.service.RegistrationService;

/**
 * Sign-up controller
 * 
 * @author akshay
 */
@RestController
@RequestMapping(value = "/auth")
public class SignUpController {

	@Autowired
	private RegistrationService regService;

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String signUp(@RequestBody SignupDTO dto) throws PurpleException {

		final Logger logger = LoggerFactory.getLogger(SignUpController.class);
		final String SUCCESS = "Successfully sign-up";

		String orgId = dto.getOrgName().replace("\\s", "").trim();

		try {
			Organization organization = new Organization(orgId, dto.getOrgName());
			organization.setBusinessArea(dto.getBusinessArea());

			User user = new User(orgId, dto.getEmail());
			user.setEmail(dto.getEmail());
			user.setPassword(dto.getPassword());
			user.setUserRole(UserRole.ADMIN_USER_ROLE);
			user.setOrganization(organization);

			regService.signUp(organization, user);

			return SUCCESS;

		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new PurpleException("Error while sign up", e);
		}
	}
}
