package com.bbytes.purple.service;

import java.util.ArrayList;
import java.util.Collections;
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
import com.bbytes.purple.repository.UserRepository;
import com.bbytes.purple.rest.dto.models.StatusDTO;
import com.bbytes.purple.rest.dto.models.UsersAndProjectsDTO;
import com.bbytes.purple.utils.ErrorHandler;

@Service
public class StatusService extends AbstractService<Status, String> {

	private StatusRepository statusRepository;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	public StatusService(StatusRepository statusRepository) {
		super(statusRepository);
		this.statusRepository = statusRepository;
	}

	public Status getStatusbyId(String statusId) {
		return statusRepository.findOne(statusId);
	}

	public List<Status> getStatusByProject(Project project) {
		return statusRepository.findByProject(project);
	}

	public List<Status> getStatusByUser(User user) {
		return statusRepository.findByUser(user);
	}

	public List<Status> findByProjectAndUser(Project project, User user) {
		return statusRepository.findByProjectAndUser(project, user);
	}

	public boolean statusIdExist(String projectId) {
		boolean state = statusRepository.findOne(projectId) == null ? false : true;
		return state;
	}

	public double findStatusHours(User user, Date dateTime) {
		double hours = 0;
		Date startDate = new DateTime(dateTime).withTime(0, 0, 0, 0).toDate();
		Date endDate = new DateTime(dateTime).withTime(23, 59, 59, 999).toDate();
		List<Status> statusList = statusRepository.findByDateTimeBetweenAndUser(startDate, endDate, user);
		if (statusList == null || statusList.isEmpty())
			return hours;

		for (Status status : statusList) {
			hours = hours + status.getHours();
		}

		return hours;
	}

	public Status create(StatusDTO status, User user) throws PurpleException {
		Status savedStatus = null;
		if (status != null && status.getProjectId() != null && !status.getProjectId().isEmpty()) {
			if (!projectService.projectIdExist(status.getProjectId()))
				throw new PurpleException("Error while adding status", ErrorHandler.PROJECT_NOT_FOUND);

			Status addStatus = new Status(status.getWorkingOn(), status.getWorkedOn(), status.getHours(), new Date());
			Project project = projectService.findByProjectId(status.getProjectId());

			double hours = findStatusHours(user, new Date());
			double newHours = hours + status.getHours();
			if (newHours > 24)
				throw new PurpleException("Exceeded the status hours", ErrorHandler.HOURS_EXCEEDED);

			addStatus.setProject(project);
			addStatus.setUser(user);
			addStatus.setBlockers(status.getBlockers());
			try {
				savedStatus = statusRepository.save(addStatus);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.ADD_STATUS_FAILED, e);
			}
		} else
			throw new PurpleException("Cannot add status with empty project", ErrorHandler.PROJECT_NOT_FOUND);
		return savedStatus;
	}

	public Status getStatus(String statusId) throws PurpleException {
		Status getStatus = null;
		if (!statusIdExist(statusId))
			throw new PurpleException("Error while getting status", ErrorHandler.STATUS_NOT_FOUND);
		try {
			getStatus = statusRepository.findOne(statusId);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_STATUS_FAILED);
		}
		return getStatus;
	}

	public List<Status> getAllStatus(User user, int timePeriod) throws PurpleException {
		List<Status> statuses = new ArrayList<Status>();
		try {
			Date endDate = new DateTime(new Date()).toDate();
			Date startDate = new DateTime(new Date()).minusDays(timePeriod).withTime(0, 0, 0, 0).toDate();
			// statuses = statusRepository.findByUser(user);
			statuses = statusRepository.findByDateTimeBetweenAndUser(startDate, endDate, user);
			Collections.sort(statuses, Collections.reverseOrder());

		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_STATUS_FAILED);
		}

		return statuses;
	}

	public void deleteStatus(String statusId) throws PurpleException {
		if (!statusIdExist(statusId))
			throw new PurpleException("Error while deleting status", ErrorHandler.STATUS_NOT_FOUND);
		try {
			Status status = statusRepository.findOne(statusId);
			statusRepository.delete(status);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_STATUS_FAILED);
		}
	}

	public Status updateStatus(String statusId, StatusDTO status, User user) throws PurpleException {

		Status newStatus = null;
		if ((!statusIdExist(statusId) || (status.getProjectId() == null || status.getProjectId().isEmpty())))
			throw new PurpleException("Error while adding project", ErrorHandler.PROJECT_NOT_FOUND);

		Project project = projectService.findByProjectId(status.getProjectId());
		Status updateStatus = getStatusbyId(statusId);

		Date statusDate = updateStatus.getDateTime();
		double hours = findStatusHours(user, statusDate);
		double newHours = hours + (status.getHours() - updateStatus.getHours());
		if (newHours > 24)
			throw new PurpleException("Exceeded the status hours", ErrorHandler.HOURS_EXCEEDED);

		updateStatus.setWorkedOn(status.getWorkedOn());
		updateStatus.setWorkingOn(status.getWorkingOn());
		updateStatus.setBlockers(status.getBlockers());
		updateStatus.setHours(status.getHours());
		updateStatus.setProject(project);
		try {
			newStatus = statusRepository.save(updateStatus);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.UPDATE_STATUS_FAILED, e);
		}

		return newStatus;
	}

	public List<Status> getAllStatusByProjectAndUser(UsersAndProjectsDTO userAndProject, User currentUser,
			Integer timePeriodValue) throws PurpleException {
		List<Status> result = new ArrayList<Status>();
		List<Project> currentUserProjectList = userService.getProjects(currentUser);
		List<String> projectIdStringQueryList = userAndProject.getProjectList();
		List<String> userQueryEmailList = userAndProject.getUserList();
		List<User> userQueryList = userRepository.findByEmailIn(userQueryEmailList);

		Date endDate = new DateTime(new Date()).toDate();
		Date startDate = new DateTime(new Date()).minusDays(timePeriodValue).withTime(0, 0, 0, 0).toDate();

		List<Project> projectQueryList = new ArrayList<>();
		// apply current user project list match filter to requested project
		// list
		if (currentUserProjectList != null) {
			if (!projectIdStringQueryList.isEmpty()) {
				for (Project project : currentUserProjectList) {
					if (projectIdStringQueryList.contains(project.getProjectId())) {
						projectQueryList.add(project);
					}
				}
			} else {
				// if the project list in request empty then the user has
				// selected 'All' option in ui so add all the
				// currentUserProjectList to projectQueryList
				projectQueryList = currentUserProjectList;
			}

		}

		// both empty
		if ((userQueryList == null || userQueryList.isEmpty())
				&& (projectQueryList == null || projectQueryList.isEmpty())) {
			return result;
		}
		// project list empty
		else if (userQueryList != null && !userQueryList.isEmpty()
				&& (projectQueryList == null || projectQueryList.isEmpty())) {
			result = statusRepository.findByDateTimeBetweenAndUserIn(startDate, startDate, userQueryList);
		}
		// user list empty
		else if (projectQueryList != null && !projectQueryList.isEmpty()
				&& (userQueryList == null || userQueryList.isEmpty())) {
			result = statusRepository.findByDateTimeBetweenAndProjectIn(startDate, endDate, projectQueryList);
		}
		// both the list not empty
		else {
			result = statusRepository.findByDateTimeBetweenAndProjectInAndUserIn(startDate, endDate, projectQueryList,
					userQueryList);
		}

		try {

		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_STATUS_FAILED);
		}

		Collections.sort(result, Collections.reverseOrder());

		return result;
	}

}
