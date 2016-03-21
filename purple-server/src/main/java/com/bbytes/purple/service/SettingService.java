package com.bbytes.purple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.PasswordDTO;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.TenancyContextHolder;

@Service
public class SettingService {

	@Autowired
	private UserService userService;

	@Autowired
	private TenantResolverService tenantResolverService;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private PasswordHashService passwordHashService;

	public void resetPassword(PasswordDTO passwordDTO, User user) throws PurpleException {

		if (user != null && passwordDTO != null) {
			if (userService.userEmailExist(user.getEmail()) || tenantResolverService.emailExist(user.getEmail()))
				throw new PurpleException("Error while resetting password", ErrorHandler.USER_NOT_FOUND);
			if (passwordHashService.passwordMatches(passwordHashService.encodePassword(passwordDTO.getOldPassword()),
					user.getPassword()))
				throw new PurpleException("Error while resetting password", ErrorHandler.USER_NOT_FOUND);
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
			if (userService.userEmailExist(user.getEmail()) || tenantResolverService.emailExist(user.getEmail()))
				throw new PurpleException("Error while resetting password", ErrorHandler.USER_NOT_FOUND);
			try {
				Organization organization = organizationService.findByOrgId(TenancyContextHolder.getTenant());
				organization.setTimePreference(timeZone);
				organizationService.save(organization);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.PASSWORD_INCORRECT);
			}
		}
	}

}