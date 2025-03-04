package com.sparta.domain.community.repository;

import com.sparta.domain.community.entity.CommunityEntity;
import com.sparta.exception.common.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import static com.sparta.exception.common.ErrorCode.*;

public interface CommunityRepository extends JpaRepository<CommunityEntity, Long> {
	Page<CommunityEntity> findAllByIsDeletedFalse(Pageable pageable);

	default CommunityEntity findByIdOrElseThrow(Long id) {
		return findById(id).orElseThrow(() -> new NotFoundException(COMMUNITY_NOT_FOUND));
	}
}
