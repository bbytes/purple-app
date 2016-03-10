package com.bbytes.purple.service;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bbytes.purple.PurpleApplicationTests;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.utils.TenancyContextHolder;


public class TestStatusService extends PurpleApplicationTests {

	@Autowired
	private StatusService statusService;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private UserService userService;

	@Autowired
	private ProjectService projectService;

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

	@Test
	public void testSave() {

		status1 = new Status("testcase2", "testcase1", 8, new DateTime());
		status1.setProject(project1);
		status1.setUser(testUser);

		status1 = statusService.save(status1);

		Assert.assertTrue(status1.getStatusId() != null);

	}

}
