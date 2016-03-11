package com.bbytes.purple.service;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.repository.StatusRepository;

@Service
public class StatusService extends AbstractService<Status, String> {

	private StatusRepository statusRepository;

	@Autowired
	public StatusService(StatusRepository statusRepository) {
		super(statusRepository);
		this.statusRepository = statusRepository;
	}

	public Status getStatusbyId(String statusId) {
		return statusRepository.findOne(statusId);
	}

	public Status getStatusByProject(Project project) {
		return statusRepository.findByProject(project);
	}

	public Status getStatusByUser(User user) {
		return statusRepository.findByUser(user);
	}

	public Status findByDateTime(DateTime dateTime) {
		return statusRepository.findByDateTime(dateTime);
	}
}
