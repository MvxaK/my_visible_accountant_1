package org.cook.extracter.repository;

import org.cook.extracter.entity.TransactionEntity;
import org.cook.extracter.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findAllByUserId(Long userId);

    Collection<TransactionEntity> findByUserId(Long userId);

    Long user(UserEntity user);
}
