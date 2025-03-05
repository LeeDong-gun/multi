package com.sparta.domain.game.service;

import com.sparta.domain.game.dto.requestDto.UpdateGameRequestDto;
import com.sparta.domain.game.dto.responseDto.GameListResponseDto;
import com.sparta.domain.game.entity.GameEntity;

public interface GameService {
	GameEntity saveGame(String name, String imgUrl, String genre, Long userId);

	GameEntity findGame(Long userId, Long gameId);

	void deleteGame(Long userId, Long gameId);

	GameEntity updateGame(Long userId, Long gameId, UpdateGameRequestDto dto);

	GameListResponseDto findGames();
}
