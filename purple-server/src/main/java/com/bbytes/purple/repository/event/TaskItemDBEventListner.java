package com.bbytes.purple.repository.event;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.stereotype.Component;

import com.bbytes.purple.domain.StatusTaskEvent;
import com.bbytes.purple.domain.TaskItem;
import com.bbytes.purple.service.StatusTaskEventService;
import com.bbytes.purple.service.TaskItemService;
import com.mongodb.DBObject;

@Component
public class TaskItemDBEventListner extends AbstractMongoEventListener<TaskItem> {

	@Autowired
	private TaskItemService taskItemService;

	@Autowired
	private StatusTaskEventService statusTaskEventService;

	@Autowired
	MongoTemplate mongoTemplate;

	/**
	 * Remove the status from the deleting statusTaskEvent by status - Cascade
	 * delete
	 */
	@Override
	public void onBeforeDelete(BeforeDeleteEvent<TaskItem> event) {
		final DBObject statusDeleted = event.getSource();
		TaskItem taskItemFromDb = taskItemService.findOne(statusDeleted.get("taskItemId").toString());
		List<StatusTaskEvent> statusTaskEvents = statusTaskEventService.findByTaskItem(taskItemFromDb);
		statusTaskEventService.delete(statusTaskEvents);
	}

}
