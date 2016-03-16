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

		TenancyContextHolder.setTenant(org.getOrgId());

		if (org != null && user != null) {
			if (orgService.orgIdExist(org.getOrgId()) || tenantResolverService.organizationExist(org.getOrgId()))
				throw new PurpleException("Error while sign up", ErrorHandler.ORG_NOT_UNIQUE);

			if (userService.userEmailExist(user.getEmail()) || tenantResolverService.emailExist(user.getEmail()))
				throw new PurpleException("Error while sign up", ErrorHandler.EMAIL_NOT_UNIQUE);
			try {
				orgService.save(org);
				userService.create(user.getEmail(), user.getName(), user.getPassword(), user.getOrganization());
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.SIGN_UP_FAILED);
			}

		}
	}

}
