package com.sparta.domain.community.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.sparta.domain.community.dto.CommunityValidMessage.*;

@Getter
@RequiredArgsConstructor
public class CommunityUpdateRequestDto {
	@NotNull(message = COMMUNITY_ID_REQUIRED)
	private final Long communityId;
	private final String title;
	private final String content;
}
