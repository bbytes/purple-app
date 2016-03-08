package com.bbytes.purple.auth.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.bbytes.purple.repository.UserRepository;
import com.bbytes.purple.utils.GlobalConstants;

public class AuthUserDetailsService implements UserDetailsService {

	private final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public final User loadUserByUsername(String username) throws UsernameNotFoundException {
		final com.bbytes.purple.domain.User user = userRepository.findOneByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException("user not found");
		}
		// need to add expired , acount loced etc to mongo db user domain object
		User userDetail = new User(user.getEmail(), user.getPassword(),
				AuthorityUtils.createAuthorityList(GlobalConstants.ROLE_NORMAL_USER));
		detailsChecker.check(userDetail);
		return userDetail;
	}

	
}
