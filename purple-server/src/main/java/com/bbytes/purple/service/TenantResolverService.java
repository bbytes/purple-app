package com.bbytes.purple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.TenantResolver;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.repository.TenantResolverRepository;
import com.bbytes.purple.utils.TenancyContextHolder;

@Service
public class TenantResolverService {

	@Autowired
	private TenantResolverRepository tenantResolverRepository;

	public String getTenantIdForUser(String email) {
		// go to default management db
		TenancyContextHolder.setDefaultTenant();
		TenantResolver tenantResolver = tenantResolverRepository.findOneByEmail(email);

		if (tenantResolver == null)
			throw new UsernameNotFoundException("User not found with email '" + email+"'");

		// set the queried tenant id as current db
		TenancyContextHolder.setTenant(tenantResolver.getOrgId());
		return tenantResolver.getOrgId();

	}

	public boolean doesTenantResolverExistForUser(User user) {
		if (user == null || user.getOrganization().getOrgId() == null)
			throw new IllegalArgumentException("User or tenantId cannot be null");

		TenancyContextHolder.setDefaultTenant();
		TenantResolver tenantResolver = tenantResolverRepository.findOneByEmail(user.getEmail());

		// set the given tenant id as current db
		TenancyContextHolder.setTenant(user.getOrganization().getOrgId());

		if (tenantResolver == null)
			return false;

		return true;
	}

	public TenantResolver saveTenantResolverForUser(User user) {
		if (user == null || user.getOrganization().getOrgId() == null)
			throw new IllegalArgumentException("User or tenantId cannot be null");

		// go to default management db
		TenancyContextHolder.setDefaultTenant();
		TenantResolver tenantResolver = new TenantResolver();
		tenantResolver.setEmail(user.getEmail());
		tenantResolver.setOrgId(user.getOrganization().getOrgId());

		tenantResolver = tenantResolverRepository.save(tenantResolver);

		// set the given tenant id as current db
		TenancyContextHolder.setTenant(user.getOrganization().getOrgId());

		return tenantResolver;

	}

}
