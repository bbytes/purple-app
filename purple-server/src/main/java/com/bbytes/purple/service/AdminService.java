package com.bbytes.purple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.utils.ErrorHandler;

@Service
public class AdminService {

	@Autowired
	private UserService userService;

	@Autowired
	private TenantResolverService tenantResolverService;

	public User addUsers(User user) throws PurpleException {

		if (user != null) {
			if (userService.userEmailExist(user.getEmail()) || tenantResolverService.emailExist(user.getEmail()))
				throw new PurpleException("Error while adding users", ErrorHandler.USER_NOT_FOUND);
			try {
				userService.save(user);
				user = userService.getUserByEmail(user.getEmail());
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.ADD_USER_FAILED);
			}
		}
		return user;
	}
}
