package com.bbytes.purple.auth.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bbytes.purple.service.TenantResolverService;

@Configuration
@EnableWebSecurity
@Order(2)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	private AuthUserDetailsService userService;

	@Autowired
	private TokenAuthenticationProvider tokenAuthenticationProvider;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TenantResolverService tenantResolverService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// CSRF Disabled and it is ok. Plz read the explanation from
		// stackoverflow
		/*
		 * Note : "If we go down the cookies way, you really need to do CSRF to
		 * avoid cross site requests. That is something we can forget when using
		 * JWT as you will see." (JWT = Json Web Token, a Token based
		 * authentication for stateless apps)
		 */
		http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.exceptionHandling().and().servletApi().and().authorizeRequests()

				// Allow anonymous resource requests
				.antMatchers("/").permitAll().antMatchers("/favicon.ico").permitAll().antMatchers("/**/*.html")
				.permitAll().antMatchers("/**/*.css").permitAll().antMatchers("/**/*.js").permitAll()

				// Allow logins urls
				.antMatchers("/auth/**").permitAll()

				// All other request need to be authenticated
				.anyRequest().authenticated().and()

				// Custom Token based authentication based on the header
				// previously given to the client

				.addFilterAfter(new StatelessAuthenticationFilter("/auth/**", tokenAuthenticationProvider),
						UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(new StatelessLoginFilter("/auth/login", tokenAuthenticationProvider, userService,
						tenantResolverService, authenticationManager), StatelessAuthenticationFilter.class)
				.headers().cacheControl().and();

	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(createMutliTenantAuthenticationProvider()).userDetailsService(userDetailsService())
				.passwordEncoder(new BCryptPasswordEncoder());
	}

	@Bean
	public AuthenticationProvider createMutliTenantAuthenticationProvider() {
		return new MutliTenantAuthenticationProvider();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	@Override
	public AuthUserDetailsService userDetailsService() {
		this.userService = new AuthUserDetailsService();
		return userService;
	}

	@Bean
	public TokenAuthenticationProvider tokenAuthenticationProvider() {
		this.tokenAuthenticationProvider = new TokenAuthenticationProvider();
		return tokenAuthenticationProvider;
	}
}