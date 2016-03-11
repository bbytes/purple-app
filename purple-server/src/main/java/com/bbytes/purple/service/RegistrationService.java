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

		try {
			TenancyContextHolder.setTenant(org.getOrgId());
			
			if (org != null && user != null) {
				if (orgService.exists(org.getOrgId()) || tenantResolverService.organizationExist(org.getOrgId()))
					throw new PurpleException("Error while sign up", ErrorHandler.ORG_NOT_UNIQUE);
				
				if (userService.exists(user.getEmail()) || tenantResolverService.emailExist(user.getEmail()))
					throw new PurpleException("Error while sign up", ErrorHandler.USER_NOT_FOUND);

				orgService.save(org);
				userService.save(user);
			}
		} catch (Exception e) {
			throw new PurpleException("Error while sign up", ErrorHandler.SIGN_UP_FAILED, e);
		}
	}

}
