package com.bbytes.purple;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bbytes.purple.auth.jwt.TokenAuthenticationProvider;
import com.bbytes.purple.repository.CommentRepository;
import com.bbytes.purple.repository.HolidaysRepository;
import com.bbytes.purple.repository.OrganizationRepository;
import com.bbytes.purple.repository.ProjectRepository;
import com.bbytes.purple.repository.StatusRepository;
import com.bbytes.purple.repository.UserRepository;
import com.bbytes.purple.service.CommentService;
import com.bbytes.purple.service.NotificationService;
import com.bbytes.purple.service.OrganizationService;
import com.bbytes.purple.service.ProjectService;
import com.bbytes.purple.service.SpringProfileService;
import com.bbytes.purple.service.StatusService;
import com.bbytes.purple.service.TenantResolverService;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.service.UtilityService;
import com.mongodb.MongoClient;

public class PurpleBaseApplicationTests extends PurpleApplicationTests {

	@Autowired
	MongoClient mongoClient;

	@Autowired
	protected StatusService statusService;

	@Autowired
	protected OrganizationService organizationService;

	@Autowired
	protected UserService userService;

	@Autowired
	protected ProjectService projectService;
	
	@Autowired
	protected CommentService commentService;

	@Autowired
	protected UtilityService utilityService;
	
	@Autowired
	protected NotificationService notificationService;

	@Autowired
	protected OrganizationRepository organizationRepository;

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected TokenAuthenticationProvider tokenAuthenticationProvider;

	@Autowired
	protected StatusRepository statusRepository;

	@Autowired
	protected ProjectRepository projectRepository;

	@Autowired
	protected CommentRepository commentRepository;
	
	@Autowired
	protected HolidaysRepository holiDaysRepository;
	
	@Autowired
	private TenantResolverService tenantResolverService;
	
	@Autowired
	protected SpringProfileService springProfileService;


	@Before
	public void cleanTenantMgmt() {
		
	}
	
	@After
	public void cleanUpData() {
		tenantResolverService.deleteAll();
	}

	@Test
	@Ignore
	public void contextLoads() {
	}

	/**
	 * Note : local is the default mongo db so dont remove it, if removed mongo will
	 * stop functioning
	 */
	@After
	public void clearTestCaseMongoDatabases() {
		for (String db : mongoClient.listDatabaseNames()) {
			if (!db.equalsIgnoreCase("local")) {
//				mongoClient.getDatabase(db).drop();
			}

		}
	}
}
