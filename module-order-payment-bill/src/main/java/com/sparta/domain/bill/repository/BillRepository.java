package com.sparta.domain.bill.repository;

import com.sparta.domain.bill.entity.BillEntity;
import com.sparta.domain.order.entity.OrderEntity;
import com.sparta.exception.common.ErrorCode;
import com.sparta.exception.common.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BillRepository extends JpaRepository<BillEntity, Long>, BillRepositoryCustom{

    default BillEntity findByIdOrElseThrow(Long billId) {
        return findById(billId).orElseThrow(() -> new NotFoundException(ErrorCode.BILL_NOT_FOUND));
    }

    @Query("SELECT b FROM BillEntity b JOIN FETCH b.tutor JOIN FETCH b.student WHERE b.id = :billId")
    Optional<BillEntity> findByIdWithTutorAndStudent(Long billId);

    Optional<BillEntity> findByOrderId(Long order);

    Optional<BillEntity> findByOrder(OrderEntity order);
}
