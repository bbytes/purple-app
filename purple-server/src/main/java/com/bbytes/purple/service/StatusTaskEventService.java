package com.bbytes.purple.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.StatusTaskEvent;
import com.bbytes.purple.domain.TaskItem;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.enums.TaskState;
import com.bbytes.purple.repository.StatusTaskEventRepository;

/**
 * Status Task Event Service
 * 
 * @author Akshay
 *
 */
@Service
public class StatusTaskEventService extends AbstractService<StatusTaskEvent, String> {

	private StatusTaskEventRepository statusTaskEventRepository;

	@Autowired
	public StatusTaskEventService(StatusTaskEventRepository statusTaskEventRepository) {
		super(statusTaskEventRepository);
		this.statusTaskEventRepository = statusTaskEventRepository;
	}

	public List<StatusTaskEvent> findByStateAndUser(TaskState state, User user) {
		return statusTaskEventRepository.findByStateAndEventOwner(state, user);
	}

	public List<StatusTaskEvent> findByUser(User user) {
		return statusTaskEventRepository.findByEventOwner(user);
	}

	public List<StatusTaskEvent> findByTaskItem(TaskItem taskItem) {
		return statusTaskEventRepository.findByTaskItem(taskItem);
	}

	public List<StatusTaskEvent> findByStatus(Status status) {
		return statusTaskEventRepository.findByStatus(status);
	}

	public List<StatusTaskEvent> findByState(TaskState state) {
		return statusTaskEventRepository.findByState(state);
	}

	public StatusTaskEvent findByStatusAndTaskItem(Status status, TaskItem taskItem) {
		return statusTaskEventRepository.findByStatusAndTaskItem(status, taskItem);
	}

}
