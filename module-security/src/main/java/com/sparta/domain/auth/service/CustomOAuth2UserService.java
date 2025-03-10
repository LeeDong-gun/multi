package com.sparta.domain.auth.service;

import static com.sparta.domain.user.dto.UserMessage.CONGRATULATION_SIGNUP;

import com.sparta.config.CustomOAuth2User;
import com.sparta.domain.auth.dto.response.GoogleResponseDto;
import com.sparta.domain.auth.dto.response.KakaoResponseDto;
import com.sparta.domain.auth.dto.response.NaverResponseDto;
import com.sparta.domain.auth.dto.response.OAuth2ResponseDto;
import com.sparta.domain.email.dto.request.SendEmailDto;
import com.sparta.domain.email.event.EmailEventPublisher;
import com.sparta.domain.user.entity.UserEntity;
import com.sparta.domain.user.repository.UserRepository;
import com.sparta.exception.common.ErrorCode;
import com.sparta.utill.UserRole;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;
	private final EmailEventPublisher emailEventPublisher;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		OAuth2User oAuth2User = super.loadUser(userRequest);

		OAuth2ResponseDto oAuth2ResponseDto = null;
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		if (registrationId.equals("naver")) {

			oAuth2ResponseDto = new NaverResponseDto(
				(Map<String, Object>)oAuth2User.getAttributes().get("response"));

		} else if (registrationId.equals("google")) {

			oAuth2ResponseDto = new GoogleResponseDto(oAuth2User.getAttributes());

		} else {
			oAuth2ResponseDto = new KakaoResponseDto(oAuth2User.getAttributes());
		}

		UserEntity user = null;

		if (userRepository.existsByEmail(oAuth2ResponseDto.getEmail())) {
			user = userRepository.findByEmailOrElseThrow(oAuth2ResponseDto.getEmail());
			if (user.getIsDeleted()) {
				throw new OAuth2AuthenticationException(ErrorCode.ALREADY_DELETED_USER.toString());
			}
			if (!user.getProvider().startsWith(registrationId)) {
				throw new OAuth2AuthenticationException(ErrorCode.AUTH_TYPE_MISMATCH.toString());
			}
		} else {
			user = UserEntity.builder()
				.email(oAuth2ResponseDto.getEmail())
				.nickName(oAuth2ResponseDto.getNickName())
				.role(UserRole.USER)
				.provider(registrationId + "new")
				.build();
			userRepository.save(user);
			emailEventPublisher.publisher(
				new SendEmailDto(user.getEmail(), CONGRATULATION_SIGNUP, CONGRATULATION_SIGNUP));
		}

		return new CustomOAuth2User(user);
	}

}
