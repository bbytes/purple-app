package com.bbytes.purple.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Holiday;
import com.bbytes.purple.domain.NotificationSetting;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.repository.NotificationSettingRepository;
import com.bbytes.purple.rest.dto.models.NotificationDTO;
import com.bbytes.purple.rest.dto.models.PasswordDTO;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.TenancyContextHolder;

@Service
public class SettingService {

	@Autowired
	private UserService userService;

	@Autowired
	private NotificationSettingRepository notificationSettingRepository;

	@Autowired
	private PasswordHashService passwordHashService;

	@Autowired
	private TenantResolverService tenantResolverService;

	public void resetPassword(PasswordDTO passwordDTO, User user) throws PurpleException {

		if (user != null && passwordDTO != null) {
			if (!userService.userEmailExist(user.getEmail()))
				throw new PurpleException("Error while resetting password", ErrorHandler.USER_NOT_FOUND);
			if (passwordDTO.getOldPassword() != null
					&& !passwordHashService.passwordMatches(passwordDTO.getOldPassword(), user.getPassword()))
				throw new PurpleException("Error while resetting password", ErrorHandler.PASSWORD_MISMATCH);
			try {
				user.setPassword(passwordHashService.encodePassword(passwordDTO.getNewPassword()));
				userService.save(user);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.PASSWORD_INCORRECT);
			}
		}
	}

	public void updateTimeZone(String timeZone, User user) throws PurpleException {
		if (user != null && timeZone != null && !timeZone.isEmpty()) {
			if (!userService.userEmailExist(user.getEmail()))
				throw new PurpleException("Error while resetting password", ErrorHandler.USER_NOT_FOUND);
			try {
				user.setTimeZone(timeZone);
				userService.save(user);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.PASSWORD_INCORRECT);
			}
		}
	}

	public NotificationSetting saveNotification(NotificationDTO notificationSettingDTO, Organization org)
			throws PurpleException {
		List<Holiday> holidayList = new ArrayList<Holiday>();
		NotificationSetting notificationSetting = new NotificationSetting();
		if (notificationSettingDTO != null) {
			DateFormat format = new SimpleDateFormat(GlobalConstants.DATE_HOLIDAY_FORMAT);
			try {
				for (String holidaydate : notificationSettingDTO.getHolidayDate()) {
					Holiday holiday = new Holiday(format.parse(holidaydate));
					holidayList.add(holiday);
				}
				notificationSetting.setCaptureHours(notificationSettingDTO.isCaptureHours());
				notificationSetting.setWeekendNotification(notificationSettingDTO.isWeekendNotification());
				notificationSetting.setStatusEnable(notificationSettingDTO.getStatusEnable());
				notificationSetting.setOrganization(org);
				notificationSetting.setHolidays(holidayList);
				notificationSettingRepository.save(notificationSetting);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.NOTIFICATION_FAILED);
			}
		}
		return notificationSetting;
	}

	public User forgotPassword(String email) throws PurpleException {
		User user = null;
		if (email != null && !email.isEmpty()) {
			String orgId = tenantResolverService.findTenantIdForUserEmail(email);
			TenancyContextHolder.setTenant(orgId);
			if (!userService.userEmailExist(email))
				throw new PurpleException("Error while forgetting password", ErrorHandler.USER_NOT_FOUND);
			try {
				user = userService.getUserByEmail(email);
				if (!user.isAccountInitialise())
					throw new Exception();
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.ACCOUNT_ACTIVATION_FAILURE);
			}
		}
		return user;
	}
}
