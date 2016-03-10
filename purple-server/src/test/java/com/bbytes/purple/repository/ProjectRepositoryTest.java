package com.bbytes.purple.repository;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
		
		proj1 = new Project("purple", "5.00 PM");
		proj1.setOrganization(bbytes);

		proj2 = new Project("reveal", "6.00 pm");
		proj2.setOrganization(bbytes);
		
		TenancyContextHolder.setTenant(proj1.getOrganization().getOrgId());
		orgRepository.save(bbytes);
	}

	@After
	public void cleanUp()
	{
		TenancyContextHolder.setTenant(proj1.getOrganization().getOrgId());
		projectRepository.deleteAll();
		orgRepository.deleteAll();
		
		TenancyContextHolder.setTenant(proj2.getOrganization().getOrgId());
		projectRepository.deleteAll();
		orgRepository.deleteAll();
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
		
		assertFalse(projectList.size() > 0);
	}
	
	@Test
	public void updateProjectTest()
	{
		assertNull(proj1.getProjectId());
		projectRepository.save(proj1);
		assertNotNull(proj1.getProjectId());
		
		Project projectObj = projectRepository.findOne(proj1.getProjectId());
		
		assertTrue(!projectObj.getProjectName().isEmpty());
		projectObj.setProjectName("hello");
		projectRepository.save(projectObj);
		
		assertThat(projectObj.getProjectName(),is("hello"));
		
	}
	
	@Test(expected=NullPointerException.class)
	public void deleteProjectTest()
	{
		if(proj1.getProjectId() == null)
		{
			throw new NullPointerException();
		}
		projectRepository.delete(proj1.getProjectId());
	}
	
	@Test
	public void saveUsersinProjectTest()
	{
		TenancyContextHolder.setTenant(proj1.getOrganization().getOrgId());
		
		 projectRepository.save(proj1);
		
		 List<User> users = userRepository.findAll();
		 
		 proj1.setUser(users);
		 projectRepository.save(proj1);
		 
		 assertTrue(proj1.getUser().size() > 0);
		 
	}
	
}
