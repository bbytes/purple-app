package com.bbytes.purple.service;

import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bbytes.purple.PurpleBaseApplicationTests;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.utils.TenancyContextHolder;

public class TestStatusService extends PurpleBaseApplicationTests {

	Organization org1;
	User testUser;
	Project project1;
	Status status1, status2;

	@Before
	public void init() {
		org1 = new Organization("google", "google-org");

	
		testUser = new User("akshay", "star@gmail");
		testUser.setOrganization(org1);
		testUser.setUserRole(UserRole.ADMIN_USER_ROLE);

		project1 = new Project("web", "2pm");
		project1.setOrganization(org1);

		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());
		organizationService.save(org1);
		userService.save(testUser);
		projectService.save(project1);

	}

	@After
	public void cleanup() {
		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());
		organizationService.deleteAll();
		userService.deleteAll();
		projectService.deleteAll();

	}

	@Test
	public void testSave() {

		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());
		status1 = new Status("testcase2", "testcase1", 8, new Date());
		status1.setProject(project1);
		status1.setUser(testUser);

		status1 = statusService.save(status1);

		Assert.assertTrue(status1.getStatusId() != null);

	}

}
