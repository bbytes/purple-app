package com.bbytes.purple.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.repository.StatusRepository;
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

	public List<Status> findByProjectAndUser(Project project, User user) {
		return statusRepository.findByProjectAndUser(project, user);
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
		if (!statusIdExist(statusid))
			throw new PurpleException("Error while getting status", ErrorHandler.STATUS_NOT_FOUND);
		try {
			getStatus = statusRepository.findOne(statusid);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_STATUS_FAILED);
		}
		return getStatus;
	}

	public List<Status> getAllStatus(User user) throws PurpleException {
		List<Status> statuses = new ArrayList<Status>();
		try {
			statuses = statusRepository.findByUser(user);
			Collections.sort(statuses, Collections.reverseOrder());

		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_STATUS_FAILED);
		}

		return statuses;
	}

	public void deleteStatus(String statusid) throws PurpleException {
		if (!statusIdExist(statusid))
			throw new PurpleException("Error while deleting status", ErrorHandler.STATUS_NOT_FOUND);
		try {
			Status status = statusRepository.findOne(statusid);
			statusRepository.delete(status);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_STATUS_FAILED);
		}
	}

	public Status updateStatus(String statusId, StatusDTO status) throws PurpleException {

		Status newStatus = null;
		if (!statusIdExist(statusId))
			throw new PurpleException("Error while adding project", ErrorHandler.PROJECT_NOT_FOUND);
		try {
			Project project = projectService.findByProjectName(status.getProjectName());
			Status updateStatus = getStatusbyId(statusId);
			updateStatus.setWorkedOn(status.getWorkedOn());
			updateStatus.setWorkingOn(status.getWorkingOn());
			updateStatus.setBlockers(status.getBlockers());
			updateStatus.setHours(status.getHours());
			updateStatus.setProject(project);
			newStatus = statusRepository.save(updateStatus);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.UPDATE_PROJECT_FAILED);
		}
		return newStatus;

	}

	public List<Status> getAllStatusByProjectAndUser(UsersAndProjectsDTO userAndProject, User currentUser)
			throws PurpleException {
		List<Status> statuses = new ArrayList<Status>();
		List<Project> projectList = new ArrayList<Project>();
		List<User> userList = new ArrayList<User>();
		Set<User> allUsers = new HashSet<User>();
		Set<Project> projects = new HashSet<Project>();
		try {
			if (userAndProject.getProjectList().isEmpty() && userAndProject.getUserList().isEmpty()) {
				projectList = userService.getProjects(currentUser);
				for (Project project : projectList) {
					allUsers.addAll(project.getUser());
				}
				userList.addAll(allUsers);
				for (Project projectsOfCurrentUser : projectList) {
					for (User userOfProjects : userList) {
						statuses.addAll(findByProjectAndUser(projectsOfCurrentUser, userOfProjects));
						Collections.sort(statuses, Collections.reverseOrder());
					}
				}
			} else if (!userAndProject.getProjectList().isEmpty() || !userAndProject.getUserList().isEmpty()) {
				if (!userAndProject.getProjectList().isEmpty() && userAndProject.getUserList().isEmpty()) {
					for (String projectid : userAndProject.getProjectList()) {
						if (!projectService.projectIdExist(projectid))
							throw new PurpleException("Error while getting status", ErrorHandler.PROJECT_NOT_FOUND);
						Project project = projectService.findByProjectId(projectid);
						projectList.add(project);
					}
					for (Project project : projectList) {
						allUsers.addAll(project.getUser());
					}
					userList.addAll(allUsers);
					for (Project projectsOfCurrentUser : projectList) {
						for (User userOfProjects : userList) {
							statuses.addAll(findByProjectAndUser(projectsOfCurrentUser, userOfProjects));
							Collections.sort(statuses, Collections.reverseOrder());
						}
					}
				}
				if (userAndProject.getProjectList().isEmpty() && !userAndProject.getUserList().isEmpty()) {
					for (String email : userAndProject.getUserList()) {
						if (!userService.userEmailExist(email))
							throw new PurpleException("Error while getting status", ErrorHandler.USER_NOT_FOUND);
						User user = userService.getUserByEmail(email);
						userList.add(user);
					}
					for (User userOfProject : userList) {
						projects.addAll(userOfProject.getProjects());
					}
					projectList.addAll(projects);
					for (Project projectsOfCurrentUser : projectList) {
						for (User userOfProjects : userList) {
							statuses.addAll(findByProjectAndUser(projectsOfCurrentUser, userOfProjects));
							Collections.sort(statuses, Collections.reverseOrder());
						}
					}
				}
				if (!userAndProject.getProjectList().isEmpty() && !userAndProject.getUserList().isEmpty()) {
					for (String projectid : userAndProject.getProjectList()) {
						if (!projectService.projectIdExist(projectid))
							throw new PurpleException("Error while getting status", ErrorHandler.PROJECT_NOT_FOUND);
						Project project = projectService.findByProjectId(projectid);
						projectList.add(project);
					}
					for (String email : userAndProject.getUserList()) {
						if (!userService.userEmailExist(email))
							throw new PurpleException("Error while getting status", ErrorHandler.USER_NOT_FOUND);
						User user = userService.getUserByEmail(email);
						userList.add(user);
					}
					for (Project projectsOfCurrentUser : projectList) {
						for (User userOfProjects : userList) {
							statuses.addAll(findByProjectAndUser(projectsOfCurrentUser, userOfProjects));
							Collections.sort(statuses, Collections.reverseOrder());
						}
					}
				}
			}
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.GET_STATUS_FAILED);
		}

		return statuses;
	}

}
