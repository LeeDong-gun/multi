package com.sparta.domain.order.controller;

import com.sparta.common.ApiResponse;
import com.sparta.config.CustomUserDetails;
import com.sparta.domain.order.dto.requestDto.OrderCreateRequestDto;
import com.sparta.domain.order.dto.responseDto.OrderResponseDto;
import com.sparta.domain.order.service.OrderServiceImplV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.sparta.common.ApiResMessage.*;
import static com.sparta.common.ApiResMessage.ORDER_CANCLED;
import static com.sparta.common.ApiResponse.success;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/v2/orders")
@RequiredArgsConstructor
public class OrderControllerV2 {

    private final OrderServiceImplV2 orderService;

    @PostMapping
    public ApiResponse<OrderResponseDto> createOrder(
            @AuthenticationPrincipal CustomUserDetails authUser,
            @RequestBody OrderCreateRequestDto dto
    ) {
        log.info("userId: {}", authUser.getId());
        Long userId = authUser.getId();
        OrderResponseDto orderResponseDto = orderService.createOrder(userId, dto);
        return success(OK, ORDER_CREATE, orderResponseDto);
    }

    // 주문 조회
    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponseDto> findOrder(
            @AuthenticationPrincipal CustomUserDetails authUser,
            @PathVariable Long orderId
    ) {
        Long userId = authUser.getId();
        OrderResponseDto orderById = orderService.findOrder(userId, orderId);
        return success(OK, ORDER_FIND, orderById);
    }

    // 주문 결제 완료
    @PatchMapping("/{orderId}")
    public ApiResponse<OrderResponseDto> updateOrder(
            @AuthenticationPrincipal CustomUserDetails authUser,
            @PathVariable Long orderId
    ) {
        Long userId = authUser.getId();
        OrderResponseDto order = orderService.updateOrder(userId, orderId);
        return success(OK, ORDER_UPDATE, order);
    }

    // 결제 완료
    @PatchMapping("/student/{orderId}")
    public ApiResponse<OrderResponseDto> completeOrder(
            @AuthenticationPrincipal CustomUserDetails authUser,
            @PathVariable Long orderId
    ) {
        Long userId = authUser.getId();
        OrderResponseDto order = orderService.completeOrder(userId, orderId);
        return success(OK, ORDER_COMPLETE, order);
    }

    // 주문 취소
    @DeleteMapping("/{orderId}")
    public ApiResponse<Void> deleteOrderByPending(
            @AuthenticationPrincipal CustomUserDetails authUser,
            @PathVariable Long orderId
    ) {
        Long userId = authUser.getId();
        orderService.deleteOrderByPending(userId, orderId);
        return success(OK, ORDER_CANCLED);
    }

    // 결제 취소 (거래중 일때)
    @DeleteMapping("/tutor/{orderId}")
    public ApiResponse<Void> deleteOrderByTrading(
            @AuthenticationPrincipal CustomUserDetails authUser,
            @PathVariable Long orderId
    ) {
        Long userId = authUser.getId();
        orderService.deleteOrderByTrading(userId, orderId);
        return success(OK, ORDER_CANCLED);
    }
}
