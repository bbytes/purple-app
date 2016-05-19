package com.bbytes.purple.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.ConfigSetting;
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
	private ConfigSettingService configSettingService;

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

	public Status create(StatusDTO statusDTO, User user) throws PurpleException {
		Status savedStatus = null;

		cleanUpStatusText(statusDTO);

		ConfigSetting configSetting = configSettingService.getConfigSetting(user.getOrganization());
		String statusEnableDate = configSetting.getStatusEnable();

		if (statusDTO != null && statusDTO.getProjectId() != null && !statusDTO.getProjectId().isEmpty()) {
			if (!projectService.projectIdExist(statusDTO.getProjectId()))
				throw new PurpleException("Error while adding status", ErrorHandler.PROJECT_NOT_FOUND);

			if (statusDTO.getDateTime() == null || statusDTO.getDateTime().isEmpty()) {
				savedStatus = new Status(statusDTO.getWorkingOn(), statusDTO.getWorkedOn(), statusDTO.getHours(),
						new Date());
			} else {
				Date statusDate = new Date(Long.parseLong(statusDTO.getDateTime()));
				Calendar cal = Calendar.getInstance();
				cal.setTime(statusDate);
				Date newTime = new DateTime(new Date())
						.withDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
						.toDate();

				Date backDate = new DateTime(new Date()).minusDays(Integer.parseInt(statusEnableDate))
						.withTime(0, 0, 0, 0).toDate();
				if (statusDate.before(backDate))
					throw new PurpleException("Cannot add status past " + statusEnableDate + " days",
							ErrorHandler.PASS_DUEDATE_STATUS_EDIT);
				if (statusDate.after(new Date()))
					throw new PurpleException("Cannot add status for future date",
							ErrorHandler.FUTURE_DATE_STATUS_EDIT);
				savedStatus = new Status(statusDTO.getWorkingOn(), statusDTO.getWorkedOn(), statusDTO.getHours(),
						newTime);
			}

			Project project = projectService.findByProjectId(statusDTO.getProjectId());

			double hours = findStatusHours(user, new Date());
			double newHours = hours + statusDTO.getHours();
			if (newHours > 24)
				throw new PurpleException("Exceeded the status hours", ErrorHandler.HOURS_EXCEEDED);

			savedStatus.setProject(project);
			savedStatus.setUser(user);
			savedStatus.setBlockers(statusDTO.getBlockers());
			try {
				savedStatus = statusRepository.save(savedStatus);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.ADD_STATUS_FAILED, e);
			}
		} else
			throw new PurpleException("Cannot add status with empty project", ErrorHandler.PROJECT_NOT_FOUND);
		return savedStatus;
	}

	/**
	 * Strip Leading and Trailing Spaces From String
	 * 
	 * @param statusDTO
	 */
	private void cleanUpStatusText(StatusDTO statusDTO) {
		if (statusDTO != null) {
			if (statusDTO.getWorkedOn() != null)
				statusDTO.setWorkedOn(statusDTO.getWorkedOn().trim());

			if (statusDTO.getWorkingOn() != null)
				statusDTO.setWorkingOn(statusDTO.getWorkingOn().trim());

			if (statusDTO.getBlockers() != null)
				statusDTO.setBlockers(statusDTO.getBlockers().trim());
		}

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

	public Status updateStatus(String statusId, StatusDTO statusDTO, User user) throws PurpleException {

		Status newStatus = null;
		if ((!statusIdExist(statusId) || (statusDTO.getProjectId() == null || statusDTO.getProjectId().isEmpty())))
			throw new PurpleException("Error while adding project", ErrorHandler.PROJECT_NOT_FOUND);

		cleanUpStatusText(statusDTO);

		Project project = projectService.findByProjectId(statusDTO.getProjectId());
		Status updateStatus = getStatusbyId(statusId);

		Date statusDate = updateStatus.getDateTime();
		double hours = findStatusHours(user, statusDate);
		double newHours = hours + (statusDTO.getHours() - updateStatus.getHours());
		if (newHours > 24)
			throw new PurpleException("Exceeded the status hours", ErrorHandler.HOURS_EXCEEDED);

		updateStatus.setWorkedOn(statusDTO.getWorkedOn());
		updateStatus.setWorkingOn(statusDTO.getWorkingOn());
		updateStatus.setBlockers(statusDTO.getBlockers());
		updateStatus.setHours(statusDTO.getHours());
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
