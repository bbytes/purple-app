package com.bbytes.purple.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.StatusTaskEvent;
import com.bbytes.purple.domain.TaskItem;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.enums.TaskState;

public interface StatusTaskEventRepository extends MongoRepository<StatusTaskEvent, String> {

	List<StatusTaskEvent> findByStateAndEventOwner(TaskState state, User user);

	List<StatusTaskEvent> findByEventOwner(User user);

	List<StatusTaskEvent> findByTaskItem(TaskItem taskItem);

	StatusTaskEvent findByStatus(Status status);

	StatusTaskEvent findByStatusAndTaskItem(Status status, TaskItem taskItem);

	List<StatusTaskEvent> findByState(TaskState state);
}
