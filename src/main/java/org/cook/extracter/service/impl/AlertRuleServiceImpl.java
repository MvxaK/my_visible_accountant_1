package org.cook.extracter.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.cook.extracter.entity.AlertRuleEntity;
import org.cook.extracter.entity.CategoryEntity;
import org.cook.extracter.entity.UserEntity;
import org.cook.extracter.mapper.AlertRuleMapper;
import org.cook.extracter.model.AlertRule;
import org.cook.extracter.repository.AlertRuleRepository;
import org.cook.extracter.repository.CategoryRepository;
import org.cook.extracter.repository.UserRepository;
import org.cook.extracter.service.interfaces.AlertRuleService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlertRuleServiceImpl implements AlertRuleService {

    private final AlertRuleRepository alertRuleRepository;
    private final CategoryRepository categoryRepository;
    private final AlertRuleMapper alertRuleMapper;
    private final UserRepository userRepository;

    @Override
    public AlertRule getAlertRuleById(Long id) {
        return alertRuleRepository.findById(id)
                .map(alertRuleMapper::toModel)
                .orElseThrow(() -> new EntityNotFoundException("AlertRule not found with id -> " + id));
    }

    @Override
    public List<AlertRule> getAllAlertRules() {
        return alertRuleRepository.findAll()
                .stream()
                .map(alertRuleMapper::toModel)
                .toList();
    }

    @Override
    public List<AlertRule> getAllAlertRulesByUserId(Long userId) {
        return alertRuleRepository.findAllByUserId(userId)
                .stream()
                .map(alertRuleMapper::toModel)
                .toList();
    }

    @Override
    @Transactional
    public AlertRule createAlertRule(AlertRule ruleToCreate) {
        UserEntity userEntity = userRepository.findById(ruleToCreate.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id -> " + ruleToCreate.getUserId()));

        CategoryEntity categoryEntity = categoryRepository.findById(ruleToCreate.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("There is no category with id -> " + ruleToCreate.getCategoryId()));

        AlertRuleEntity alertRuleEntity = alertRuleMapper.toEntity(ruleToCreate);
        alertRuleEntity.setUser(userEntity);
        alertRuleEntity.setCategory(categoryEntity);
        alertRuleEntity.setIsActive(ruleToCreate.getIsActive());
        alertRuleEntity.setCreatedAt(java.time.LocalDateTime.now());

        return alertRuleMapper.toModel(alertRuleRepository.save(alertRuleEntity));
    }

    @Override
    @Transactional
    public AlertRule updateAlertRule(Long id, AlertRule ruleToUpdate, Long requestingUserId, boolean isAdmin) {
        AlertRuleEntity alertRuleEntity = alertRuleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AlertRule not found with id -> " + id));

        if (!isAdmin && !alertRuleEntity.getUser().getId().equals(requestingUserId)) {
            throw new AccessDeniedException("You don't have permission to update this alert rule");
        }

        alertRuleEntity.setThresholdAmount(ruleToUpdate.getThresholdAmount());
        alertRuleEntity.setCurrency(ruleToUpdate.getCurrency());
        alertRuleEntity.setIsActive(ruleToUpdate.getIsActive());

        return alertRuleMapper.toModel(alertRuleRepository.save(alertRuleEntity));
    }

    @Override
    @Transactional
    public void deleteAlertRule(Long id, Long requestingUserId, boolean isAdmin) {
        AlertRuleEntity alertRuleEntity = alertRuleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AlertRule not found with id -> " + id));

        if (!isAdmin && !alertRuleEntity.getUser().getId().equals(requestingUserId)) {
            throw new AccessDeniedException("You don't have permission to delete this alert rule");
        }

        alertRuleRepository.deleteById(id);
    }

    @Override
    public Optional<AlertRule> findByUserIdAndCategoryId(Long userId, Long categoryId) {
        AlertRuleEntity alertRuleEntity = alertRuleRepository.findByUserIdAndCategoryId(userId, categoryId);

        return Optional.ofNullable(alertRuleEntity).map(alert -> alertRuleMapper.toModel(alertRuleEntity));
    }
}