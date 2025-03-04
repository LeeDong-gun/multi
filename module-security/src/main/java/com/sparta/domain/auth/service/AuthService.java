package com.sparta.domain.auth.service;

import com.sparta.domain.auth.dto.request.*;
import org.springframework.http.HttpHeaders;

public interface AuthService {

	void signUpUser(SignUpUserRequestDto dto);

	void oAuth2signUpUser(OAuthUserRequestDto dto);

	HttpHeaders authenticate(SignInUserRequestDto dto);
}
