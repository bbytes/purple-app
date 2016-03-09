package com.bbytes.purple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.repository.UserRoleRepository;

@Service
public class UserRoleService {

	@Autowired
	private UserRoleRepository userRoleRepository;
	
	public UserRole getRoleById(String roleId)
	{
		return userRoleRepository.findOne(roleId);
	}
	
	public UserRole getRoleByName(String roleName)
	{
		return userRoleRepository.findByRoleName(roleName);
	}
}
