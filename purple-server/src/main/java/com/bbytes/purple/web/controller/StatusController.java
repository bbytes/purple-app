package com.bbytes.purple.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.rest.dto.models.StatusDTO;
import com.bbytes.purple.service.StatusService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.SuccessHandler;

/**
 * Status Controller
 * 
 * @author akshay
 *
 */
@RestController
public class StatusController {

	private static final Logger logger = LoggerFactory.getLogger(StatusController.class);

	@Autowired
	private StatusService statusService;

	@Autowired
	private UserService userService;

	/**
	 * The add status method is used to save the status for project
	 * 
	 * @param statusDTO
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/status/add", method = RequestMethod.POST)
	public RestResponse addStatus(@RequestBody StatusDTO statusDTO) throws PurpleException {

		// We will get current logged in user
		User user = userService.getLoggedinUser();

		Status status = statusService.create(statusDTO, user);

		logger.debug("Status for project  '" + status.getProject().getProjectName() + "' is added successfully");
		RestResponse statusReponse = new RestResponse(RestResponse.SUCCESS, status, SuccessHandler.ADD_STATUS_SUCCESS);

		return statusReponse;
	}

	/**
	 * The get status method is used to get status for project
	 * 
	 * @param statusId
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/status/{statusid}", method = RequestMethod.GET)
	public RestResponse getStatus(@PathVariable("statusid") String statusId) throws PurpleException {

		Status status = statusService.getStatus(statusId);

		logger.debug("Status for project  '" + status.getProject().getProjectName() + "' is getting successfully");
		RestResponse statusReponse = new RestResponse(RestResponse.SUCCESS, status, SuccessHandler.GET_STATUS_SUCCESS);

		return statusReponse;
	}

	/**
	 * The get all status method is used to get all status related to project
	 * 
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/status", method = RequestMethod.GET)
	public RestResponse getAllStatus() throws PurpleException {

		List<Status> statusList = statusService.getAllStatus();

		logger.debug("All status are fetched successfully");
		RestResponse statusReponse = new RestResponse(RestResponse.SUCCESS, statusList,
				SuccessHandler.GET_STATUS_SUCCESS);

		return statusReponse;
	}

	/**
	 * The delete status method is used to delete particular status
	 * 
	 * @param statusId
	 * @param request
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/status/{statusid}", method = RequestMethod.DELETE)
	public RestResponse deleteStatus(@PathVariable("statusid") String statusId) throws PurpleException {
		final String DELETE_STATUS_SUCCESS_MSG = "Successfully deleted status";

		statusService.deleteStatus(statusId);

		logger.debug("Status with status id  '" + statusId + "' is deleted successfully");
		RestResponse statusReponse = new RestResponse(RestResponse.SUCCESS, DELETE_STATUS_SUCCESS_MSG,
				SuccessHandler.DELETE_STATUS_SUCCESS);

		return statusReponse;
	}

	/**
	 * The updateStatus method is used to update the status for particular
	 * project
	 * 
	 * @param statusId
	 * @param statusDTO
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/api/v1/status/update/{statusid}", method = RequestMethod.PUT)
	public RestResponse updateStatus(@PathVariable("statusid") String statusId, @RequestBody StatusDTO statusDTO)
			throws PurpleException {

		User user = userService.getLoggedinUser();

		Status status = statusService.updateStatus(statusId, statusDTO, user);

		logger.debug("Projects are fetched successfully");
		RestResponse statusReponse = new RestResponse(RestResponse.SUCCESS, status,
				SuccessHandler.UPDATE_STATUS_SUCCESS);

		return statusReponse;
	}
}
