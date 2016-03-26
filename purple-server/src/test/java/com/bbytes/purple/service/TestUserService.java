package com.bbytes.purple.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import com.bbytes.purple.PurpleBaseApplicationTests;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.utils.TenancyContextHolder;

import io.jsonwebtoken.lang.Assert;

public class TestUserService extends PurpleBaseApplicationTests {

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private UserService userService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private TenantResolverService tenantResolverService;

	Organization org;
	Organization org2;
	User testUser;
	User testUser2;

	@Before
	public void init() {
		org = new Organization("testsame", "same-org");
		org2 = new Organization("testsame2", "same-org2");

		testUser = new User("testsamemail", "same@gmail");
		testUser.setOrganization(org);
		testUser.setUserRole(UserRole.ADMIN_USER_ROLE);

		testUser2 = new User("testsamemail", "same@gmail");
		testUser2.setOrganization(org2);
		testUser2.setUserRole(UserRole.ADMIN_USER_ROLE);

		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());
		organizationService.deleteAll();
		userService.deleteAll();
		projectService.deleteAll();

		TenancyContextHolder.setTenant(testUser2.getOrganization().getOrgId());
		organizationService.deleteAll();
		userService.deleteAll();
		projectService.deleteAll();

		TenancyContextHolder.setTenant(org.getOrgId());
		organizationService.save(org);
		TenancyContextHolder.setTenant(org2.getOrgId());
		organizationService.save(org2);

	}

	@After
	public void cleanup() {
		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());
		organizationService.deleteAll();
		userService.deleteAll();
		projectService.deleteAll();

		TenancyContextHolder.setTenant(testUser2.getOrganization().getOrgId());
		organizationService.deleteAll();
		userService.deleteAll();
		projectService.deleteAll();

	}

	@Test(expected = DuplicateKeyException.class)
	public void testSaveUserWithSameEmailUnderSameOrg() {

		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());
		userService.save(testUser);
		userService.save(testUser2);

	}

	@Test(expected = DuplicateKeyException.class)
	public void testSaveUserWithSameEmailUnderDiffOrg() {

		TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());
		userService.save(testUser);
		TenancyContextHolder.setTenant(testUser2.getOrganization().getOrgId());
		userService.save(testUser2);
	}

	@Test
	public void testDelete() throws InterruptedException {
		if(springProfileService.isSaasMode()){
			TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());
			userService.deleteAll();
			Assert.isNull(tenantResolverService.findOneByEmail(testUser.getEmail()));
			userService.save(testUser);
			Assert.notNull(tenantResolverService.findOneByEmail(testUser.getEmail()));
			userService.delete(testUser);
			Assert.isNull(tenantResolverService.findOneByEmail(testUser.getEmail()));	
		}else{
			TenancyContextHolder.setTenant(testUser.getOrganization().getOrgId());
			userService.deleteAll();
			Assert.isNull(tenantResolverService.findOneByEmail(testUser.getEmail()));
			userService.save(testUser);
			Assert.isNull(tenantResolverService.findOneByEmail(testUser.getEmail()));
			
		}
		
	}

}
