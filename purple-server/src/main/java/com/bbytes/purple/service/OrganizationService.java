package com.bbytes.purple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.repository.HolidaysRepository;
import com.bbytes.purple.repository.OrganizationRepository;

@Service
public class OrganizationService extends AbstractService<Organization, String>{

	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	public OrganizationService(OrganizationRepository OrganizationRepository) {
		super(OrganizationRepository);
	}
	
	public Organization getOrgById(String orgId)
	{
		return organizationRepository.findByOrgId(orgId);
	}
}
