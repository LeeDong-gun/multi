package com.sparta.domain.payment.service;

import com.sparta.config.CustomUserDetails;
import com.sparta.config.TossPaymentConfig;
import com.sparta.domain.order.entity.OrderEntity;
import com.sparta.domain.order.repository.OrderRepository;
import com.sparta.domain.payment.dto.request.CancelPaymentRequestDto;
import com.sparta.domain.payment.dto.response.CancelResponseDto;
import com.sparta.domain.payment.dto.response.PaymentResponseDto;
import com.sparta.domain.payment.entity.PaymentEntity;
import com.sparta.domain.payment.repository.PaymentRepository;
import com.sparta.exception.common.*;
import com.sparta.utill.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.sparta.exception.common.ErrorCode.*;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final TossPaymentConfig tossPaymentConfig;
    private final RateLimitService rateLimitService;

    @Transactional
    @Override
    public PaymentResponseDto createPayment(CustomUserDetails auth, Long orderId) {
        Long userId = auth.getId();
        OrderEntity order = orderRepository.findByIdOrElseThrow(orderId);

        if (!order.getUser().getId().equals(userId)) {
            throw new ForbiddenException(FORBIDDEN_ACCESS);
        }

        logger.info("주문상태: {}", order.getStatus());
        // 결제 대기 상태에서 결제요청 불가
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderException(INVALID_ORDER_STATUS);
        }


        // 결제 정보가 있다면 새로 만들지말고 결제 정보를 조회해서 사용
        PaymentEntity existingPayment = paymentRepository.findByOrder(order);
        if (existingPayment != null) {
            PaymentResponseDto response = new PaymentResponseDto(existingPayment);
            response.setSuccessUrl(tossPaymentConfig.getSuccessUrl());
            response.setFailUrl(tossPaymentConfig.getFailUrl());
            logger.info("결제정보Id: {}", existingPayment.getPaymentId());
            return response;
        }

        PaymentEntity payment = PaymentEntity.builder()
                .orderId(UUID.randomUUID().toString())
                .order(order)
                .customer(order.getUser())
                .orderName(order.getProduct().getProductName())
                .amount(order.getTotalPrice())
                .customerName(order.getUser().getNickName())
                .customerEmail(order.getUser().getEmail())
                .userKey(order.getUser().getCustomerKey())
                .ispaid(false)
                .iscanceled(false)
                .build();

        PaymentResponseDto response = new PaymentResponseDto(payment);
        response.setSuccessUrl(tossPaymentConfig.getSuccessUrl());
        response.setFailUrl(tossPaymentConfig.getFailUrl());
        paymentRepository.save(payment);
        logger.info("결제정보Id 생성: {}", payment.getPaymentId());

        return response;
    }

    @Override
    public CancelResponseDto requestCancel(CustomUserDetails auth, CancelPaymentRequestDto dto) {

        PaymentEntity payment = paymentRepository.findByPaymentKey(dto.getKey())
                .orElseThrow(() -> new PaymentException(PAYMENT_NOT_FOUND));

        // 판매자 검증
        if (!payment.getOrder().getProduct().getUser().getId().equals(auth.getId())) {
            throw new ForbiddenException(FORBIDDEN_ACCESS);
        }

        // 취소 완료 되었는지 검증
        if (payment.getOrder().getStatus().equals(OrderStatus.CANCELED)) {
            throw new PaymentException(PAYMENT_CANCELED_OK);
        }

        // 결제 요청 상태일 때 취소 불가
        if (payment.getOrder().getStatus().equals(OrderStatus.PENDING)) {
            throw new PaymentException(PAYMENT_PENDING);
        }

        if (!rateLimitService.isRequest(auth.getId())) {
            throw new BusinessException(INVALID_REQUEST_MANY);
        }

        logger.info("취소 이유: {}, paymentKey: {}", dto.getReason(), dto.getKey());

        return CancelResponseDto.builder()
                .cancelReason(dto.getReason())
                .paymentKey(dto.getKey())
                .build();
    }

}
