package org.cook.extracter.service.interfaces;

import org.cook.extracter.model.AlertRule;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface AlertRuleService {

    AlertRule getAlertRuleById(Long id);
    List<AlertRule> getAllAlertRules();
    List<AlertRule> getAllAlertRulesByUserId(Long userId);
    AlertRule createAlertRule(AlertRule ruleToCreate);
    AlertRule updateAlertRule(Long id, AlertRule ruleToUpdate, Long requestingUserId, boolean isAdmin);
    void deleteAlertRule(Long id, Long requestingUserId, boolean isAdmin);

    Optional<AlertRule> findByUserIdAndCategoryId(Long userId, Long categoryId);
}
