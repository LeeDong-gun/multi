package com.sparta.domain.game.repository;

import static com.sparta.exception.common.ErrorCode.GAME_NOT_FOUND;

import com.sparta.domain.game.entity.GameEntity;
import com.sparta.exception.common.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<GameEntity, Long> {
	default GameEntity findByIdOrElseThrow(Long id){
		return findById(id).orElseThrow(() -> new NotFoundException(GAME_NOT_FOUND));
	}

}
