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

	public String findTenantIdForUserEmail(String email) {

		String tenantIdToBeSetBackToContext = TenancyContextHolder.getTenant();

		// go to default management db
		TenancyContextHolder.setDefaultTenant();
		TenantResolver tenantResolver = tenantResolverRepository.findOneByEmail(email);

		if (tenantResolver == null)
			throw new UsernameNotFoundException("User not found with email '" + email + "'");

		// set the given tenant id as current db
		TenancyContextHolder.setTenant(tenantIdToBeSetBackToContext);

		return tenantResolver.getOrgId();

	}

	public boolean doesTenantResolverExistForUser(User user) {
		if (user == null || user.getOrganization().getOrgId() == null)
			throw new IllegalArgumentException("User or tenantId cannot be null");

		String tenantIdToBeSetBackToContext = TenancyContextHolder.getTenant();

		TenancyContextHolder.setDefaultTenant();
		TenantResolver tenantResolver = tenantResolverRepository.findOneByEmail(user.getEmail());

		// set the given tenant id as current db
		TenancyContextHolder.setTenant(tenantIdToBeSetBackToContext);

		if (tenantResolver == null)
			return false;

		return true;
	}

	public TenantResolver saveTenantResolverForUser(User user) {
		if (user == null || user.getOrganization().getOrgId() == null)
			throw new IllegalArgumentException("User or tenantId cannot be null");

		String tenantIdToBeSetBackToContext = TenancyContextHolder.getTenant();

		// go to default management db
		TenancyContextHolder.setDefaultTenant();
		TenantResolver tenantResolver = new TenantResolver();
		tenantResolver.setEmail(user.getEmail());
		tenantResolver.setOrgId(user.getOrganization().getOrgId());

		tenantResolver = tenantResolverRepository.save(tenantResolver);

		// set the given tenant id as current db
		TenancyContextHolder.setTenant(tenantIdToBeSetBackToContext);

		return tenantResolver;

	}

	public TenantResolver updateUserIdInTenantResolverForUser(User user) {
		if (user == null || user.getUserId() == null)
			throw new IllegalArgumentException("User or User id cannot be null");

		String tenantIdToBeSetBackToContext = TenancyContextHolder.getTenant();

		// go to default management db
		TenancyContextHolder.setDefaultTenant();
		TenantResolver tenantResolver = tenantResolverRepository.findOneByEmail(user.getEmail());
		tenantResolver.setUserId(user.getUserId());

		tenantResolver = tenantResolverRepository.save(tenantResolver);

		// set the given tenant id as current db
		TenancyContextHolder.setTenant(tenantIdToBeSetBackToContext);

		return tenantResolver;

	}

	public TenantResolver deleteTenantResolverForUserId(String userId) {
		if (userId == null)
			throw new IllegalArgumentException("User id cannot be null");

		String tenantIdToBeSetBackToContext = TenancyContextHolder.getTenant();

		// go to default management db
		TenancyContextHolder.setDefaultTenant();

		TenantResolver tenantResolver = tenantResolverRepository.findOneByUserId(userId);
		if (tenantResolver == null)
			throw new IllegalArgumentException("User id cannot be null");

		tenantResolverRepository.delete(tenantResolver);

		// set the given tenant id as current db
		TenancyContextHolder.setTenant(tenantIdToBeSetBackToContext);

		return tenantResolver;

	}

	public TenantResolver findOneByEmail(String email) {
		String tenantIdToBeSetBackToContext = TenancyContextHolder.getTenant();

		// go to default management db
		TenancyContextHolder.setDefaultTenant();

		TenantResolver tenantResolver = tenantResolverRepository.findOneByEmail(email);

		// set the given tenant id as current db
		TenancyContextHolder.setTenant(tenantIdToBeSetBackToContext);
		return tenantResolver;
	}

	public TenantResolver findOneByUserId(String userId) {
		String tenantIdToBeSetBackToContext = TenancyContextHolder.getTenant();

		// go to default management db
		TenancyContextHolder.setDefaultTenant();
		TenantResolver tenantResolver = tenantResolverRepository.findOneByUserId(userId);

		// set the given tenant id as current db
		TenancyContextHolder.setTenant(tenantIdToBeSetBackToContext);
		return tenantResolver;
	}

}
