package com.bbytes.purple.repository.event;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bbytes.purple.PurpleApplicationTests;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.repository.OrganizationRepository;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.TenancyContextHolder;

public class MultiTenantUserRepositoryEventTest extends PurpleApplicationTests {

	@Autowired
	UserService userService;
	@Autowired
	OrganizationRepository organizationRepository;

	User admin1;
	Organization test;
	UserRole role;

	@Before
	public void setUp() {
		test = new Organization("test", "Test-Org");

		admin1 = new User("admin-1", "admin@test.com");
		admin1.setOrganization(test);
		admin1.setPassword("test123");

		role = UserRole.NORMAL_USER_ROLE;
	}

	@After
	public void cleanUp() {
		TenancyContextHolder.setTenant(admin1.getOrganization().getOrgId());
		userService.deleteAll();
		organizationRepository.deleteAll();
	}

	@Test
	public void checkUserCreationDateUpdateEvent() {
		TenancyContextHolder.setTenant(admin1.getOrganization().getOrgId());
		test = organizationRepository.save(test);
		admin1.setOrganization(test);
		admin1.setUserRole(role);
		admin1 = userService.save(admin1);
		assertThat(admin1.getCreationDate(), is(notNullValue()));
	}

	@Test
	public void checkOrgCreationDateUpdateEvent() {
		TenancyContextHolder.setTenant(test.getOrgId());
		test = organizationRepository.save(test);
		assertThat(test.getCreationDate(), is(notNullValue()));
	}

}