package com.bbytes.purple.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.repository.StatusRepository;
import com.bbytes.purple.rest.dto.models.StatusDTO;
import com.bbytes.purple.utils.ErrorHandler;

@Service
public class StatusService extends AbstractService<Status, String> {

	private StatusRepository statusRepository;

	@Autowired
	private ProjectService projectService;

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

	public List<Status> getStatusByUser(User user) {
		return statusRepository.findByUser(user);
	}

	public Status findByDateTime(DateTime dateTime) {
		return statusRepository.findByDateTime(dateTime);

	}

	public boolean statusIdExist(String projectId) {
		boolean state = statusRepository.findOne(projectId) == null ? false : true;
		return state;
	}

	public Status create(StatusDTO status, User user) throws PurpleException {
		Status savedStatus = null;
		if (status != null && status.getProjectId() != null && !status.getProjectId().isEmpty()) {
			if (!projectService.projectIdExist(status.getProjectId()))
				throw new PurpleException("Error while adding status", ErrorHandler.PROJECT_NOT_FOUND);
			try {
				Status addStatus = new Status(status.getWorkingOn(), status.getWorkedOn(), status.getHours(),
						new Date());
				Project project = projectService.findByProjectId(status.getProjectId());
				addStatus.setProject(project);
				addStatus.setUser(user);
				addStatus.setBlockers(status.getBlockers());
				savedStatus = statusRepository.save(addStatus);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.ADD_STATUS_FAILED);
			}
		} else
			throw new PurpleException("Can not add status with empty project", ErrorHandler.PROJECT_NOT_FOUND);
		return savedStatus;
	}

	public Status getStatus(String statusid) throws PurpleException {
		Status getStatus = null;
		if (!statusid.equals("null")) {
			if (!statusIdExist(statusid))
				throw new PurpleException("Error while getting status", ErrorHandler.STATUS_NOT_FOUND);
			try {
				getStatus = statusRepository.findOne(statusid);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.GET_STATUS_FAILED);
			}
		} else
			throw new PurpleException("Can not find with empty status", ErrorHandler.STATUS_NOT_FOUND);
		return getStatus;
	}

	public List<Status> getAllStatus(User user) throws PurpleException {
		List<Status> statuses = new ArrayList<Status>();
		try {
			statuses = statusRepository.findByUser(user);
			Collections.sort(statuses,Collections.reverseOrder());
			
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_STATUS_FAILED);
		}

		return statuses;
	}

	public void deleteStatus(String statusid) throws PurpleException {
		if (!statusid.equals("null")) {
			if (!statusIdExist(statusid))
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

	public Status updateStatus(String statusId, StatusDTO status, User user) throws PurpleException {

		Status updatedStatus = null;
		if (!statusId.equals("null")) {
			if (!statusIdExist(statusId))
				throw new PurpleException("Error while adding project", ErrorHandler.PROJECT_NOT_FOUND);
			try {
				Status updateStatus = getStatusbyId(statusId);
				updateStatus.setWorkedOn(status.getWorkedOn());
				updateStatus.setWorkingOn(status.getWorkingOn());
				updateStatus.setBlockers(status.getBlockers());
				updatedStatus = statusRepository.save(updateStatus);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.UPDATE_PROJECT_FAILED);
			}
		} else
			throw new PurpleException("Can not find empty project", ErrorHandler.PROJECT_NOT_FOUND);
		return updatedStatus;

	}
	
	
}
