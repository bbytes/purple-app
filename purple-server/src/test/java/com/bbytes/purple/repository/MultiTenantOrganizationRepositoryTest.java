package com.bbytes.purple.repository;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import com.bbytes.purple.PurpleApplicationTests;
import com.bbytes.purple.database.MultiTenantDbFactory;
import com.bbytes.purple.domain.Organization;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MultiTenantOrganizationRepositoryTest extends PurpleApplicationTests{
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	Organization org1, org2;
	
	@Before
	public void setUp()
	{
		org1 = new Organization("abc", "abc-org");
		org2 = new Organization("xyz", "xyz-org");
		
		MultiTenantDbFactory.setDatabaseNameForCurrentThread(org1.getOrgId());
		organizationRepository.save(org1);
		
		MultiTenantDbFactory.setDatabaseNameForCurrentThread(org2.getOrgId());
		organizationRepository.save(org2);
	}
	
	@After
	public void cleanUp()
	{
		MultiTenantDbFactory.setDatabaseNameForCurrentThread(org1.getOrgId());
		organizationRepository.deleteAll();
		
		MultiTenantDbFactory.setDatabaseNameForCurrentThread(org2.getOrgId());
		organizationRepository.deleteAll();
	}
	
	@Test
	public void saveOrgTest()
	{
		MultiTenantDbFactory.setDatabaseNameForCurrentThread(org1.getOrgId());
		organizationRepository.save(org1);
		assertThat(org1.getOrgId(), is(notNullValue()));
		
		MultiTenantDbFactory.setDatabaseNameForCurrentThread(org2.getOrgId());
		organizationRepository.save(org2);
		assertThat(org2.getOrgId(), is(notNullValue()));
	}
	
	@Test
	public void deleteOrgTest()
	{
		MultiTenantDbFactory.setDatabaseNameForCurrentThread(org1.getOrgId());
		
		Organization orgTest = organizationRepository.findByOrgId(org1.getOrgId());
		organizationRepository.delete(orgTest);
		
		assertFalse(organizationRepository.exists(org1.getOrgId()));
	}

	@Test
	public void getOrgTest()
	{
		MultiTenantDbFactory.setDatabaseNameForCurrentThread(org1.getOrgId());
		
		Organization orgTest = organizationRepository.findByOrgId(org1.getOrgId());
		
		assertThat(orgTest.getOrgId(), is("abc"));
	}
	
	@Test
	public void updateOrgTest()
	{
		MultiTenantDbFactory.setDatabaseNameForCurrentThread(org1.getOrgId());
		
		Organization updatedOrg = organizationRepository.findByOrgId(org1.getOrgId());
		updatedOrg.setOrgName("info");
		organizationRepository.save(updatedOrg);
		
		assertEquals("info", organizationRepository.findByOrgId(updatedOrg.getOrgId()).getOrgName());
		
	}
}
