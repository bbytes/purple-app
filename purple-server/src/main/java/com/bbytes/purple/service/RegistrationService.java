package com.bbytes.purple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.TenancyContextHolder;

@Service
public class RegistrationService {

	@Autowired
	private OrganizationService orgService;

	@Autowired
	private UserService userService;

	@Autowired
	private TenantResolverService tenantResolverService;

	public void signUp(Organization org, User user) throws PurpleException {

		if (org != null && user != null) {

			if (tenantResolverService.emailExist(user.getEmail()))
				throw new PurpleException("Error while sign up", ErrorHandler.EMAIL_NOT_UNIQUE);

			if (tenantResolverService.organizationExist(org.getOrgId()))
				throw new PurpleException("Error while sign up", ErrorHandler.ORG_NOT_UNIQUE);

			try {
				TenancyContextHolder.setTenant(org.getOrgId());
				orgService.save(org);
				userService.create(user.getEmail(), user.getName(), user.getPassword(), user.getOrganization());
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.SIGN_UP_FAILED, e);
			}
		}
	}

	public User activateAccount(User activeUser) throws PurpleException {

		if (activeUser != null) {
			try {
				activeUser.setAccountInitialise(true);
				activeUser.setStatus(User.JOINED);
				userService.save(activeUser);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.AUTH_FAILURE);
			}
		}
		return activeUser;
	}
	
	public User resendActivation(String email) throws PurpleException {
		User user = null;
		if (email != null && !email.isEmpty()) {
			String orgId = tenantResolverService.findTenantIdForUserEmail(email);
			TenancyContextHolder.setTenant(orgId);
			if (!userService.userEmailExist(email))
				throw new PurpleException("Error while resend activation link", ErrorHandler.USER_NOT_FOUND);
			try {
				user = userService.getUserByEmail(email);
				
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.RESEND_ACTIVATION_FAILURE);
			}
		}
		return user;
	}
}
