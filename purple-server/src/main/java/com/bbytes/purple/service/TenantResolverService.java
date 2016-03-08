package com.bbytes.purple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bbytes.purple.database.MultiTenantDbFactory;
import com.bbytes.purple.domain.TenantResolver;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.repository.TenantResolverRepository;

@Service
public class TenantResolverService {

	@Autowired
	private TenantResolverRepository tenantResolverRepository;

	public String getTenantIdForUser(String email) {
		// go to default management db
		MultiTenantDbFactory.setTenantManagementDatabaseNameForCurrentThread();
		TenantResolver tenantResolver = tenantResolverRepository.findOneByEmail(email);

		if (tenantResolver == null)
			throw new UsernameNotFoundException("Given email " + email + " not in DB");

		// set the queried tenant id as current db
		MultiTenantDbFactory.setDatabaseNameForCurrentThread(tenantResolver.getOrgId());
		return tenantResolver.getOrgId();

	}

	public boolean doesTenantResolverExistForUser(User user) {
		if (user == null || user.getOrganization().getOrgId() == null)
			throw new IllegalArgumentException("User or tenantId cannot be null");

		MultiTenantDbFactory.setTenantManagementDatabaseNameForCurrentThread();
		TenantResolver tenantResolver = tenantResolverRepository.findOneByEmail(user.getEmail());

		// set the given tenant id as current db
		MultiTenantDbFactory.setDatabaseNameForCurrentThread(user.getOrganization().getOrgId());

		if (tenantResolver == null)
			return false;

		return true;
	}

	public TenantResolver saveTenantResolverForUser(User user) {
		if (user == null || user.getOrganization().getOrgId() == null)
			throw new IllegalArgumentException("User or tenantId cannot be null");

		// go to default management db
		MultiTenantDbFactory.setTenantManagementDatabaseNameForCurrentThread();
		TenantResolver tenantResolver = new TenantResolver();
		tenantResolver.setEmail(user.getEmail());
		tenantResolver.setOrgId(user.getOrganization().getOrgId());

		tenantResolver = tenantResolverRepository.save(tenantResolver);

		// set the given tenant id as current db
		MultiTenantDbFactory.setDatabaseNameForCurrentThread(user.getOrganization().getOrgId());

		return tenantResolver;

	}

}
