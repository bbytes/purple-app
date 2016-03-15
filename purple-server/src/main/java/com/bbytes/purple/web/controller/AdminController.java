package com.bbytes.purple.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.rest.dto.models.UserDTO;
import com.bbytes.purple.service.AdminService;
import com.bbytes.purple.service.OrganizationService;
import com.bbytes.purple.utils.SuccessHandler;
import com.bbytes.purple.utils.TenancyContextHolder;

@RestController
public class AdminController {

	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	private AdminService adminService;
	
	@Autowired
	private OrganizationService organizationService;

	/**
	 * 
	 * @param userDTO
	 * @return
	 * @throws PurpleException
	 */
	@RequestMapping(value = "/admin/users/adduser", method = RequestMethod.POST)
	public RestResponse addUsers(@RequestBody UserDTO userDTO) throws PurpleException {

		Organization org = organizationService.findByOrgId(TenancyContextHolder.getTenant());
		User addUser = new User(userDTO.getUserName(), userDTO.getEmail());
		addUser.setOrganization(org);
		addUser.setStatus(User.PENDING);

		User user = adminService.addUsers(addUser);
		
		logger.debug("User with email  '" +  userDTO.getEmail() + "' are added successfully");
		RestResponse userReponse = new RestResponse(RestResponse.SUCCESS, user, SuccessHandler.ADD_USER_SUCCESS);

		return userReponse;
	}
}
