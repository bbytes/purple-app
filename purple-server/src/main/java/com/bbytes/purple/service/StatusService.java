package com.bbytes.purple.service;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.repository.StatusRepository;
import com.bbytes.purple.utils.ErrorHandler;

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

	public boolean statusIdExist(String projectId) {
		boolean state = statusRepository.findOne(projectId) == null ? false : true;
		return state;
	}

	public Status create(Status status) throws PurpleException {
		Status savedStatus = null;
		if (status != null) {
			if (status.getProject().getProjectId() == null && status.getProject().getProjectId().isEmpty())
				throw new PurpleException("Error while adding status", ErrorHandler.STATUS_NOT_FOUND);
			try {
				savedStatus = statusRepository.save(status);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.ADD_STATUS_FAILED);
			}
		}
		return savedStatus;
	}

	public Status getStatus(String statusid) throws PurpleException {
		Status getStatus = null;
		if (statusid != null && !statusid.isEmpty()) {
			if (!statusRepository.findOne(statusid).equals(statusid))
				throw new PurpleException("Error while getting status", ErrorHandler.STATUS_NOT_FOUND);
			try {
				getStatus = statusRepository.findOne(statusid);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.GET_STATUS_FAILED);
			}
		}
		return getStatus;
	}

	public List<Status> getAllStatus() throws PurpleException {
		List<Status> statuses = new ArrayList<Status>();
		try {
			statuses = statusRepository.findAll();
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_STATUS_FAILED);
		}

		return statuses;
	}

	public void deleteStatus(String statusid) throws PurpleException {
		if (statusid != null && !statusid.isEmpty()) {
			if (!statusRepository.findOne(statusid).equals(statusid))
				throw new PurpleException("Error while deleting status", ErrorHandler.STATUS_NOT_FOUND);
			try {
				Status status = statusRepository.findOne(statusid);
				statusRepository.delete(status);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.GET_STATUS_FAILED);
			}
		} else
			throw new PurpleException("Can not delete empty status", ErrorHandler.STATUS_NOT_FOUND);
	}

	public Status updateStatus(String statusid, Status status) throws PurpleException {

		Status updatedStatus = null;
		if (statusid != null && !statusid.isEmpty() && status != null) {
			if (statusIdExist(statusid))
				throw new PurpleException("Error while updating status", ErrorHandler.STATUS_NOT_FOUND);
			try {
				Status updateStatus = statusRepository.findOne(statusid);
				updateStatus.setProject(status.getProject());
				updateStatus.setWorkedOn(status.getWorkedOn());
				updateStatus.setWorkingOn(status.getWorkingOn());
				updateStatus.setBlockers(status.getBlockers());
				updatedStatus = statusRepository.save(updateStatus);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.UPDATE_PROJECT_FAILED);
			}
		}
		return updatedStatus;
	}
}
