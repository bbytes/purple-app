package com.bbytes.purple.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.User;
import com.bbytes.purple.rest.dto.models.BaseDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.rest.dto.models.UserDTO;


@Service
public class DataModelToDTOConversionService {
	
	public static final String JOINED_USERS_COUNT = "joined_count";
	public static final String PENDING_USERS_COUNT = "pending_count";
	
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
		userDTO.setEmail(user.getEmail());
		userDTO.setUserName(user.getName());
		userDTO.setStatus(user.getStatus());
		userDTO.setUserRole(convertToBaseDTO(user.getUserRole().getRoleName()));
		return userDTO;
	}
	
	public Map<String, Object> getResponseMapWithGridDataAndUserStatusCount(List<User> users) {
		List<UserDTO> userDTOList = new ArrayList<UserDTO>();
		long joinedCount = 0;
		long pendingCount = 0;
		for (User user : users) {
			if(User.PENDING.equalsIgnoreCase(user.getStatus())) {
				pendingCount++;
			} else if(User.JOINED.equalsIgnoreCase(user.getStatus())) {
				joinedCount++;
			}
			userDTOList.add(convertUser(user));
		}
		return getResponseMapWithGridDataAndUserStatusCount(joinedCount, pendingCount, userDTOList);
	}
	
	private Map<String, Object> getResponseMapWithGridDataAndUserStatusCount(long joinedCount, long pendingCount, List<?> gridData) {
		Map<String, Object> responseData = new LinkedHashMap<String, Object>();
		responseData.put(JOINED_USERS_COUNT, joinedCount);
		responseData.put(PENDING_USERS_COUNT, pendingCount);
		responseData.put(RestResponse.GRID_DATA, gridData);
		return responseData;
	}
	
}
