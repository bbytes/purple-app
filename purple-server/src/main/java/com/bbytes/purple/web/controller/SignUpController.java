package com.bbytes.purple.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.purple.auth.jwt.TokenAuthenticationProvider;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.rest.dto.models.SignUpRequestDTO;
import com.bbytes.purple.rest.dto.models.UserDTO;
import com.bbytes.purple.service.DataModelToDTOConversionService;
import com.bbytes.purple.service.NotificationService;
import com.bbytes.purple.service.RegistrationService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.GlobalConstants;
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

	@Autowired
	protected TokenAuthenticationProvider tokenAuthenticationProvider;

	@Autowired
	private UserService userService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private DataModelToDTOConversionService dataModelToDTOConversionService;

	@Value("${base.url}")
	private String baseUrl;

	/**
	 * The Sign up method is used to register organization and user
	 * 
	 * @param signUpRequestDTO
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/auth/signup", method = RequestMethod.POST)
	public RestResponse signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) throws PurpleException {

		// we assume the angular layer will do empty/null org name , user email
		// etc.. validation
		final String SIGN_UP_SUCCESS_MSG = "Activation link is successfully sent to your register email address";

		String orgId = signUpRequestDTO.getOrgName().replaceAll("\\s+", "_").trim();

		Organization organization = new Organization(orgId, signUpRequestDTO.getOrgName().trim());
		organization.setBusinessArea(signUpRequestDTO.getBusinessArea());

		User user = new User(orgId, signUpRequestDTO.getEmail());
		user.setEmail(signUpRequestDTO.getEmail().toLowerCase());
		user.setPassword(signUpRequestDTO.getPassword());
		user.setOrganization(organization);
		List<String> emailList = new ArrayList<String>();
		emailList.add(user.getEmail());

		registrationService.signUp(organization, user);

		final String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(signUpRequestDTO.getEmail().toLowerCase(), 30);

		Map<String, Object> emailBody = new HashMap<>();
		emailBody.put(GlobalConstants.USER_NAME, user.getName());
		emailBody.put(GlobalConstants.SUBSCRIPTION_DATE, new Date());
		emailBody.put(GlobalConstants.ACTIVATION_LINK, baseUrl + GlobalConstants.TOKEN_URL + xauthToken);
		notificationService.sendTemplateEmail(emailList, GlobalConstants.EMAIL_ACTIVATION_SUBJECT,
				GlobalConstants.EMAIL_ACTIVATION_TEMPLATE, emailBody);

		logger.debug("User with email  '" + user.getEmail() + "' signed up successfully");

		RestResponse signUpResponse = new RestResponse(RestResponse.SUCCESS, SIGN_UP_SUCCESS_MSG,
				SuccessHandler.SIGN_UP_SUCCESS);

		return signUpResponse;

	}

	@RequestMapping(value = "/api/v1/admin/activateAccount", method = RequestMethod.GET)
	public RestResponse accountActivation() throws PurpleException {

		User user = userService.getLoggedinUser();
		User activeUser = registrationService.activateAccount(user);
		UserDTO responseDTO = dataModelToDTOConversionService.convertUser(activeUser);
		logger.debug("User with email  '" + user.getEmail() + "' signed up successfully");

		RestResponse activeResponse = new RestResponse(RestResponse.SUCCESS, responseDTO,
				SuccessHandler.SIGN_UP_SUCCESS);
		return activeResponse;
	}
}
