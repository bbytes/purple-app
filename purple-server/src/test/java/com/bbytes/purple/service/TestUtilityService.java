package com.bbytes.purple.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.UUID;

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

public class TestUtilityService extends PurpleBaseApplicationTests {

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

		project1 = new Project("web");
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

	@SuppressWarnings("resource")
	@Test
	public void testCSV() throws Exception {

		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());
		status1 = new Status("<ul><li>REV-3701 : Report Mail Template issue due to locale string</li></ul><p></p>", "testcase1", 8, new Date());
		status1.setProject(project1);
		status1.setUser(testUser);
		status1 = statusService.save(status1);
		
		status2 = new Status("testcase2", "testcase1", 8, new Date());
		status2.setProject(project1);
		status2.setUser(testUser);
		status2 = statusService.save(status2);
		
		File csv = utilityService.getCSV(UUID.randomUUID().toString(), statusService.findAll());
		Assert.assertTrue(csv.length() > 0);
		String line = null;
		 while ((line = new BufferedReader(new FileReader(csv)).readLine()) != null) {
		   System.out.println(line);
		 }

	}

}
