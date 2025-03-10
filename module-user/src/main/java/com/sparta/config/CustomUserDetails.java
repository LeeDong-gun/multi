package com.sparta.config;

import com.sparta.domain.user.entity.UserEntity;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class CustomUserDetails implements UserDetails {

	UserEntity user;

	public CustomUserDetails(UserEntity user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> grantedAuthority = new ArrayList<>();
		grantedAuthority.add(new GrantedAuthority() {
			@Override
			public String getAuthority() {
				return "ROLE_" + user.getRole().toString();
			}
		});

		return grantedAuthority;
	}

	public UserEntity getUser() {
		return user;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	public Long getId() {
		return user.getId();
	}

	public String getNickName() {
		return user.getNickName();
	}

	public String getCustomerKey() {
		return user.getCustomerKey();
	}
}
