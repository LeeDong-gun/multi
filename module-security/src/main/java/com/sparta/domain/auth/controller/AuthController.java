package com.sparta.domain.auth.controller;

import static com.sparta.common.ApiResMessage.*;
import static com.sparta.common.ApiResponse.*;
import static org.springframework.http.HttpStatus.CREATED;

import com.sparta.common.ApiResponse;
import com.sparta.domain.auth.dto.request.OAuthUserRequestDto;
import com.sparta.domain.auth.dto.request.SignInUserRequestDto;
import com.sparta.domain.auth.dto.request.SignUpUserRequestDto;
import com.sparta.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/signup")
	public ApiResponse<Void> signUpUser(@Valid @RequestBody SignUpUserRequestDto dto) {
		authService.signUpUser(dto);

		return success(CREATED, SIGNUP_SUCCESS);
	}

	@PostMapping("/oauth2signup")
	public ApiResponse<Void> oAuth2signUpUser(@Valid @RequestBody OAuthUserRequestDto dto) {
		authService.oAuth2signUpUser(dto);

		return success(CREATED, SIGNUP_SUCCESS);
	}

	@PostMapping("/signin")
	public ResponseEntity<ApiResponse<Void>> signInUser(@Valid @RequestBody SignInUserRequestDto dto) {
		HttpHeaders headers = authService.authenticate(dto);

		ApiResponse<Void> responseBody = ApiResponse.success(HttpStatus.OK, LOGIN_SUCCESS);

		return ResponseEntity.ok().headers(headers).body(responseBody);
	}
}
