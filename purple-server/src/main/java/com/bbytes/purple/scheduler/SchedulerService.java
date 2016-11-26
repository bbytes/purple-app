package com.bbytes.purple.scheduler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;

import com.bbytes.plutus.client.PlutusClient;
import com.bbytes.plutus.client.PlutusClientException;
import com.bbytes.plutus.enums.AppProfile;
import com.bbytes.plutus.model.ProductPlanStats;
import com.bbytes.plutus.response.ProductStatsRestResponse;
import com.bbytes.plutus.util.BillingConstant;
import com.bbytes.purple.auth.jwt.TokenAuthenticationProvider;
import com.bbytes.purple.domain.ConfigSetting;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.TenantResolver;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.repository.TenantResolverRepository;
import com.bbytes.purple.service.ConfigSettingService;
import com.bbytes.purple.service.NotificationService;
import com.bbytes.purple.service.OrganizationService;
import com.bbytes.purple.service.ProjectService;
import com.bbytes.purple.service.RegistrationService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.TenancyContextHolder;

/**
 * Scheduler Service for distributing email's.
 * 
 * @author Akshay
 *
 */
@Component
public class SchedulerService {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

	@Autowired
	private TenantResolverRepository tenantResolverRepository;

	@Autowired
	protected TokenAuthenticationProvider tokenAuthenticationProvider;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private UserService userService;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private ConfigSettingService configSettingService;

	@Autowired
	private RegistrationService registrationService;

	@Value("${base.url}")
	private String baseUrl;

	@Value("${plutus.base.url}")
	private String plutusBaseUrl;

	@Value("${email.scheduler.subject}")
	private String schedulerSubject;

	@Value("${email.associate.checklist.subject}")
	private String associateChecklistSubject;

	private TaskScheduler taskScheduler;

	public static final int SATURDAY = 6;

	public static final int SUNDAY = 7;

	/**
	 * Initialize the task scheduler
	 */
	@PostConstruct
	private void init() {
		ScheduledExecutorService localExecutor = Executors.newScheduledThreadPool(25);
		taskScheduler = new ConcurrentTaskScheduler(localExecutor);
	}

	/**
	 * emailSchedule method is used to schedule emails according to projects
	 * 
	 * @throws PurpleException
	 * @throws ParseException
	 */

	/* Cron Runs every 30 minutes */
	@Scheduled(cron = "0 0/30 * * * ?")
	public void dailyEmailSchedule() throws PurpleException, ParseException {

		List<TenantResolver> tenantResolverList = tenantResolverRepository.findAll();
		// creating a hashset to store orgId's
		Set<String> orgIdList = new LinkedHashSet<String>();
		for (TenantResolver tr : tenantResolverList) {
			orgIdList.add(tr.getOrgId());
		}
		for (String orgId : orgIdList) {
			TenancyContextHolder.setTenant(orgId);
			// flag is just to check whether weekend notification of
			// organization is ON or OFF
			boolean sendDailyEmail = true;
			LocalDate todaysDate = new LocalDate();
			// checking current day is weekend or not
			if (todaysDate.getDayOfWeek() == SATURDAY || todaysDate.getDayOfWeek() == SUNDAY) {
				// getting config setting
				ConfigSetting configSetting = configSettingService
						.getConfigSetting(organizationService.findByOrgId(orgId));
				if (configSetting != null) {
					if (configSetting.isWeekendNotification())
						sendDailyEmail = true;
					else
						sendDailyEmail = false;
				}
			}
			if (sendDailyEmail) {
				List<User> allUsers = userService.getAllUsers();
				for (User user : allUsers) {

					// initialise the user's time preference with default 6.00
					// pm if it not present
					if (user.getTimePreference() == null) {
						user.setTimePreference(User.DEFAULT_EMAIL_REMINDER_TIME);
						userService.save(user);
					}

					// check for user is active or not
					if (userService.isActiveUser(user)) {

						String timePreference = user.getTimePreference();

						DateFormat outputFormat = new SimpleDateFormat(GlobalConstants.SCHEDULER_TIME_FORMAT);
						DateFormat inputFormat = new SimpleDateFormat(GlobalConstants.SCHEDULER_DATE_FORMAT);
						DateFormat dateFormat = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);

						Date date = inputFormat.parse(timePreference);
						String outputTime = outputFormat.format(date);

						DateTime now = DateTime.now();
						LocalTime userTime = new LocalTime(outputTime);
						DateTime userTimeDatetime = DateTime.now().withTime(userTime);

						// checking condition whether timepreference is fall in
						// between current time to next 30 min. and user's email
						// notification setting should going to consider
						if (now.isBefore(userTimeDatetime) && userTimeDatetime.isBefore(now.plusMinutes(30))
								&& user.isEmailNotificationState()) {
							String hours = new SimpleDateFormat("HH").format(date);
							String minutes = new SimpleDateFormat("mm").format(date);
							DateTime dateTime = new DateTime().withHourOfDay(Integer.parseInt(hours));
							dateTime = dateTime.withMinuteOfHour(Integer.parseInt(minutes));

							List<String> emailList = new ArrayList<String>();

							long currentDate = new Date().getTime();
							String statusEditEnableDays = configSettingService.getConfigSetting(user.getOrganization())
									.getStatusEnable();

							if (statusEditEnableDays == null)
								statusEditEnableDays = "1";

							Integer validHours = Integer.parseInt(statusEditEnableDays) * 24;

							String postDate = dateFormat.format(new Date());

							final String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(user.getEmail(),
									validHours);

							Map<String, Object> emailBody = new HashMap<>();
							emailBody.put(GlobalConstants.USER_NAME, user.getName());
							emailBody.put(GlobalConstants.CURRENT_DATE, postDate);
							emailBody.put(GlobalConstants.ACTIVATION_LINK, baseUrl + GlobalConstants.STATUS_URL
									+ xauthToken + GlobalConstants.STATUS_DATE + currentDate);
							emailBody.put(GlobalConstants.SETTING_LINK,
									baseUrl + GlobalConstants.SETTING_URL + xauthToken);
							emailBody.put(GlobalConstants.VALID_HOURS, validHours);

							emailList.add(user.getEmail());
							// this is to schedule task for particular time
							taskScheduler.schedule(new EmailAndSlackSendJob(user, emailBody, emailList,
									notificationService, schedulerSubject), dateTime.toDate());

						}
					}
				}
			}
		}
		TenancyContextHolder.clearContext();
	}

	/**
	 * sendEmailforStatusUpdate Method is used to distribute emails to "MANAGER"
	 * who couldn't update status.
	 * 
	 * @throws PurpleException
	 * @throws ParseException
	 */

	/* Cron Runs every Tuesday-Saturday at 10 am */
	@Scheduled(cron = "	0 0 10 ? * TUE,WED,THU,FRI,SAT")
	// @Scheduled(cron = " 0/10 * * * * *")
	public void sendEmailToDefaulter() throws PurpleException, ParseException {

		final String template = GlobalConstants.ASSOCIATES_EMAIL_TEMPLATE;
		DateFormat dateFormat = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);

		List<TenantResolver> tenantResolverList = tenantResolverRepository.findAll();
		// creating a hashset to store orgId's
		Set<String> orgIdList = new LinkedHashSet<String>();
		for (TenantResolver tr : tenantResolverList) {
			orgIdList.add(tr.getOrgId());
		}
		for (String orgId : orgIdList) {

			TenancyContextHolder.setTenant(orgId);

			Date startDate = new DateTime(new Date()).minusDays(1).withTimeAtStartOfDay().toDate();
			Date endDate = new DateTime(new Date()).withTimeAtStartOfDay().toDate();

			// getting list of defaulter users
			List<User> defaulterUsers = userService.getDefaulterUsers(startDate, endDate);

			// creating map of users which will be key as ADMIN/Manager of
			// project and value would be defaulter users
			Map<User, Set<User>> defaulterMap = new LinkedHashMap<User, Set<User>>();

			// iterating all defaulter users
			for (User userFromDb : defaulterUsers) {

				// getting list of all project by user
				List<Project> projectList = projectService.findProjectByUser(userFromDb);
				// flag is just to check whether given project has Manager or
				// ADMIN
				boolean flag = true;
				// iterating all projects of above user
				for (Project project : projectList) {
					Set<User> usersFromProject = project.getUsers();
					// iterating all users of above project to search
					// manager or ADMIN present
					for (User userToSendList : usersFromProject) {
						if (userToSendList.getUserRole().equals(UserRole.MANAGER_USER_ROLE)) {

							// below condition to putting all key value pairs
							// into map for ex. manager as key and all defaulter
							// users are as values
							if (defaulterMap.containsKey(userToSendList)) {
								defaulterMap.get(userToSendList).add(userFromDb);
							} else {
								Set<User> defaulterUserList = new HashSet<User>();
								defaulterUserList.add(userFromDb);
								defaulterMap.put(userToSendList, defaulterUserList);
							}
							// setting flag to ensure at least manager is
							// present
							// in project, then below if condition would not
							// execute
							flag = false;
						}

					}
					if (flag) {
						for (User userToSendList : usersFromProject) {
							if (userToSendList.getUserRole().equals(UserRole.ADMIN_USER_ROLE)) {

								if (defaulterMap.containsKey(userToSendList)) {
									defaulterMap.get(userToSendList).add(userFromDb);
								} else {
									Set<User> defaulterUserList = new HashSet<User>();
									defaulterUserList.add(userFromDb);
									defaulterMap.put(userToSendList, defaulterUserList);
								}
							}
						}
					}
				}

			}

			// creating set to avoid sending duplicate email's to user if it
			// part
			// of more than one project and is in defaulter.
			Set<String> finalEmailList = new HashSet<String>();

			// iterating map for sending defaulter user email
			for (Map.Entry<User, Set<User>> entry : defaulterMap.entrySet()) {
				Set<String> emailSet = new LinkedHashSet<String>();
				Set<Map<String, String>> nameListMap = new LinkedHashSet<Map<String, String>>();
				// checking user is active or not
				if (userService.isActiveUser(entry.getKey()))
					emailSet.add(entry.getKey().getEmail());
				for (User user : entry.getValue()) {
					// checking user is active or not
					if (userService.isActiveUser(user)) {
						if (!finalEmailList.contains(user.getEmail())) {
							emailSet.add(user.getEmail());
							finalEmailList.add(user.getEmail());
						}
						// only adding normal users to namelist of defaulter
						if (user.getUserRole().equals(UserRole.NORMAL_USER_ROLE)) {
							Map<String, String> map = new HashMap<String, String>();
							map.put("name", user.getName());
							nameListMap.add(map);
						}
					}
				}

				List<String> emailList = new ArrayList<String>();
				emailList.addAll(emailSet);

				String postDate = dateFormat.format(startDate);

				Map<String, Object> emailBody = new HashMap<>();
				emailBody.put(GlobalConstants.CURRENT_DATE, postDate);
				emailBody.put(GlobalConstants.USER_NAME, nameListMap);

				if (!nameListMap.isEmpty() && !emailList.isEmpty()) {
					notificationService.sendTemplateEmail(emailList, associateChecklistSubject, template, emailBody);
				}

			}
		}
		TenancyContextHolder.clearContext();
	}

	/**
	 * cleanUpMarkForDeleteData Method is used to clean up the all users,
	 * statuses and comment data from db if user is set as mark for delete
	 * 
	 * @throws PurpleException
	 * @throws ParseException
	 */

	/* Cron Runs every day at 8 am */
	@Scheduled(cron = "	0 0 8 * * ?")
	public void cleanUpMarkForDeleteData() throws PurpleException {

		List<TenantResolver> tenantResolverList = tenantResolverRepository.findAll();

		// creating a hashset to store orgId's
		Set<String> orgIdList = new LinkedHashSet<String>();
		for (TenantResolver tr : tenantResolverList) {
			orgIdList.add(tr.getOrgId());
		}
		for (String orgId : orgIdList) {
			TenancyContextHolder.setTenant(orgId);
			List<User> allUsers = userService.getAllUsers();

			for (User userFromDb : allUsers) {
				// checking condition for mark delete
				if (userFromDb.isMarkDelete() && userFromDb.getMarkDeleteDate() != null) {
					// user DB listener is taking care to delete statuses,
					// comments and removing dbref from project
					if (userFromDb.getMarkDeleteDate().before(DateTime.now().toDate()))
						userService.delete(userFromDb);
				}
			}
		}

		TenancyContextHolder.clearContext();
	}

	/* runs every 4 hours */
	// @Scheduled(cron = "0 0/1 * * * ?")

	/* runs every 4 hours */
	@Scheduled(cron = "0 0 0/6 * * ?")
	public void sendStatsToPlutusServer() throws PurpleException, PlutusClientException {

		List<TenantResolver> tenantResolverList = tenantResolverRepository.findAll();

		// creating a hashset to store orgId's
		Set<String> orgList = new LinkedHashSet<String>();
		for (TenantResolver tr : tenantResolverList) {
			orgList.add(tr.getOrgId());
		}

		for (String orgId : orgList) {
			TenancyContextHolder.setTenant(orgId);
			Organization organization = organizationService.findByOrgId(orgId);

			if (organization != null && organization.getSubscriptionKey() == null) {
				logger.warn("Subscription Key not available for organization with id " + orgId);
				User user = userService.findTopByOrderByCreationDateAsc();
				registrationService.createPlutusSubscription(organization, user);
			}

			PlutusClient plutusClient = PlutusClient.create(plutusBaseUrl, organization.getSubscriptionKey(),
					organization.getSubscriptionSecret(), AppProfile.saas);

			ProductPlanStats productPlanStats = new ProductPlanStats();
			productPlanStats.addStats(BillingConstant.STATUSNAP_USER_COUNT, userService.countByMarkDelete(false));
			productPlanStats.addStats(BillingConstant.STATUSNAP_PROJECT_COUNT, projectService.count());
			productPlanStats.setSubscriptionKey(organization.getSubscriptionKey());
			ProductStatsRestResponse response = plutusClient.sendStats(productPlanStats);
			if (!response.isSuccess()) {
				logger.warn("Product stats updated failed for tenant " + orgId);
			}
		}

		TenancyContextHolder.clearContext();
	}

}
