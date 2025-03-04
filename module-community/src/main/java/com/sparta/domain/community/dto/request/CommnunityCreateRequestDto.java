package com.sparta.domain.community.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.sparta.domain.community.dto.CommunityValidMessage.*;

@Getter
@RequiredArgsConstructor
public class CommnunityCreateRequestDto {
	@NotBlank(message = TITLE_REQUIRED)
	private final String title;
	@NotBlank(message = CONTENT_REQUIRED)
	private final String content;
	@NotNull(message = GAME_ID_REQUIRED)
	private final Long gameId;
}
