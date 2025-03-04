package com.sparta.domain.order.service;

import com.sparta.domain.order.dto.requestDto.OrderCreateRequestDto;
import com.sparta.domain.order.dto.responseDto.OrderResponseDto;

public interface OrderService {
    OrderResponseDto createOrder(Long userId, OrderCreateRequestDto dto);

    OrderResponseDto findOrder(Long userId, Long orderId);

    OrderResponseDto updateOrder(Long userId, Long orderId);

    OrderResponseDto completeOrder(Long userId, Long orderId);

    void deleteOrderByPending(Long userId, Long orderId);

    void deleteOrderByTrading(Long userId, Long orderId);
}
