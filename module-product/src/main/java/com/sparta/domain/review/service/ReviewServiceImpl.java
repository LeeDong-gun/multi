package com.sparta.domain.review.service;

import static com.sparta.exception.common.ErrorCode.*;
import static com.sparta.utill.OrderStatus.COMPLETED;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.domain.order.repository.OrderRepository;
import com.sparta.domain.product.entity.ProductEntity;
import com.sparta.domain.product.repository.ProductRepository;
import com.sparta.domain.review.dto.request.ReviewRequestDto;
import com.sparta.domain.review.dto.response.ReviewResponseDto;
import com.sparta.domain.review.entity.ReviewEntity;
import com.sparta.domain.review.repository.ReviewQueryRepository;
import com.sparta.domain.review.repository.ReviewRepository;
import com.sparta.domain.user.entity.UserEntity;
import com.sparta.domain.user.repository.UserRepository;
import com.sparta.exception.common.DuplicateException;
import com.sparta.exception.common.ForbiddenException;
import com.sparta.exception.common.MismatchException;
import com.sparta.utill.UserRole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final ReviewQueryRepository reviewQueryRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;

	@Override
	@Transactional
	public ReviewResponseDto saveReview(ReviewRequestDto dto, Long userId, Long productId) {

		// 해당 상품을 거래 완료한 사용자인지 확인
		if (!orderRepository.existsByUserIdAndProductIdAndStatus(userId, productId, COMPLETED)) {
			throw new ForbiddenException(COMPLETED_ORDER_REQUIRED);
		}

		// 이미 리뷰를 작성한 유저인지 확인
		if (reviewRepository.existsByUserIdAndProductId(userId, productId)) {
			throw new DuplicateException(DUPLICATE_REVIEW);
		}

		UserEntity user = userRepository.findById(userId).orElseThrow(RuntimeException::new); // Todo: 변경 예정
		ProductEntity product = productRepository.findById(productId).orElseThrow(RuntimeException::new); // Todo: 변경 예정

		ReviewEntity review = ReviewEntity.builder()
			.contents(dto.getContents())
			.starScore(dto.getStarScore())
			.user(user)
			.product(product)
			.build();

		reviewRepository.save(review);

		return new ReviewResponseDto(review);
	}

	@Override
	@Transactional
	public void deleteReview(Long userId, Long productId, Long reviewId) {

		UserEntity user = userRepository.findById(userId).orElseThrow(RuntimeException::new); // Todo: 변경 예정

		// 리뷰 삭제는 관리자 권한만 실행 가능
		if (!user.getRole().equals(UserRole.ADMIN)) {
			throw new ForbiddenException(FORBIDDEN_ACCESS);
		}

		ReviewEntity review = reviewRepository.findByIdOrElseThrow(reviewId);

		// 이미 삭제된 리뷰인지 확인
		if (review.getIsDeleted()) {
			throw new DuplicateException(REVIEW_ISDELETED);
		}

		//리뷰가 해당 상품의 리뷰인 지 확인
		if (!review.getProduct().getId().equals(productId)) {
			throw new MismatchException(MISMATCH_REVIEW_PRODUCT);
		}

		review.deleteReview();
	}

	@Override
	public Slice<ReviewResponseDto> findReviews(Long productId, Pageable pageable) {
		return reviewQueryRepository.findReviews(productId, pageable);
	}
}
