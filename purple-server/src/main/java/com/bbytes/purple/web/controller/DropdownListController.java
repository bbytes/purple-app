package com.bbytes.purple.web.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.purple.rest.dto.models.BaseDTO;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.service.DataModelToDTOConversionService;
import com.bbytes.purple.utils.SuccessHandler;

/**
 * Dropdown list Controller for various dropdown list
 * 
 * @author akshay
 *
 */

@RestController
@RequestMapping(value = "/api/v1/dropdownList")
public class DropdownListController {

	private static final Logger logger = LoggerFactory.getLogger(DropdownListController.class);

	@Autowired
	private DataModelToDTOConversionService dataModelToDTOConversionService;

	/**
	 * getRoleDropdownList method is used to return all users role.
	 * 
	 * @return
	 */
	@RequestMapping(value = "/roles", method = RequestMethod.GET)
	public RestResponse getRoleDropdownList() {

		List<String> userRole = new LinkedList<String>();
		userRole.add("ADMIN");
		userRole.add("MANAGER");
		userRole.add("NORMAL");

		List<BaseDTO> roles = dataModelToDTOConversionService.convertRolesToEntityDTOList(userRole);

		logger.debug("Getting all users role successfully");
		RestResponse rolesResponse = new RestResponse(RestResponse.SUCCESS, roles, SuccessHandler.DROPDOWNLIST_SUCCESS);

		return rolesResponse;
	}

	/**
	 * getStatusEnableDropdownList method is used to return all status enable
	 * days.
	 * 
	 * @return
	 */
	@RequestMapping(value = "/statusEnable", method = RequestMethod.GET)
	public RestResponse getStatusEnableDropdownList() {

		Map<String, String> statusMap = new LinkedHashMap<String, String>();

		statusMap.put("1 Day", "1");
		statusMap.put("2 Days", "2");
		statusMap.put("3 Days", "3");

		List<BaseDTO> baseDTOList = new ArrayList<BaseDTO>();

		for (Entry<String, String> entry : statusMap.entrySet()) {

			BaseDTO baseDTO = new BaseDTO();
			baseDTO.setId(entry.getKey());
			baseDTO.setValue(entry.getValue());
			baseDTOList.add(baseDTO);
		}

		logger.debug("Getting days for enable successfully");
		RestResponse statusMapResponse = new RestResponse(RestResponse.SUCCESS, baseDTOList,
				SuccessHandler.DROPDOWNLIST_SUCCESS);

		return statusMapResponse;
	}

	/**
	 * getHoursDropdownList method is used to return hours for status.
	 * 
	 * @return
	 */
	@RequestMapping(value = "/hours", method = RequestMethod.GET)
	public RestResponse getHoursDropdownList() {

		List<String> hoursList = new LinkedList<String>();
		hoursList.add("1");
		hoursList.add("1.5");
		hoursList.add("2");
		hoursList.add("2.5");
		hoursList.add("3");
		hoursList.add("3.5");
		hoursList.add("4");
		hoursList.add("4.5");
		hoursList.add("5");
		hoursList.add("5.5");
		hoursList.add("6");
		hoursList.add("6.5");
		hoursList.add("7");
		hoursList.add("7.5");
		hoursList.add("8");
		hoursList.add("8.5");
		hoursList.add("9");
		hoursList.add("9.5");
		hoursList.add("10");
		hoursList.add("10.5");
		hoursList.add("11");
		hoursList.add("11.5");
		hoursList.add("12");

		List<BaseDTO> hours = dataModelToDTOConversionService.convertRolesToEntityDTOList(hoursList);

		logger.debug("Getting hours successfully");
		RestResponse hoursResponse = new RestResponse(RestResponse.SUCCESS, hours, SuccessHandler.DROPDOWNLIST_SUCCESS);

		return hoursResponse;
	}

}
