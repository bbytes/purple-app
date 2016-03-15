package com.bbytes.purple.repository;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
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
		user1.setUserRole(UserRole.ADMIN_USER_ROLE);
		
		
		TenancyContextHolder.setTenant(user1.getOrganization().getOrgId());
		organizationRepository.save(org1);
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
		userService.save(user1);
		
		assertThat(user1.getUserRole().getRoleName(), is(notNullValue()));
	}
	
	@Test
	public void updateUserRoleTest()
	{
		TenancyContextHolder.setTenant(user1.getOrganization().getOrgId());
		
		User updateUser = userService.getUserById(user1.getUserId());
		updateUser.setUserRole(new UserRole("superUser"));
		
		userService.save(updateUser);
		assertThat(userService.findOne(user1.getUserId()).getUserRole().getRoleName(), is("superUser"));
		
	}
	
	@Test
	public void getRoleTest()
	{
		TenancyContextHolder.setTenant(user1.getOrganization().getOrgId());
		
		User role = userService.findOne(user1.getUserId());
		
		assertThat(role.getUserRole(), is(notNullValue()));
	}
	
	@Test
	public void setPermissionToRoleTest()
	{
		TenancyContextHolder.setTenant(user1.getOrganization().getOrgId());
		
		List<Permission> list = new ArrayList<Permission>();
		list.add(new Permission("read"));
		list.add(new Permission("write"));
		
		user1.getUserRole().setPermissions(list);
		userService.save(user1);
		
		assertThat(userService.findOne(user1.getUserId()).getUserRole().getPermissions(), is(notNullValue()));
	}
}
