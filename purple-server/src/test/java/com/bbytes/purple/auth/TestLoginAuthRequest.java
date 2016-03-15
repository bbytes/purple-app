package com.bbytes.purple.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.bbytes.purple.PurpleApplication;
import com.bbytes.purple.auth.jwt.SpringSecurityConfig;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.domain.UserRole;
import com.bbytes.purple.repository.OrganizationRepository;
import com.bbytes.purple.service.UserService;
import com.bbytes.purple.utils.GlobalConstants;
import com.bbytes.purple.utils.TenancyContextHolder;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { PurpleApplication.class, SpringSecurityConfig.class })
@WebAppConfiguration
public class TestLoginAuthRequest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@Autowired
	private FilterChainProxy filterChainProxy;

	@Autowired
	private UserService userService;

	@Autowired
	private OrganizationRepository organizationRepository;

	private User adminUser1;

	private Organization testOrg;

	private String password;
	private String email;

	private String xauthToken;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).dispatchOptions(true).addFilters(filterChainProxy)
				.build();

		testOrg = new Organization("test", "Test-Org");

		password = "test123";
		email = "admin1@test.com";
		adminUser1 = new User("admin-1", email);
		adminUser1.setOrganization(testOrg);
		adminUser1.setUserRole(UserRole.ADMIN_USER_ROLE);

		TenancyContextHolder.setTenant(adminUser1.getOrganization().getOrgId());
		organizationRepository.save(testOrg);
		userService.save(adminUser1);
		userService.updatePassword(password, adminUser1);

	}

	@After
	public void cleanUp() {
		TenancyContextHolder.setTenant(adminUser1.getOrganization().getOrgId());
		userService.deleteAll();
		organizationRepository.deleteAll();
	}

	@Test
	public void testAnonymousAccess() throws Exception {
		mockMvc.perform(get("/api/user/account")).andExpect(status().is4xxClientError());
	}

	@Test
	public void testLoginFailed() throws Exception {
		mockMvc.perform(get("/auth/login").param("username", "email@test.com").param("password", "plainttext")
				.header(GlobalConstants.HEADER_TENANT_ID, "wrong")).andExpect(status().is4xxClientError());
	}

	@Test
	public void testLoginSuccess() throws Exception {
		mockMvc.perform(get("/auth/login").param("username", email).param("password", password)
				.header(GlobalConstants.HEADER_TENANT_ID, adminUser1.getOrganization().getOrgId()))
				.andExpect(status().isOk());
	}

	@Test
	public void testURLAccessDenied() throws Exception {
		mockMvc.perform(get("/app/status")).andExpect(status().isUnauthorized());
	}

	@Test
	public void testLoginAndAccessProtectedUrl() throws Exception {
		mockMvc.perform(get("/auth/login").param("username", email).param("password", password))
				.andDo(new ResultHandler() {

					@Override
					public void handle(MvcResult result) throws Exception {
						xauthToken = result.getResponse().getHeader(GlobalConstants.HEADER_AUTH_TOKEN);

					}
				});
		
		mockMvc.perform(get("/app/status").param("username", email).param("password", password)
				.header(GlobalConstants.HEADER_AUTH_TOKEN, xauthToken))
				.andExpect(status().isOk());
	}

}
