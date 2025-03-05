package com.sparta.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.domain.review.entity.ReviewEntity;
import com.sparta.exception.common.ErrorCode;
import com.sparta.exception.common.NotFoundException;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

	default ReviewEntity findByIdOrElseThrow(Long id) {
		return findById(id).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
	}

	boolean existsByUserIdAndProductId(Long userId, Long productId);

}
