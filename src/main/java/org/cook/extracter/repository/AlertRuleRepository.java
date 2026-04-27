package org.cook.extracter.repository;

import org.cook.extracter.entity.AlertRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AlertRuleRepository extends JpaRepository<AlertRuleEntity, Long> {

    List<AlertRuleEntity> findAllByUserId(Long userId);

    AlertRuleEntity findByUserIdAndCategoryId(Long userId, Long categoryId);
}
