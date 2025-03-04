package com.sparta.domain.payment.repository;

import com.sparta.domain.order.entity.OrderEntity;
import com.sparta.domain.payment.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByPaymentKey(String paymentKey);

    Optional<PaymentEntity> findByOrderId(String orderId);

    PaymentEntity findByOrder(OrderEntity order);
}
