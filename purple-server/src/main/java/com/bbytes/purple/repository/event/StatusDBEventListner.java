package com.bbytes.purple.repository.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.stereotype.Component;

import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.StatusTaskEvent;
import com.bbytes.purple.service.StatusService;
import com.bbytes.purple.service.StatusTaskEventService;
import com.mongodb.DBObject;

@Component
@Profile("saas")
public class StatusDBEventListner extends AbstractMongoEventListener<Status> {

	@Autowired
	private StatusService statusService;

	@Autowired
	private StatusTaskEventService statusTaskEventService;

	@Autowired
	MongoTemplate mongoTemplate;

	/**
	 * Remove the status from the deleting statusTaskEvent by status - Cascade
	 * delete
	 */
	@Override
	public void onBeforeDelete(BeforeDeleteEvent<Status> event) {
		final DBObject statusDeleted = event.getSource();
		Status statusFromDb = statusService.findOne(statusDeleted.get("statusId").toString());
		StatusTaskEvent statusTaskEvent = statusTaskEventService.findByStatus(statusFromDb);
		statusTaskEventService.delete(statusTaskEvent);
	}

}
