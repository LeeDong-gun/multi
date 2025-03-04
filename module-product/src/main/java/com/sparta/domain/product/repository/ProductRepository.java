package com.sparta.domain.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import com.sparta.domain.product.entity.ProductEntity;
import com.sparta.exception.common.ErrorCode;
import com.sparta.exception.common.NotFoundException;
import com.sparta.utill.ProductStatus;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

	List<ProductEntity> findAllByIsDeletedFalseAndStatus(ProductStatus status);

	default ProductEntity findByIdOrElseThrow(Long id) {
		return findById(id).orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
	}

	// 비관적 락 쿼리
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})  // 락 획특 시간 설정
	@Query("SELECT p FROM ProductEntity p WHERE p.id = :productId")
	Optional<ProductEntity> findByIdWithLock(Long productId);

	Optional<ProductEntity> findByProductName(String productName);
}
