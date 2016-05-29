package com.bbytes.purple.repository;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.bbytes.purple.PurpleBaseApplicationTests;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.utils.TenancyContextHolder;

public class StatusRepositoryTest extends PurpleBaseApplicationTests {

	Organization org1;
	User testUser;
	User testUser2;
	Project project1;
	Project project2;
	Status status1, status2, status3;

	@Before
	public void setUp() {
		org1 = new Organization("google", "google-org");

		testUser = new User("akshay", "star@gmail");
		testUser.setOrganization(org1);
		testUser.setUserRole(UserRole.ADMIN_USER_ROLE);

		testUser2 = new User("akshay", "star55@gmail");
		testUser2.setOrganization(org1);
		testUser2.setUserRole(UserRole.ADMIN_USER_ROLE);

		project1 = new Project("web");
		project1.setOrganization(org1);
		
		project2 = new Project("web22");
		project2.setOrganization(org1);

		status1 = new Status("testcase2", "testcase1", 8, DateTime.now().minusDays(8).toDate());
		status1.setProject(project1);
		status1.setUser(testUser);

		status2 = new Status("t2", "t1", 4, new Date());
		status2.setProject(project1);
		status2.setUser(testUser);

		status3 = new Status("t2", "t1", 4, new Date());
		status3.setProject(project2);
		status3.setUser(testUser2);

		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());
		organizationRepository.deleteAll();
		userService.deleteAll();

		organizationRepository.save(org1);
		userService.save(testUser);
		userService.save(testUser2);
		projectRepository.save(project1);
		projectRepository.save(project2);
		statusRepository.save(status1);
		statusRepository.save(status2);
		statusRepository.save(status3);

	}

	@After
	public void cleanUp() {
		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());
		organizationRepository.deleteAll();
		projectRepository.deleteAll();
		userService.deleteAll();
		statusRepository.deleteAll();
	}

	@Test
	public void saveStatusTest() {
		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());

		statusRepository.save(status1);
		assertNotNull(status1.getStatusId());

		statusRepository.save(status2);
		assertNotNull(status2.getStatusId());
	}

	@Test
	public void getAllStatus() {
		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());
		List<Status> statusList = statusRepository.findAll();
		assertThat(statusList.size(), is(2));
	}

	@Test
	public void deleteStatusTest() {
		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());
		Status status = statusRepository.findOne(status1.getStatusId());
		statusRepository.delete(status);

		assertFalse(statusRepository.exists(status.getStatusId()));

	}

	@Test
	public void updateStatusTest() {
		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());

		assertNotNull(status1.getStatusId());

		Status updateStatus = statusRepository.findOne(status1.getStatusId());
		updateStatus.setBlockers("Issue");

		statusRepository.save(updateStatus);

		assertNotNull(updateStatus.getBlockers());
	}

	@Test
	public void testDateRangeUserQuery() {
		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());

		Date start = DateTime.now().withTime(0, 0, 0, 0).toDate();
		Date endDate = DateTime.now().withTime(23, 59, 59, 999).toDate();

		List<Status> statusList = statusRepository.findByDateTimeBetweenAndUser(start, endDate, testUser);

		assertTrue(statusList.size() == 1);

		start = DateTime.now().minusDays(10).withTime(0, 0, 0, 0).toDate();
		endDate = DateTime.now().withTime(23, 59, 59, 999).toDate();

		List<User> users = Arrays.asList(testUser, testUser2);

		statusList = statusRepository.findByDateTimeBetweenAndUserIn(start, endDate, users);

		assertTrue(statusList.size() == 3);
	}
	
	@Test
	public void testStatusForProjectListAndUserListQuery() {
		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());

		List<User> users = Arrays.asList(testUser, testUser2);
		List<Project> projects = Arrays.asList(project1, project2);
		List<Status> statusList = statusRepository.findByProjectInAndUserIn(projects,users);
		assertTrue(statusList.size() == 3);
		
		users = Arrays.asList(testUser, testUser2);
		projects = Arrays.asList(project1);
		statusList = statusRepository.findByProjectInAndUserIn(projects,users);
		assertTrue(statusList.size() == 2);
		
		// only users test 
		users = Arrays.asList(testUser, testUser2);
		statusList = statusRepository.findByUserIn(users);
		assertTrue(statusList.size() == 3);
	}

}
