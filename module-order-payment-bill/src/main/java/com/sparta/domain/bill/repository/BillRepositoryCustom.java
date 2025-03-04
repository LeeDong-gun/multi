package com.sparta.domain.bill.repository;

import com.sparta.domain.bill.entity.BillEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BillRepositoryCustom {
    Page<BillEntity> findTutorBills(Long tutorId, Pageable pageable);

    Page<BillEntity> findStudentBills(Long studentId, Pageable pageable);
}
