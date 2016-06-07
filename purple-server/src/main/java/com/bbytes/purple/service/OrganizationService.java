package com.bbytes.purple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.repository.OrganizationRepository;

@Service
public class OrganizationService extends AbstractService<Organization, String> {

	private OrganizationRepository organizationRepository;

	@Autowired
	public OrganizationService(OrganizationRepository organizationRepository) {
		super(organizationRepository);
		this.organizationRepository = organizationRepository;
	}

	public Organization findByOrgId(String orgId) {
		return organizationRepository.findByOrgId(orgId);
	}

	public boolean orgIdExist(String orgId) {
		boolean state = organizationRepository.findByOrgId(orgId) == null ? false : true;
		return state;
	}
}
