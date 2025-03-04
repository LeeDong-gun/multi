package com.sparta.domain.order.repository;

import com.sparta.domain.order.entity.OrderEntity;
import com.sparta.exception.common.ErrorCode;
import com.sparta.exception.common.NotFoundException;
import com.sparta.utill.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    boolean existsByUserIdAndProductIdAndStatus(Long userId, Long productId, OrderStatus status);

    default OrderEntity findByIdOrElseThrow(Long orderId) {
        return findById(orderId).orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND));
    }
}
