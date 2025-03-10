package com.sparta.domain.review.controller;

import static com.sparta.common.ApiResMessage.REVIEW_DELETE;
import static com.sparta.common.ApiResMessage.REVIEW_LIST_SUCCESS;
import static com.sparta.common.ApiResMessage.REVIEW_SUCCESS;
import static com.sparta.common.ApiResponse.success;
import static org.springframework.http.HttpStatus.OK;

import com.sparta.common.ApiResponse;

import com.sparta.config.CustomUserDetails;
import com.sparta.domain.review.dto.request.ReviewRequestDto;
import com.sparta.domain.review.dto.response.ReviewResponseDto;
import com.sparta.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Review 생성 API
     *
     * @param dto contents(리뷰 내용), startScore(별점)
     */
    @PostMapping("/products/{productId}/reviews")
    public ApiResponse<ReviewResponseDto> saveReview(
        @AuthenticationPrincipal CustomUserDetails authUser,
        @Valid @RequestBody ReviewRequestDto dto,
        @PathVariable Long productId
    ) {
        ReviewResponseDto result = reviewService.saveReview(dto, authUser.getId(), productId);
        return success(OK ,REVIEW_SUCCESS, result);
    }

    /**
     * Review 삭제 API
     */
    @DeleteMapping("/admin/products/{productId}/reviews/{reviewId}")
    public ApiResponse<Void> deleteReview(
        @AuthenticationPrincipal CustomUserDetails authUser,
        @PathVariable Long productId,
        @PathVariable Long reviewId
    ) {
        reviewService.deleteReview(authUser.getId(), productId, reviewId);
        return success(OK, REVIEW_DELETE);
    }

    /**
     * Review 목록 조회 API
     *
     * @param pageable 무한스크롤 구조로 size만 받음
     * @return
     */
    @GetMapping("/products/{productId}/reviews")
    public ApiResponse<Slice<ReviewResponseDto>> findReviews(
        @PathVariable Long productId,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return success(OK, REVIEW_LIST_SUCCESS, reviewService.findReviews(productId, pageable));
    }
}
