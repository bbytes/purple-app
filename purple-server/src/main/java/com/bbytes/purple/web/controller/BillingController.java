package com.bbytes.purple.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.plutus.client.PlutusClient;
import com.bbytes.plutus.client.PlutusClientException;
import com.bbytes.plutus.enums.AppProfile;
import com.bbytes.plutus.enums.ProductName;
import com.bbytes.plutus.response.ProductStatsRestResponse;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.rest.dto.models.RestResponse;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.ErrorHandler;

@RestController
public class BillingController {
	private static final Logger logger = LoggerFactory.getLogger(BillingController.class);

	@Value("${plutus.base.url}")
	private String plutusBaseUrl;

	@Autowired
	private UserService userService;

	private PlutusClient getPlutusClient(Organization organization) {
		if (organization != null && organization.getSubscriptionKey() == null) {
			logger.warn("Subscription Key not available for organization with id " + organization.getOrgId());
			return null;
		}

		PlutusClient plutusClient = PlutusClient.create(plutusBaseUrl, organization.getSubscriptionKey(),
				organization.getSubscriptionSecret(), AppProfile.saas);
		return plutusClient;
	}

	@RequestMapping(value = "/api/v1/billing/pricingPlans", method = RequestMethod.GET)
	public RestResponse getAllProjectsByUser() throws PurpleException, PlutusClientException {

		Organization organization = userService.getLoggedInUser().getOrganization();
		PlutusClient plutusClient = getPlutusClient(organization);
		if (plutusClient == null)
			throw new PurpleException("Subscription information not available for organization" + organization.getOrgId(),
					ErrorHandler.SERVER_ERROR);
		
		ProductStatsRestResponse plutusResponse = plutusClient.getPricingPlans(ProductName.Statusnap.toString());
		RestResponse response = new RestResponse(plutusResponse.isSuccess(), plutusResponse.getData()) ;
		return response;
	}

}