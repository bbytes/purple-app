package com.bbytes.purple.repository;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import com.bbytes.purple.PurpleApplicationTests;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.TenancyContextHolder;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserRepositoryTest  extends PurpleApplicationTests {

	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrganizationRepository orgRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	Organization sample, test;
	
	User user1, user2, user3;
	
	@Before
	public void setUp()
	{
		sample = new Organization("sample", "sample123");
		test = new Organization("test", "test123");
		
		user1 = new User("user1", "abc@gmail");
		user1.setOrganization(sample);
		user1.setPassword("122");
		
		user2 = new User("user2", "test@gmail");
		user2.setOrganization(sample);
		user2.setPassword("2222");
		
		user3 = new User("user3", "xyz@gmail");
		user3.setOrganization(test);
		user3.setPassword("3333");

		TenancyContextHolder.setTenant(user1.getOrganization().getOrgId());
		orgRepository.save(sample);
		userService.save(user1);
		
		TenancyContextHolder.setTenant(user2.getOrganization().getOrgId());
		orgRepository.save(sample);
		userService.save(user2);
		
		TenancyContextHolder.setTenant(user3.getOrganization().getOrgId());
		orgRepository.save(test);
		userService.save(user3);
	}
	
	@After
	public void cleanUp()
	{
		TenancyContextHolder.setTenant(user1.getOrganization().getOrgId());
		userService.deleteAll();
		orgRepository.deleteAll();
		
		TenancyContextHolder.setTenant(user2.getOrganization().getOrgId());
		userService.deleteAll();
		orgRepository.deleteAll();

		TenancyContextHolder.setTenant(user3.getOrganization().getOrgId());
		userService.deleteAll();
		orgRepository.deleteAll();
		
	}
	
	@Test
	public void saveUsersTest()
	{

		TenancyContextHolder.setTenant(user1.getOrganization().getOrgId());
		userService.save(user1);
		assertNotNull(user1.getUserId());

		TenancyContextHolder.setTenant(user2.getOrganization().getOrgId());
		userService.save(user2);
		assertNotNull(user2.getUserId());

		TenancyContextHolder.setTenant(user3.getOrganization().getOrgId());
		userService.save(user3);
		assertNotNull(user3.getUserId());
		
		
	}
	
	@Test
	public void deleteUserTest()
	{
		TenancyContextHolder.setTenant(user2.getOrganization().getOrgId());
		
		User user = userService.getUserByEmail("test@gmail");
		userService.delete(user);
		
		assertThat(userService.getUserByEmail("test@gmail"), is(nullValue()));
	}
	
	@Test
	public void getUserListTest()
	{
		TenancyContextHolder.setTenant(user1.getOrganization().getOrgId());
		
		List<User> userList = userRepository.findAll();
		
		assertThat(userList.size(), is(2));
	}
	
	@Test
	public void updateUserTest()
	{
		TenancyContextHolder.setTenant(user3.getOrganization().getOrgId());
		
		assertNotNull(user3.getUserId());
		
		User updateUser = userRepository.findOne(user3.getUserId());
		updateUser.setEmail("akshay@gmail");
		
		userService.save(updateUser);
		assertEquals("akshay@gmail", updateUser.getEmail());
	}
}
