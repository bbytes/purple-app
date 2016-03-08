package com.bbytes.purple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordHashService passwordHashService;


	public User getUserById(String id) {
		return userRepository.findOne(id);
	}

	public User getUserByEmail(String email) {
		return userRepository.findOneByEmail(email);
	}

	public Iterable<User> getAllUsers() {
		return userRepository.findAll(new Sort("email"));
	}

	public User save(User user) {
		return userRepository.save(user);
	}

	public void delete(User user) {
		userRepository.delete(user);
	}

	public void deleteAll() {
		userRepository.deleteAll();
	}

	public User create(String email, String name, Organization org) {
		return null;
//		User user = new User(name, email);
//		user.setTenantId(tenant.getTenantId());
//		return userRepository.save(user);
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

}