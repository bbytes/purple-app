package com.bbytes.purple.auth.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.bbytes.purple.repository.UserRepository;

public class AuthUserDetailsService implements UserDetailsService {

	private final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

	@Autowired
	private UserRepository userRepository;

	@Override
	public final User loadUserByUsername(String username) throws UsernameNotFoundException {
		final com.bbytes.purple.domain.User user = userRepository.findOneByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with email '" + username + "'");
		}
		try {
			// need to add expired , account locked etc to mongo db user domain
			// object
			User userDetail = new User(user.getEmail(), user.getPassword(),
					AuthorityUtils.createAuthorityList(user.getUserRole().getRoleName()));
			detailsChecker.check(userDetail);
			return userDetail;
		} catch (Exception ex) {
			throw new UsernameNotFoundException(ex.getMessage());
		}
	}

}
