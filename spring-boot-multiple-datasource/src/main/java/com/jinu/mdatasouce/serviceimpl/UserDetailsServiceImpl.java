package com.jinu.mdatasouce.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.jinu.mdatasouce.user.model.User;
import com.jinu.mdatasouce.user.model.UserRoles;
import com.jinu.mdatasouce.user.repository.UserRepository;
import com.jinu.mdatasouce.user.repository.UserRolesRepository;
import com.jinu.mdatasouce.security.MyUserDetails;

public class UserDetailsServiceImpl implements UserDetailsService {
	Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRolesRepository userRolesRepository;

	@Override	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.getUserByUsername(username);
		com.jinu.mdatasouce.security.User securityUser = createSecurityUserFromUser(user);
		if (user == null) {
			throw new UsernameNotFoundException("Could not find user");
		}
		
		List<UserRoles> userRoles = userRolesRepository.getUserRolesByUser(user);
		List<GrantedAuthority> authorities = userRoles.stream().map(e->new SimpleGrantedAuthority(e.getRole())).collect(Collectors.toList());
        securityUser.setAuthorities(authorities);
		return new MyUserDetails(securityUser);
	}

	private com.jinu.mdatasouce.security.User createSecurityUserFromUser(User user) {
		com.jinu.mdatasouce.security.User securityUser = new com.jinu.mdatasouce.security.User();
		securityUser.setUsername(user.getUsername());
		securityUser.setPassword(user.getPassword());
		securityUser.setEmail(user.getEmail());
		securityUser.setAccountNonExpired(user.getAccountNonExpired());
		securityUser.setAccountNonLocked(user.getAccountNonLocked());
		securityUser.setEnabled(user.getEnabled());
		return securityUser;
	}
}
