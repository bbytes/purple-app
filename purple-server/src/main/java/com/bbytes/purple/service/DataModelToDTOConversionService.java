package com.bbytes.purple.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.rest.dto.models.BaseDTO;
import com.bbytes.purple.rest.dto.models.ProjectDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.rest.dto.models.StatusDTO;
import com.bbytes.purple.rest.dto.models.StatusResponseDTO;
import com.bbytes.purple.rest.dto.models.UserDTO;
import com.bbytes.purple.utils.GlobalConstants;

@Service
public class DataModelToDTOConversionService {

	public static final String JOINED_USERS_COUNT = "joined_count";
	public static final String PENDING_USERS_COUNT = "pending_count";
	public static final String PROJECT_COUNT = "project_count";

	public BaseDTO convertToBaseDTO(String value) {
		BaseDTO baseDTO = new BaseDTO();
		baseDTO.setId(value);
		baseDTO.setValue(value);
		return baseDTO;
	}

	public List<BaseDTO> convertRolesToEntityDTOList(List<String> values) {
		List<BaseDTO> baseDTOList = new ArrayList<BaseDTO>();
		for (String value : values) {
			baseDTOList.add(convertToBaseDTO(value));
		}
		return baseDTOList;
	}

	public List<UserDTO> convertUsers(List<User> users) {
		List<UserDTO> userDTOList = new ArrayList<UserDTO>();
		for (User user : users) {
			userDTOList.add(convertUser(user));
		}
		return userDTOList;
	}

	public UserDTO convertUser(User user) {
		UserDTO userDTO = new UserDTO();
		userDTO.setId(user.getUserId());
		userDTO.setEmail(user.getEmail());
		userDTO.setUserName(user.getName());
		userDTO.setStatus(user.getStatus());
		userDTO.setAccountInitialise(user.isAccountInitialise());
		userDTO.setUserRole(convertToBaseDTO(user.getUserRole().getRoleName()));
		return userDTO;
	}

	public ProjectDTO convertProject(Project project) {

		List<UserDTO> userDTOList = new ArrayList<UserDTO>();
		for (User user : project.getUser()) {
			UserDTO userDTO = new UserDTO();
			userDTO.setEmail(user.getEmail());
			userDTO.setUserName(user.getName());
			userDTOList.add(userDTO);
		}
		ProjectDTO projectDTO = new ProjectDTO();
		projectDTO.setProjectId(project.getProjectId());
		projectDTO.setProjectName(project.getProjectName());
		projectDTO.setTimePreference(project.getTimePreference());
		projectDTO.setUserList(userDTOList);
		projectDTO.setUsersCount(project.getUser().size());
		return projectDTO;
	}

	public ProjectDTO convertProjectList(Project project) {

		ProjectDTO projectDTO = new ProjectDTO();
		projectDTO.setProjectId(project.getProjectId());
		projectDTO.setProjectName(project.getProjectName());
		projectDTO.setTimePreference(project.getTimePreference());
		return projectDTO;
	}

	public StatusDTO convertStatus(Status status) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GlobalConstants.TIME_FORMAT);

		StatusDTO statusDTO = new StatusDTO();
		statusDTO.setStatusId(status.getStatusId());
		statusDTO.setProjectName(status.getProject().getProjectName());
		statusDTO.setUserName(status.getUser().getName());
		statusDTO.setWorkedOn(status.getWorkedOn());
		statusDTO.setWorkingOn(status.getWorkingOn());
		statusDTO.setHours(status.getHours());
		statusDTO.setBlockers(status.getBlockers());
		statusDTO.setTime(simpleDateFormat.format(status.getDateTime()).toString());
		return statusDTO;
	}

	public Map<String, Object> getResponseMapWithGridDataAndUserStatusCount(List<User> users) {
		List<UserDTO> userDTOList = new ArrayList<UserDTO>();
		long joinedCount = 0;
		long pendingCount = 0;
		for (User user : users) {
			if (User.PENDING.equalsIgnoreCase(user.getStatus())) {
				pendingCount++;
			} else if (User.JOINED.equalsIgnoreCase(user.getStatus())) {
				joinedCount++;
			}
			userDTOList.add(convertUser(user));
		}
		return getResponseMapWithGridDataAndUserStatusCount(joinedCount, pendingCount, userDTOList);
	}

	public Map<String, Object> getResponseMapWithGridDataAndProjectCount(List<Project> projects) {
		List<ProjectDTO> projectDTOList = new ArrayList<>();
		long projectCount = 0;
		projectCount = projects.size();
		for (Project project : projects) {
			projectDTOList.add(convertProject(project));
		}
		return getResponseMapWithGridDataAndProjectCount(projectCount, projectDTOList);
	}

	public Map<String, Object> getResponseMapWithGridDataAndProjectList(List<Project> projects) {
		List<ProjectDTO> projectDTOList = new ArrayList<>();
		long projectCount = 0;
		projectCount = projects.size();
		for (Project project : projects) {
			projectDTOList.add(convertProjectList(project));
		}
		return getResponseMapWithGridDataAndProjectCount(projectCount, projectDTOList);
	}

	private Map<String, Object> getResponseMapWithGridDataAndUserStatusCount(long joinedCount, long pendingCount,
			List<UserDTO> gridData) {
		Map<String, Object> responseData = new LinkedHashMap<String, Object>();
		responseData.put(JOINED_USERS_COUNT, joinedCount);
		responseData.put(PENDING_USERS_COUNT, pendingCount);
		responseData.put(RestResponse.GRID_DATA, gridData);
		return responseData;
	}

	private Map<String, Object> getResponseMapWithGridDataAndProjectCount(long projectCount,
			List<ProjectDTO> gridData) {
		Map<String, Object> responseData = new LinkedHashMap<String, Object>();
		responseData.put(PROJECT_COUNT, projectCount);
		responseData.put(RestResponse.GRID_DATA, gridData);
		return responseData;
	}

	public Map<String, Object> getResponseMapWithGridDataAndStatus(List<Status> statusses) {

		Map<String, List<StatusDTO>> statusMap = new HashMap<String, List<StatusDTO>>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);
		for (Status status : statusses) {
			String date = simpleDateFormat.format(status.getDateTime());
			StatusDTO statusDTO = convertStatus(status);

			if (statusMap.containsKey(date)) {
				statusMap.get(date).add(statusDTO);
			} else {
				List<StatusDTO> statusDTOList = new LinkedList<StatusDTO>();
				statusDTOList.add(statusDTO);
				statusMap.put(date, statusDTOList);
			}
		}
		Map<String, Object> responseData = new LinkedHashMap<String, Object>();
		responseData.put(RestResponse.GRID_DATA, getStatusResponse(statusMap));
		return responseData;
	}

	public List<StatusResponseDTO> getStatusResponse(Map<String, List<StatusDTO>> statusMap) {
		List<StatusResponseDTO> statusResponseDTOList = new LinkedList<StatusResponseDTO>();
		Set<String> keys = statusMap.keySet();
		for (String key : keys) {
			StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
			statusResponseDTO.setDate(key);
			statusResponseDTO.setStatusList(statusMap.get(key));
			statusResponseDTOList.add(statusResponseDTO);
		}
		return statusResponseDTOList;
	}

}
