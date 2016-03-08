package com.bbytes.purple.repository.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;

import com.bbytes.purple.domain.User;
import com.bbytes.purple.service.TenantResolverService;

/**
 * Equivalent of a domain method annotated by <code>PrePersist</code>.
 * <p/>
 * This handler shows how to implement your custom UUID generation.
 * 
 */
@Component
public class UserDBEventListener extends AbstractMongoEventListener<User> {

	@Autowired
	private TenantResolverService tenantResolverService;

	@Override
	public void onAfterSave(AfterSaveEvent<User> event) {
		if (!tenantResolverService.doesTenantResolverExistForUser(event.getSource())) {
			tenantResolverService.saveTenantResolverForUser(event.getSource());
		}
	}

}