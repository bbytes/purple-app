package com.bbytes.purple.repository;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import com.bbytes.purple.PurpleApplicationTests;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.TenancyContextHolder;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MultiTenantOrgAndUserRepositoryTest extends PurpleApplicationTests {

	@Autowired
	private UserService userService;

	@Autowired
	private OrganizationRepository orgRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	User admin1, admin2;
	Organization test, abc;

	@Before
	public void setUp() {
		test = new Organization("test", "Test-Org");
		abc = new Organization("abc", "ABC-Org");

		admin1 = new User("admin-1", "admin@test.com");
		admin1.setOrganization(test);
		admin1.setPassword("test123");
		admin2 = new User("admin-2", "admin@abc.com");
		admin2.setOrganization(abc);
		admin2.setPassword("test123");

		TenancyContextHolder.setTenant(admin1.getOrganization().getOrgId());
		userService.deleteAll();
		userRoleRepository.save(UserRole.NORMAL_USER_ROLE);
		orgRepository.save(test);
		TenancyContextHolder.setTenant(admin2.getOrganization().getOrgId());
		userService.deleteAll();
		userRoleRepository.save(UserRole.NORMAL_USER_ROLE);
		orgRepository.save(abc);

	}

	@After
	public void cleanUp() {

		TenancyContextHolder.setTenant(admin1.getOrganization().getOrgId());
		userService.deleteAll();
		userRoleRepository.deleteAll();
		orgRepository.deleteAll();
		TenancyContextHolder.setTenant(admin2.getOrganization().getOrgId());
		userService.deleteAll();
		userRoleRepository.deleteAll();
		orgRepository.deleteAll();
		
		clearTestCaseMongoDatabases();
	}

	@Test
	public void saveOrgsTest() {
		Organization newOrg = new Organization("neworg", "New-Org");
		TenancyContextHolder.setTenant(newOrg.getOrgId());
		orgRepository.save(newOrg);
		assertThat(test.getOrgId(), is(notNullValue()));
		orgRepository.deleteAll();
	}

	@Test
	public void saveUserTest() {

		TenancyContextHolder.setTenant(admin1.getOrganization().getOrgId());
		userService.save(admin1);

		TenancyContextHolder.setTenant(admin2.getOrganization().getOrgId());
		userService.save(admin2);

		assertThat(admin1.getUserId(), is(notNullValue()));
	}


}