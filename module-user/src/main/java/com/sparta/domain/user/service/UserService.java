package com.sparta.domain.user.service;


import com.sparta.domain.user.dto.request.ChangePasswordDto;
import com.sparta.domain.user.dto.request.DeleteUserRequestDto;
import com.sparta.domain.user.dto.request.ResetPasswordConfirmDto;
import com.sparta.domain.user.dto.request.ResetPasswordDto;
import com.sparta.domain.user.dto.request.UpdateUserImgUrlReqeustDto;
import com.sparta.domain.user.dto.request.UpdateUserRequestDto;
import com.sparta.domain.user.dto.response.UserResponseDto;

public interface UserService {

	UserResponseDto findUserById(String role, Long id);

	UserResponseDto findUser(Long id);

	UserResponseDto updateUser(Long id, UpdateUserRequestDto dto);

	void changePassword(Long id, ChangePasswordDto dto);

	UserResponseDto updateImgUrl(Long id, UpdateUserImgUrlReqeustDto dto);

	void deleteUser(Long id, DeleteUserRequestDto dto);

	void resetPassword(ResetPasswordDto dto);

	void resetPasswordConfirm(ResetPasswordConfirmDto dto);
}
