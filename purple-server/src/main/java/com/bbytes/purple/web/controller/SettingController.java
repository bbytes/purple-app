package com.bbytes.purple.web.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.purple.auth.jwt.TokenAuthenticationProvider;
import com.bbytes.purple.domain.ConfigSetting;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.ConfigSettingDTO;
import com.bbytes.purple.rest.dto.models.ConfigSettingResponseDTO;
import com.bbytes.purple.rest.dto.models.PasswordDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.rest.dto.models.SettingDTO;
import com.bbytes.purple.rest.dto.models.UserDTO;
import com.bbytes.purple.service.ConfigSettingService;
import com.bbytes.purple.service.DataModelToDTOConversionService;
import com.bbytes.purple.service.NotificationService;
import com.bbytes.purple.service.SettingService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.SuccessHandler;

/**
 * Setting Controller
 * 
 * @author akshay
 *
 */
@RestController
public class SettingController {

	private static final Logger logger = LoggerFactory.getLogger(SettingController.class);

	@Autowired
	private SettingService settingService;

	@Autowired
	private ConfigSettingService configSettingService;

	@Autowired
	private DataModelToDTOConversionService dataModelToDTOConversionService;

	@Autowired
	protected TokenAuthenticationProvider tokenAuthenticationProvider;

	@Autowired
	private UserService userService;

	@Autowired
	private NotificationService notificationService;

	@Value("${base.url}")
	private String baseUrl;
	
	@Value("${email.forgot.password.subject}")
	private String forgotPasswordSubject;

	/**
	 * The reset password method is used to reset password for admin as well as
	 * user
	 * 
	 * @param passwordDTO
	 * @param request
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/user/setting/password", method = RequestMethod.POST)
	public RestResponse resetPassword(@RequestBody PasswordDTO passwordDTO) throws PurpleException {

		// we assume angular side will take care for password validation
		final String PASSWORD_RESET_SUCCESS_MSG = "Successfully reset password";
		User user = userService.getLoggedInUser();
		settingService.resetPassword(passwordDTO, user);

		logger.debug("User with email  '" + user.getEmail() + "' is reset password successfully");
		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, PASSWORD_RESET_SUCCESS_MSG,
				SuccessHandler.RESET_PASSWORD);

		return userReponse;
	}

	/**
	 * The updateSetting method is used to update timezone and timePreference
	 * for users.
	 * 
	 * @param timeZone
	 * @param timePreference
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/setting", method = RequestMethod.POST)
	public RestResponse updateSetting(@RequestBody SettingDTO settingDTO) throws PurpleException {

		User user = userService.getLoggedInUser();
		user = settingService.updateSetting(settingDTO.getTimeZone(), settingDTO.getTimePreference(),settingDTO.getEmailNotificationState(), user);
		UserDTO responseDTO = dataModelToDTOConversionService.convertUser(user);
		logger.debug("Setting is updated successfully");
		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, responseDTO, SuccessHandler.UPDATE_SETTING);

		return userReponse;
	}

	/**
	 * The notificationSetting method is used to update config settings
	 * 
	 * @param notificationSettingDTO
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/admin/configSetting/update", method = RequestMethod.POST)
	public RestResponse updateConfigSetting(@RequestBody ConfigSettingDTO configSettingDTO) throws PurpleException {

		Organization organization = userService.getLoggedInUser().getOrganization();
		ConfigSetting notificationSetting = configSettingService.saveNotification(configSettingDTO, organization);
		ConfigSettingResponseDTO notificationMap = dataModelToDTOConversionService
				.getResponseMapWithGridDataAndNotification(notificationSetting);

		logger.debug("Config settings are saved successfully");
		RestResponse settingMap = new RestResponse(RestResponse.SUCCESS, notificationMap,
				SuccessHandler.NOTIFICATION_SUCCESS);

		return settingMap;
	}

	/**
	 * The notificationSetting method is used to get all config settings
	 * 
	 * @param notificationSettingDTO
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/admin/configSetting", method = RequestMethod.GET)
	public RestResponse getConfigSetting() throws PurpleException {

		Organization organization = userService.getLoggedInUser().getOrganization();
		ConfigSetting notificationSetting = configSettingService.getConfigSetting(organization);
		ConfigSettingResponseDTO notificationMap = dataModelToDTOConversionService
				.getResponseMapWithGridDataAndNotification(notificationSetting);
		logger.debug("Config settings is fetched successfully");
		RestResponse settingMap = new RestResponse(RestResponse.SUCCESS, notificationMap,
				SuccessHandler.NOTIFICATION_SUCCESS);

		return settingMap;
	}

	/**
	 * The forgotPassword method is used to send the link of reset password
	 * 
	 * @param email
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/auth/forgotPassword", method = RequestMethod.GET)
	public RestResponse forgotPassword(@RequestParam String email) throws PurpleException {

		final String FORGOT_PASSWORD_SUCCESS_MSG = "Forgot password link is successfully sent to your register email address";
		final String template = GlobalConstants.EMAIL_FORGOT_PASSWORD_TEMPLATE;

		User user = settingService.forgotPassword(email);
		final String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(user.getEmail(), 168);
		List<String> emailList = new ArrayList<String>();
		emailList.add(email);

		Map<String, Object> emailBody = new HashMap<>();
		emailBody.put(GlobalConstants.USER_NAME, user.getName());
		emailBody.put(GlobalConstants.ACTIVATION_LINK, baseUrl + GlobalConstants.FORGOT_PASSWORD_URL + xauthToken);

		notificationService.sendTemplateEmail(emailList, forgotPasswordSubject, template, emailBody);

		logger.debug("Forgot password is done successfully");
		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, FORGOT_PASSWORD_SUCCESS_MSG,
				SuccessHandler.FORGOT_PASSWORD_SUCCESS);

		return userReponse;
	}
}
