package com.sparta.domain.auth.service;

import static com.sparta.exception.common.ErrorCode.AUTH_TYPE_MISMATCH;

import com.sparta.config.CustomUserDetails;
import com.sparta.domain.user.entity.UserEntity;
import com.sparta.domain.user.repository.UserRepository;
import com.sparta.exception.common.AlreadyDeletedUserException;
import com.sparta.exception.common.MismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity user = userRepository.findByEmailOrElseThrow(email);
		if (user.getIsDeleted()) {
			throw new AlreadyDeletedUserException();
		}
		String provider = user.getProvider();
		if (!provider.startsWith("none")) {
			throw new MismatchException(AUTH_TYPE_MISMATCH);
		}

		return new CustomUserDetails(user);
	}
}
