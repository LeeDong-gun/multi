package com.sparta.domain.user.controller;


import static com.sparta.common.ApiResMessage.*;
import static com.sparta.common.ApiResponse.*;

import com.sparta.common.ApiResponse;
import com.sparta.config.CustomUserDetails;
import com.sparta.domain.user.dto.request.ChangePasswordDto;
import com.sparta.domain.user.dto.request.DeleteUserRequestDto;
import com.sparta.domain.user.dto.request.ResetPasswordConfirmDto;
import com.sparta.domain.user.dto.request.ResetPasswordDto;
import com.sparta.domain.user.dto.request.UpdateUserImgUrlReqeustDto;
import com.sparta.domain.user.dto.request.UpdateUserRequestDto;
import com.sparta.domain.user.dto.response.UserResponseDto;
import com.sparta.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2")
public class UserController {

	private final UserService userService;

	@GetMapping("/admin/users/{userId}")
	public ApiResponse<UserResponseDto> findUserById(
			@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long userId) {
		String role = customUserDetails.
				getAuthorities().
				iterator().next().
				getAuthority();
		UserResponseDto responseDto = userService.findUserById(role, userId);

		return success(HttpStatus.OK, FIND_SUCCESS, responseDto);
	}

	@GetMapping("/users")
	public ApiResponse<UserResponseDto> findUser(
			@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		UserResponseDto responseDto = userService.findUser(customUserDetails.getId());

		return success(HttpStatus.OK, FIND_SUCCESS, responseDto);
	}

	@PatchMapping("/users")
	public ApiResponse<UserResponseDto> updateUser(
			@AuthenticationPrincipal CustomUserDetails customUserDetails,
			@Valid @RequestBody UpdateUserRequestDto dto
	) {

		UserResponseDto responseDto = userService.updateUser(customUserDetails.getId(), dto);

		return success(HttpStatus.OK, UPDATE_SUCCESS, responseDto);
	}

	@PatchMapping("/users/changingPassword")
	public ApiResponse<Void> changePassword(
			@AuthenticationPrincipal CustomUserDetails customUserDetails,
			@Valid @RequestBody ChangePasswordDto dto) {
		userService.changePassword(customUserDetails.getId(), dto);

		return success(HttpStatus.OK, PASSWORD_CHANGE_SUCCESS);
	}

	@PatchMapping("/users/profileImage")
	public ApiResponse<UserResponseDto> updateImgUrl(
			@AuthenticationPrincipal CustomUserDetails customUserDetails,
			@Valid @RequestBody UpdateUserImgUrlReqeustDto dto
	) {
		UserResponseDto responseDto = userService.updateImgUrl(customUserDetails.getId(), dto);

		return success(HttpStatus.OK, UPDATE_SUCCESS, responseDto);
	}

	@DeleteMapping("/users")
	public ApiResponse<Void> deleteUser(
			@AuthenticationPrincipal CustomUserDetails customUserDetails,
			@Valid @RequestBody DeleteUserRequestDto dto
	) {
		userService.deleteUser(customUserDetails.getId(), dto);

		return success(HttpStatus.OK, DELETE_SUCCESS);
	}

	@PostMapping("/users/resetPassword")
	public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordDto dto) {
		userService.resetPassword(dto);
		return success(HttpStatus.OK, RESET_EMAIL_SEND_SUCCESS);
	}

	@PostMapping("/users/resetPasswordConfirm")
	public ApiResponse<Void> resetPasswordConfirm(@Valid @RequestBody ResetPasswordConfirmDto dto) {
		userService.resetPasswordConfirm(dto);
		return success(HttpStatus.OK, RESET_PASSWORD_SUCCESS);
	}

}
