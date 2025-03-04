package com.sparta.domain.community.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CommunityListResponseDto {
	private final List<CommunityReadResponseDto> communityList;

}
