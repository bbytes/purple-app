package com.bbytes.purple.repository.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.stereotype.Component;

import com.bbytes.purple.domain.User;
import com.bbytes.purple.service.TenantResolverService;
import com.mongodb.DBObject;

/**
 * Equivalent of a domain method annotated by <code>PrePersist</code>.
 * <p/>
 * This handler shows how to implement your custom UUID generation.
 * 
 */
@Component
@Profile("saas")
public class UserDBEventListener extends AbstractMongoEventListener<User> {

	@Autowired
	private TenantResolverService tenantResolverService;

	/**
	 * Remove uses from tenant resolver after user delete command
	 */
	@Override
	public void onAfterDelete(AfterDeleteEvent<User> event) {
		final DBObject userDeleted = event.getSource();
		tenantResolverService.deleteTenantResolverForUserId(userDeleted.get("userId").toString());
	}

	/**
	 * Update tenant resolver in tenant management db when a user is saved in
	 * tenant db , the user email has to be unique across all tenant db
	 */
	@Override
	public void onBeforeSave(BeforeSaveEvent<User> event) {
		User userToBeSaved = event.getSource();
		if (!tenantResolverService.doesTenantResolverExistForUser(userToBeSaved)) {
			tenantResolverService.saveTenantResolverForUser(userToBeSaved);
		} else {
			if (userToBeSaved.getUserId() == null) {
				throw new DuplicateKeyException("Trying to save user with same email address");
			}
		}
	}

	/**
	 * Update user id after user save in tenant resolver , we need the user id
	 * to remove the tenant resolver entry when the user is deleted from tenant
	 * db
	 */
	@Override
	public void onAfterSave(AfterSaveEvent<User> event) {
		User userToBeSaved = event.getSource();
		tenantResolverService.updateUserIdInTenantResolverForUser(userToBeSaved);
	}

}