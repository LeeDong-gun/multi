package com.sparta.domain.payment.service;

import com.sparta.config.CustomUserDetails;
import com.sparta.domain.payment.dto.request.CancelPaymentRequestDto;
import com.sparta.domain.payment.dto.response.CancelResponseDto;
import com.sparta.domain.payment.dto.response.PaymentResponseDto;

public interface PaymentService {
    PaymentResponseDto createPayment(CustomUserDetails auth, Long orderId);

    CancelResponseDto requestCancel(CustomUserDetails auth, CancelPaymentRequestDto dto);
}
