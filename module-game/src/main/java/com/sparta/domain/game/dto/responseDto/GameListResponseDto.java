package com.sparta.domain.game.dto.responseDto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GameListResponseDto {
	private final List<GameResponseDto> gameList;
}
