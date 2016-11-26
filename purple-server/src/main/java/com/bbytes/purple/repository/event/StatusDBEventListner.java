package com.bbytes.purple.repository.event;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.stereotype.Component;

import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.StatusTaskEvent;
import com.bbytes.purple.domain.TaskItem;
import com.bbytes.purple.domain.TaskList;
import com.bbytes.purple.service.StatusService;
import com.bbytes.purple.service.StatusTaskEventService;
import com.bbytes.purple.service.TaskItemService;
import com.bbytes.purple.service.TaskListService;
import com.mongodb.DBObject;

@Component
@Profile("saas")
public class StatusDBEventListner extends AbstractMongoEventListener<Status> {

	@Autowired
	private StatusService statusService;

	@Autowired
	private StatusTaskEventService statusTaskEventService;

	@Autowired
	private TaskListService taskListService;

	@Autowired
	private TaskItemService taskItemService;

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
		List<StatusTaskEvent> statusTaskEventList = statusTaskEventService.findByStatus(statusFromDb);
		// looping all statusTaskEventList, which was fetch by status
		for (StatusTaskEvent statusTaskEvent : statusTaskEventList) {
			// removing spend hours from taskItem when status is getting delete
			TaskItem taskItem = taskItemService.findOne(statusTaskEvent.getTaskItem().getTaskItemId());
			taskItem.removeSpendHours(statusTaskEvent.getSpendHours());
			taskItemService.save(taskItem);

			// removing spend hours from taskList when status is getting delete
			TaskList taskList = taskListService.findOne(statusTaskEvent.getTaskItem().getTaskList().getTaskListId());
			taskList.removeSpendHours(statusTaskEvent.getSpendHours());
			taskListService.save(taskList);
		}
		statusTaskEventService.delete(statusTaskEventList);
	}
}
