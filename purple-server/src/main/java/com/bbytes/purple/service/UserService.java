package com.bbytes.purple.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.repository.UserRepository;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.TenancyContextHolder;

@Service
public class UserService extends AbstractService<User, String> {

	@Autowired
	private PasswordHashService passwordHashService;

	private UserRepository userRepository;

	@Autowired
	public UserService(UserRepository userRepository) {
		super(userRepository);
		this.userRepository = userRepository;
	}

	public boolean userEmailExist(String email) {
		boolean state = userRepository.findOneByEmail(email) == null ? false : true;
		return state;
	}

	public User getUserById(String id) {
		return userRepository.findOne(id);
	}

	public User getUserByEmail(String email) {
		return userRepository.findOneByEmail(email);
	}

	public Iterable<User> getAllUsers() {
		return userRepository.findAll(new Sort("email"));
	}

	public void delete(User user) {
		userRepository.delete(user);
	}

	public void deleteAll() {
		userRepository.deleteAll();
	}

	public User create(String email, String name, Organization org) {
		User user = new User(name, email);
		user.setOrganization(org);
		return userRepository.save(user);
	}

	public User create(String email, String name, String password, Organization org) {
		User user = new User(name, email);
		user.setOrganization(org);
		user.setPassword(passwordHashService.encodePassword(password));
		user.setUserRole(UserRole.ADMIN_USER_ROLE);
		return userRepository.save(user);
	}

	public boolean updatePassword(String password, String userEmail) {
		User user = userRepository.findOneByEmail(userEmail);
		return updatePassword(password, user);
	}

	public boolean updatePassword(String password, User user) {
		if (user != null && password != null) {
			user.setPassword(passwordHashService.encodePassword(password));
			userRepository.save(user);
			return true;
		}

		return false;
	}

	public User getRequestUser(HttpServletRequest request) {
		if (request == null || request.getHeader(GlobalConstants.USER_EMAIL) == null)
			return null;

		String email = request.getHeader(GlobalConstants.USER_EMAIL);
		User user = userRepository.findOneByEmail(email);
		return user;
	}
}