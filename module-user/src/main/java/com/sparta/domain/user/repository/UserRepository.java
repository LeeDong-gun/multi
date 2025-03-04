package com.sparta.domain.user.repository;



import static com.sparta.exception.common.ErrorCode.USER_NOT_FOUND;

import com.sparta.domain.user.entity.UserEntity;
import com.sparta.exception.common.EmailDuplicatedException;
import com.sparta.exception.common.NotFoundException;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;



public interface UserRepository extends JpaRepository<UserEntity, Long> {

	boolean existsByEmail(String email);

	default void existsByEmailOrElseThrow(String email) {
		if (existsByEmail(email)) {
			throw new EmailDuplicatedException();
		}
	}

	Optional<UserEntity> findByEmail(String email);

	default UserEntity findByEmailOrElseThrow(String email) {

		return findByEmail(email).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
	}

	default UserEntity findByIdOrElseThrow(Long userId) {

		return findById(userId)
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
	}

	Optional<UserEntity> findByCustomerKey(String userCustomerId);
}
