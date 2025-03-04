package com.sparta.domain.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class CancelResponseDto {
    private String paymentKey;
    private String cancelReason;
}
