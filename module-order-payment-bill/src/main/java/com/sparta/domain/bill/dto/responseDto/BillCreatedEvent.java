package com.sparta.domain.bill.dto.responseDto;

import com.sparta.domain.bill.entity.BillEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BillCreatedEvent {
    private final BillEntity bill;
}
