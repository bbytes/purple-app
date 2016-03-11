package com.bbytes.purple.repository;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import com.bbytes.purple.PurpleApplicationTests;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.utils.TenancyContextHolder;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProjectRepositoryTest extends PurpleApplicationTests {
	
	@Autowired
	private OrganizationRepository orgRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	Organization bbytes;
	Project proj1, proj2;
	User user1, user2;
	
	@Before
	public void setUp()
	{
		bbytes = new Organization("bbytes", "BB-Org");
		user1 = new User("aaa", "aa@gmail");
		user1.setOrganization(bbytes);
		user2 = new User("bbb", "bb@gmail");
		user2.setOrganization(bbytes);
		
		proj1 = new Project("purple", "5.00 PM");
		proj1.setOrganization(bbytes);

		proj2 = new Project("reveal", "6.00 pm");
		proj2.setOrganization(bbytes);
		
		TenancyContextHolder.setTenant(proj1.getOrganization().getOrgId());
		orgRepository.deleteAll();
		projectRepository.deleteAll();
		userRepository.deleteAll();
		
		orgRepository.save(bbytes);
		projectRepository.save(proj1);
		projectRepository.save(proj2);
	}

	@After
	public void cleanUp()
	{
		TenancyContextHolder.setTenant(proj1.getOrganization().getOrgId());
		projectRepository.deleteAll();
		orgRepository.deleteAll();
		userRepository.deleteAll();
		
		TenancyContextHolder.setTenant(proj2.getOrganization().getOrgId());
		projectRepository.deleteAll();
		orgRepository.deleteAll();
		userRepository.deleteAll();
	}
	
	@Test
	public void saveProjectTest()
	{
		TenancyContextHolder.setTenant(proj1.getOrganization().getOrgId());
		projectRepository.save(proj1);
		
		TenancyContextHolder.setTenant(proj2.getOrganization().getOrgId());
		projectRepository.save(proj2);
	
		assertThat(proj1.getProjectId(), is(notNullValue()));
	}
	
	@Test
	public void findAllProjectTest()
	{
		TenancyContextHolder.setTenant(proj1.getOrganization().getOrgId());
		List<Project> projectList = projectRepository.findAll();
		
		assertTrue(projectList.size() > 0);
	}
	
	@Test
	public void updateProjectTest()
	{
		TenancyContextHolder.setTenant(proj1.getOrganization().getOrgId());
		Project projectObj = projectRepository.findOne(proj1.getProjectId());
		
		assertTrue(!projectObj.getProjectName().isEmpty());
		projectObj.setProjectName("hello");
		projectRepository.save(projectObj);
		
		assertThat(projectObj.getProjectName(),is("hello"));
		
	}
	
	@Test
	public void deleteProjectTest()
	{
		TenancyContextHolder.setTenant(proj1.getOrganization().getOrgId());
		projectRepository.delete(proj1.getProjectId());
		
		assertNull(projectRepository.findOne(proj1.getProjectId()));
	}
	
	@Test
	public void saveUsersinProjectTest()
	{
		TenancyContextHolder.setTenant(proj1.getOrganization().getOrgId());
		
		userRepository.save(user1);
		userRepository.save(user2);
		List<User> users = userRepository.findAll();
		 
		proj1.setUser(users);
		projectRepository.save(proj1);
		 
		assertTrue(proj1.getUser().size() > 0);
		 
	}
	
}
