package com.sparta.domain.bill.controller;

import com.sparta.common.ApiResponse;
import com.sparta.config.CustomUserDetails;
import com.sparta.domain.bill.dto.responseDto.BillResponseDto;
import com.sparta.domain.bill.service.BillServiceImplV2;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.sparta.common.ApiResMessage.*;
import static com.sparta.common.ApiResponse.*;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v2/bills")
@RequiredArgsConstructor
public class BillControllerV2 {

    private final BillServiceImplV2 billService;

    @Value("${toss.client.key}")
    private String tossClientKey;

    // 결제내역 페이징 조회(tutor 전용)
    @GetMapping("/tutor")
    public ApiResponse<Page<BillResponseDto>> findBillsByTutor(
            @AuthenticationPrincipal CustomUserDetails authUser,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Long userId = authUser.getId();
        Page<BillResponseDto> billById = billService.findBillsByTutor(userId, pageable);
        return success(OK, BILL_FIND, billById);
    }

    // 결제내역 페이징 조회(student 전용)
    @GetMapping("/student")
    public ApiResponse<Page<BillResponseDto>> findBillsByStudent(
            @AuthenticationPrincipal CustomUserDetails authUser,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Long userId = authUser.getId();
        Page<BillResponseDto> billById = billService.findBillsByStudent(userId, pageable);
        return success(OK, BILL_FIND, billById);
    }

    // 결제내역 단건 조회(tutor 전용)
    @GetMapping("/tutor/{billId}")
    public ApiResponse<BillResponseDto> findBillByTutor(
            @AuthenticationPrincipal CustomUserDetails authUser,
            @PathVariable Long billId
    ) {
        Long userId = authUser.getId();
        BillResponseDto bill = billService.findBillByTutor(userId, billId);
        return success(OK, BILL_FIND, bill);
    }

    // 결제내역 단건 조회(student 전용)
    @GetMapping("/student/{billId}")
    public ApiResponse<BillResponseDto> findBillByStudent(
            @AuthenticationPrincipal CustomUserDetails authUser,
            @PathVariable Long billId
    ) {
        Long userId = authUser.getId();
        BillResponseDto bill = billService.findBillByStudent(userId, billId);
        return success(OK, BILL_FIND, bill);
    }

    // 결제내역 삭제(tutor)
    @DeleteMapping("/tutor/{billId}")
    public ApiResponse<Void> deleteBillByTutor(
            @AuthenticationPrincipal CustomUserDetails auth,
            @PathVariable Long billId
    ) {
        Long userId = auth.getId();
        billService.deleteBillByTutor(userId, billId);
        return success(OK, BILL_DELETE);
    }

    // 결제내역 삭제(student)
    @DeleteMapping("/student/{billId}")
    public ApiResponse<Void> deleteBillByStudent(
            @AuthenticationPrincipal CustomUserDetails auth,
            @PathVariable Long billId
    ) {
        Long userId = auth.getId();
        billService.deleteBillByStudent(userId, billId);
        return success(OK, BILL_DELETE);
    }

    @GetMapping("/client-key")
    public String getClientKey() {
        return tossClientKey;
    }
}