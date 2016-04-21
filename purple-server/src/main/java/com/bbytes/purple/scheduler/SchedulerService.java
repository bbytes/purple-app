package com.bbytes.purple.scheduler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.TenantResolver;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.repository.TenantResolverRepository;
import com.bbytes.purple.service.AdminService;
import com.bbytes.purple.service.NotificationService;
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
	private AdminService adminService;

	@Autowired
	private NotificationService notificationService;

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
			List<Project> allProjects = adminService.getAllProjects();
			for (Project project : allProjects) {

				String timePreference = project.getTimePreference();
				DateFormat outputFormat = new SimpleDateFormat(GlobalConstants.SCHEDULER_TIME_FORMAT);
				DateFormat inputFormat = new SimpleDateFormat(GlobalConstants.SCHEDULER_DATE_FORMAT);

				Date date = inputFormat.parse(timePreference);
				String outputTime = outputFormat.format(date);

				LocalTime now = LocalTime.now();
				LocalTime projectTime = new LocalTime(outputTime);

				if (now.isBefore(projectTime) && projectTime.isBefore(now.plusMinutes(30))) {
					String hours = new SimpleDateFormat("HH").format(date);
					String minutes = new SimpleDateFormat("mm").format(date);
					DateTime dateTime = new DateTime().withHourOfDay(Integer.parseInt(hours));
					dateTime = dateTime.withMinuteOfHour(Integer.parseInt(minutes));
					List<String> emailList = new ArrayList<String>();
					for (User user : project.getUser()) {
						emailList.add(user.getEmail());
					}
					taskScheduler.schedule(new EmailSendJob(project, emailList, notificationService),
							dateTime.toDate());
				}
			}
		}
		TenancyContextHolder.setDefaultTenant();
	}
}
