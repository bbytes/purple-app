package com.bbytes.purple.scheduler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;

import com.bbytes.purple.auth.jwt.TokenAuthenticationProvider;
import com.bbytes.purple.domain.ProjectUserCountStats;
import com.bbytes.purple.domain.TenantResolver;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.repository.TenantResolverRepository;
import com.bbytes.purple.service.AdminService;
import com.bbytes.purple.service.ConfigSettingService;
import com.bbytes.purple.service.EmailService;
import com.bbytes.purple.service.NotificationService;
import com.bbytes.purple.service.StatusService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.TenancyContextHolder;

/**
 * Scheduler Service
 * 
 * @author akshay
 *
 */
@Component
public class SchedulerService {

	@Autowired
	private TenantResolverRepository tenantResolverRepository;

	@Autowired
	protected TokenAuthenticationProvider tokenAuthenticationProvider;

	@Autowired
	private AdminService adminService;

	@Autowired
	private UserService userService;

	@Autowired
	private StatusService statusService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private ConfigSettingService configSettingService;

	@Value("${base.url}")
	private String baseUrl;

	private TaskScheduler taskScheduler;

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
	@Scheduled(cron = "0 0/30 * * * ?")
	public void emailSchedule() throws PurpleException, ParseException {

		List<TenantResolver> tenantResolver = tenantResolverRepository.findAll();
		Set<String> orgId = new LinkedHashSet<String>();
		for (TenantResolver tr : tenantResolver) {
			orgId.add(tr.getOrgId());
		}
		for (String org : orgId) {
			TenancyContextHolder.setTenant(org);
			List<User> allUsers = adminService.getAllUsers();
			for (User user : allUsers) {

				if (user.getTimePreference() == null) {
					user.setTimePreference(User.DEFAULT_EMAIL_REMINDER_TIME);
					userService.save(user);
				}

				String timePreference = user.getTimePreference();

				DateFormat outputFormat = new SimpleDateFormat(GlobalConstants.SCHEDULER_TIME_FORMAT);
				DateFormat inputFormat = new SimpleDateFormat(GlobalConstants.SCHEDULER_DATE_FORMAT);
				DateFormat dateFormat = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);

				Date date = inputFormat.parse(timePreference);
				String outputTime = outputFormat.format(date);

				DateTime now = DateTime.now();
				LocalTime userTime = new LocalTime(outputTime);
				DateTime userTimeDatetime = DateTime.now().withTime(userTime);

				if (now.isBefore(userTimeDatetime) && userTimeDatetime.isBefore(now.plusMinutes(30))
						&& user.isEmailNotificationState()) {
					String hours = new SimpleDateFormat("HH").format(date);
					String minutes = new SimpleDateFormat("mm").format(date);
					DateTime dateTime = new DateTime().withHourOfDay(Integer.parseInt(hours));
					dateTime = dateTime.withMinuteOfHour(Integer.parseInt(minutes));

					List<String> emailList = new ArrayList<String>();

					long currentDate = new Date().getTime();
					String statusEditEnableDays = configSettingService
							.getConfigSettingbyOrganization(user.getOrganization()).getStatusEnable();

					if (statusEditEnableDays == null)
						statusEditEnableDays = "1";

					Integer validHours = Integer.parseInt(statusEditEnableDays) * 24;

					String postDate = dateFormat.format(new Date());

					final String xauthToken = tokenAuthenticationProvider.getAuthTokenForUser(user.getEmail(),
							validHours);

					Map<String, Object> emailBody = new HashMap<>();
					emailBody.put(GlobalConstants.USER_NAME, user.getName());
					emailBody.put(GlobalConstants.CURRENT_DATE, postDate);
					emailBody.put(GlobalConstants.ACTIVATION_LINK, baseUrl + GlobalConstants.STATUS_URL + xauthToken
							+ GlobalConstants.STATUS_DATE + currentDate);
					emailBody.put(GlobalConstants.SETTING_LINK, baseUrl + GlobalConstants.SETTING_URL + xauthToken);
					emailBody.put(GlobalConstants.VALID_HOURS, validHours);

					emailList.add(user.getEmail());
					taskScheduler.schedule(new EmailSendJob(emailBody, emailList, notificationService),
							dateTime.toDate());

				}
			}
		}
		TenancyContextHolder.setDefaultTenant();
	}

	@Scheduled(cron = "0 0 10 * * ?")
	public void sendEmailforStatusUpdate() throws PurpleException, ParseException {

		final String subject = GlobalConstants.ASSOCIATE_LIST_SUBJECT;
		final String template = GlobalConstants.ASSOCIATES_EMAIL_TEMPLATE;
		DateFormat dateFormat = new SimpleDateFormat(GlobalConstants.DATE_FORMAT);

		List<TenantResolver> tenantResolver = tenantResolverRepository.findAll();
		Set<String> orgId = new LinkedHashSet<String>();
		for (TenantResolver tr : tenantResolver) {
			orgId.add(tr.getOrgId());
		}
		for (String org : orgId) {
			TenancyContextHolder.setTenant(org);
			List<User> allUsers = adminService.getAllUsers();
			Date startDate = new DateTime(new Date()).minusDays(1).withTimeAtStartOfDay().toDate();
			Date endDate = new DateTime(new Date()).withTimeAtStartOfDay().toDate();

			Iterable<ProjectUserCountStats> result = statusService.getUserofStatus(startDate, endDate);
			Set<User> userList = new LinkedHashSet<User>();
			for (Iterator<ProjectUserCountStats> iterator = result.iterator(); iterator.hasNext();) {
				ProjectUserCountStats projectUserCountStats = (ProjectUserCountStats) iterator.next();
				userList.add(projectUserCountStats.getUser());
			}
			List<User> userListToBeSendMail = new LinkedList<User>();
			Set<String> nameList = new LinkedHashSet<String>();

			for (User user : allUsers) {
				if (!userList.toString().contains(user.toString())) {
					userListToBeSendMail.add(user);
					nameList.add(user.getName());
				}
				if (user.getUserRole().getRoleName().equals("MANAGER"))
					userListToBeSendMail.add(user);
			}
			List<String> emailList = new ArrayList<String>();

			for (User userFromList : userListToBeSendMail) {
				emailList.add(userFromList.getEmail());
			}

			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			for (String name : nameList) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("name", name);
				list.add(map);
			}
			String postDate = dateFormat.format(startDate);

			Map<String, Object> emailBody = new HashMap<>();
			emailBody.put(GlobalConstants.CURRENT_DATE, postDate);
			emailBody.put(GlobalConstants.USER_NAME, list);

			if (!nameList.isEmpty())
				emailService.sendEmail(emailList, emailBody, subject, template);
		}
	}

}
