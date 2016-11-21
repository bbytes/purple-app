package com.bbytes.purple.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bbytes.plutus.client.PlutusClient;
import com.bbytes.plutus.client.PlutusClientException;
import com.bbytes.plutus.enums.AppProfile;
import com.bbytes.plutus.enums.BillingCycle;
import com.bbytes.plutus.enums.Currency;
import com.bbytes.plutus.enums.ProductName;
import com.bbytes.plutus.model.SubscriptionInfo;
import com.bbytes.plutus.response.SubscriptionRegisterRestResponse;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.TenancyContextHolder;

@Service
public class RegistrationService {

	private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

	@Autowired
	private OrganizationService orgService;

	@Autowired
	private UserService userService;

	@Autowired
	private TenantResolverService tenantResolverService;

	@Value("${plutus.base.url}")
	private String plutusBaseUrl;

	private PlutusClient plutusClient;

	@PostConstruct
	private void init() {
		plutusClient = PlutusClient.create(plutusBaseUrl, AppProfile.saas);
	}

	public void signUp(Organization org, User user) throws PurpleException {

		if (org != null && user != null) {

			if (tenantResolverService.emailExist(user.getEmail()))
				throw new PurpleException("Error while sign up, email exist", ErrorHandler.EMAIL_NOT_UNIQUE);

			if (tenantResolverService.organizationExist(org.getOrgId()))
				throw new PurpleException("Error while sign up, org not unique", ErrorHandler.ORG_NOT_UNIQUE);

			try {
				// update Plutus server for billing information and other
				// details for saas subscription
				createPlutusSubscription(org, user);
			} catch (PlutusClientException ex) {
				logger.error(ex.getMessage(), ex);
				throw new PurpleException("Subscription failed", ErrorHandler.SIGN_UP_FAILED);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.SIGN_UP_FAILED, e);
			}
		}
	}

	/**
	 * Create new Subscription in plutus for the new organization account in
	 * statusnap
	 * 
	 * @param tenantId
	 * @param orgName
	 * @param email
	 * @param userName
	 * @return
	 * @throws PlutusClientException
	 */
	public void createPlutusSubscription(Organization org, User user) throws PlutusClientException {
		SubscriptionInfo subscriptionInfo = new SubscriptionInfo();
		subscriptionInfo.setAppProfile(AppProfile.saas);
		subscriptionInfo.setBillingAddress("N/A");
		subscriptionInfo.setBillingCycle(BillingCycle.Monthly);
		subscriptionInfo.setContactNo("N/A");
		subscriptionInfo.setCurrency(Currency.USD);
		subscriptionInfo.setCustomerName(org.getOrgName());
		subscriptionInfo.setEmail(user.getEmail());
		subscriptionInfo.setContactPerson(user.getEmail());
		subscriptionInfo.setProductName(ProductName.Statusnap.toString());
		subscriptionInfo.setTenantId(org.getOrgId());

		SubscriptionRegisterRestResponse response = plutusClient.register(subscriptionInfo);

		if (response.isSuccess()) {
			org.setSubscriptionKey(response.getSubscriptionKey());
			org.setSubscriptionSecret(response.getSubscriptionSecret());

			TenancyContextHolder.setTenant(org.getOrgId());
			org = orgService.save(org);
			if (userService.getUserByEmail(user.getEmail()) == null)
				userService.create(user.getEmail(), user.getName(), user.getPassword(), user.getOrganization());

		} else {
			logger.error("Subscription failed as plutus server response failed for org '" + org.getOrgName() + "' with email "
					+ user.getEmail());
			throw new PlutusClientException("Subscription creation failed");
		}
	}

	public User activateAccount(User activeUser) throws PurpleException {

		if (activeUser != null) {
			try {
				activeUser.setAccountInitialise(true);
				activeUser.setStatus(User.JOINED);
				userService.save(activeUser);
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.AUTH_FAILURE);
			}
		}
		return activeUser;
	}

	public User resendActivation(String email) throws PurpleException {
		User user = null;
		if (email != null && !email.isEmpty()) {
			String orgId = tenantResolverService.findTenantIdForUserEmail(email);
			TenancyContextHolder.setTenant(orgId);
			if (!userService.userEmailExist(email))
				throw new PurpleException("Error while resend activation link", ErrorHandler.USER_NOT_FOUND);
			try {
				user = userService.getUserByEmail(email);

			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.RESEND_ACTIVATION_FAILURE);
			}
		}
		return user;
	}
}
