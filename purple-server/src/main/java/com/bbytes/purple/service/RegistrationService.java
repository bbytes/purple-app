package com.bbytes.purple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.utils.TenancyContextHolder;

@Service
public class RegistrationService {

	@Autowired
	private OrganizationService orgService;

	@Autowired
	private UserService userService;

	public void signUp(Organization org, User user) throws PurpleException {

		try {
			if (org != null && user != null) {
				TenancyContextHolder.setTenant(user.getOrganization().getOrgId());
				orgService.save(org);
				userService.save(user);
			}
		} catch (Exception e) {
			throw new PurpleException("Error while sign up", e);
		}
	}

}
