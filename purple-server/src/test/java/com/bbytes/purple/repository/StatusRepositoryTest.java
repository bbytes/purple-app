package com.bbytes.purple.repository;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bbytes.purple.PurpleApplicationTests;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.TenancyContextHolder;

public class StatusRepositoryTest extends PurpleApplicationTests{

	@Autowired
	private StatusRepository statusRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired 
	private OrganizationRepository orgRepository;
	
	@Autowired
	private UserService userService;
	
	Organization org1;
	User testUser;
	Project project1;
	Status status1, status2;

	@Before
	public void setUp()
	{
		org1 = new Organization("google", "google-org");
		
		testUser = new User("akshay", "star@gmail");
		testUser.setOrganization(org1);
		testUser.setUserRole(UserRole.ADMIN_USER_ROLE);
		
		project1 = new Project("web", "2pm");
		project1.setOrganization(org1);
		
		status1 = new Status("testcase2", "testcase1", 8, new DateTime());
		status1.setProject(project1);
		status1.setUser(testUser);
		
		status2 = new Status("t2", "t1", 4, new DateTime());
		status2.setProject(project1);
		status2.setUser(testUser); 
		
		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());
		orgRepository.save(org1);
		userService.save(testUser);
		projectRepository.save(project1);
		statusRepository.save(status1);
		statusRepository.save(status2);
		
	}
	
	@After
	public void cleanUp()
	{
		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());
		orgRepository.deleteAll();
		projectRepository.deleteAll();
		userService.deleteAll();
		statusRepository.deleteAll();
	}
	
	@Test
	public void saveStatusTest()
	{
		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());
		
		statusRepository.save(status1);
		assertNotNull(status1.getStatusId());
		
		statusRepository.save(status2);
		assertNotNull(status2.getStatusId());
	}
	
	@Test
	public void getAllStatus()
	{
		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());
		
		List<Status> statusList = statusRepository.findAll();
		
		assertThat(statusList.size(), is(2));
	}
	
	@Test
	public void deleteStatusTest()
	{
		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());
		Status status = statusRepository.findOne(status1.getStatusId());
		statusRepository.delete(status);
		
		assertFalse(statusRepository.exists(status.getStatusId()));
		
	}
	
	@Test
	public void updateStatusTest()
	{
		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());
		
		assertNotNull(status1.getStatusId());
		
		Status updateStatus = statusRepository.findOne(status1.getStatusId());
		updateStatus.setBlockers("Issue");
		
		statusRepository.save(updateStatus);
		
		assertNotNull(updateStatus.getBlockers());
	}
}
