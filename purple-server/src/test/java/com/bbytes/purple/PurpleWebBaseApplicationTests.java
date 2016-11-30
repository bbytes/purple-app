package com.bbytes.purple;

import java.nio.charset.Charset;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.bbytes.purple.auth.jwt.SpringSecurityConfig;
import com.bbytes.purple.auth.jwt.TokenAuthenticationProvider;
import com.bbytes.purple.repository.CommentRepository;
import com.bbytes.purple.repository.HolidaysRepository;
import com.bbytes.purple.repository.OrganizationRepository;
import com.bbytes.purple.repository.ProjectRepository;
import com.bbytes.purple.repository.StatusRepository;
import com.bbytes.purple.repository.TaskItemRepository;
import com.bbytes.purple.repository.TaskListRepository;
import com.bbytes.purple.repository.UserRepository;
import com.bbytes.purple.service.CommentService;
import com.bbytes.purple.service.DataModelToDTOConversionService;
import com.bbytes.purple.service.IntegrationService;
import com.bbytes.purple.service.NotificationService;
import com.bbytes.purple.service.OrganizationService;
import com.bbytes.purple.service.ProjectService;
import com.bbytes.purple.service.SpringProfileService;
import com.bbytes.purple.service.StatusService;
import com.bbytes.purple.service.TenantResolverService;
import com.bbytes.purple.service.UserService;
import com.mongodb.MongoClient;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { PurpleApplication.class, SpringSecurityConfig.class })
@WebAppConfiguration
public class PurpleWebBaseApplicationTests extends PurpleApplicationTests {

	@Autowired
	MongoClient mongoClient;
	
	@Autowired
	protected DataModelToDTOConversionService dataModelToDTOConversionService;

	@Autowired
	protected StatusService statusService;

	@Autowired
	protected OrganizationService organizationService;
	
	@Autowired
	protected CommentService commentService;

	@Autowired
	protected UserService userService;

	@Autowired
	protected ProjectService projectService;
	
	@Autowired
	protected IntegrationService integrationService;

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
	protected TaskListRepository taskListRepository;
	
	@Autowired
	protected TaskItemRepository taskItemRepository;

	@Autowired
	protected WebApplicationContext context;

	protected MockMvc mockMvc;

	@Autowired
	protected FilterChainProxy filterChainProxy;

	@Autowired
	private TenantResolverService tenantResolverService;
	
	@Autowired
	protected SpringProfileService springProfileService;

	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).dispatchOptions(true).addFilters(filterChainProxy)
				.build();
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
	 * Note : local is the default mongo db so dont remove it, if removed mongo
	 * will stop functioning
	 */
	@After
	public void clearTestCaseMongoDatabases() {
		for (String db : mongoClient.listDatabaseNames()) {
			if (!db.equalsIgnoreCase("local")) {
				// mongoClient.getDatabase(db).drop();
			}

		}
	}
}
