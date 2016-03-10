package com.bbytes.purple.repository;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import com.bbytes.purple.PurpleApplicationTests;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Permission;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.TenancyContextHolder;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserRoleRepositoryTest  extends PurpleApplicationTests{

	@Autowired
	private UserRoleRepository userRoleRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	Organization org1;
	User user1;
	UserRole admin;
	
	@Before
	public void setUp()
	{
		admin = (UserRole.ADMIN_USER_ROLE); 
		
		org1 = new Organization("bb", "bb-company");
		user1 = new User("hitman", "hitman@gmail");
		user1.setOrganization(org1);
		user1.setUserRole(admin);
		
		
		TenancyContextHolder.setTenant(user1.getOrganization().getOrgId());
		organizationRepository.save(org1);
		userRoleRepository.save(admin);
		userService.save(user1);
		
	}
	
	@After
	public void cleanUp()
	{
		TenancyContextHolder.setTenant(user1.getOrganization().getOrgId());
		
		userRoleRepository.deleteAll();
		userService.deleteAll();
		organizationRepository.deleteAll();
	}
	
	@Test
	public void saveUserRoleTest()
	{
		TenancyContextHolder.setTenant(user1.getOrganization().getOrgId());
		userRoleRepository.save(admin);
		
		assertThat(org1.getOrgId(), is(notNullValue()));
	}
	
	@Test
	public void deleteUserRoleTest()
	{
		TenancyContextHolder.setTenant(user1.getOrganization().getOrgId());
		
		UserRole role = userRoleRepository.findOne(admin.getRoleId());
		
		userRoleRepository.delete(role);
		assertFalse(userRoleRepository.exists(admin.getRoleId()));
		User deleteuserRole = userService.getUserById(user1.getUserId());
		userService.save(deleteuserRole);
		assertNull(deleteuserRole.getUserRole());
	}
	
	@Test
	public void updateUserRoleTest()
	{
		TenancyContextHolder.setTenant(user1.getOrganization().getOrgId());
		
		UserRole role = userRoleRepository.findOne(admin.getRoleId());
		role.setRoleName("superuser");
		
		userRoleRepository.save(role);
		assertThat(userRoleRepository.findOne(admin.getRoleId()).getRoleName(), is("superuser"));
		
	}
	
	@Test
	public void getAllRolesTest()
	{
		TenancyContextHolder.setTenant(user1.getOrganization().getOrgId());
		
		List<UserRole> rolesList = userRoleRepository.findAll();
		
		assertThat(rolesList.size(), is(1));
	}
	
	@Test
	public void setPermissionToRoleTest()
	{
		TenancyContextHolder.setTenant(user1.getOrganization().getOrgId());
		
		List<Permission> list = new ArrayList<Permission>();
		list.add(new Permission("read"));
		list.add(new Permission("write"));
		
		admin.setPermissions(list);
		userRoleRepository.save(admin);
		
		assertThat(userRoleRepository.findOne(admin.getRoleId()).getPermissions(), is(notNullValue()));
	}
}
