package com.sparta.domain.auth.dto.response;

import java.util.Map;

public class KakaoResponseDto implements OAuth2ResponseDto {

	private final Map<String, Object> attribute;
	private final Map<String, Object> account;
	private final Map<String, Object> profile;

	public KakaoResponseDto(Map<String, Object> attribute) {
		this.attribute = attribute;
		this.account = (Map<String, Object>)attribute.get("kakao_account");
		this.profile = (Map<String, Object>)attribute.get("profile");
	}

	@Override
	public String getProvider() {

		return "kakao";
	}

	@Override
	public String getEmail() {

		return account.get("email").toString();
	}

	@Override
	public String getNickName() {

		return profile.get("nickname").toString();
	}
}
