package org.cook.extracter.controller.api;

import lombok.RequiredArgsConstructor;
import org.cook.extracter.model.AlertRule;
import org.cook.extracter.security.details.CustomUserDetails;
import org.cook.extracter.service.interfaces.AlertRuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alert_rules")
@RequiredArgsConstructor
public class AlertRuleApiController {

    private final AlertRuleService alertRuleService;

    @GetMapping
    public ResponseEntity<List<AlertRule>> getAllAlertRules() {
        List<AlertRule> alertRules = alertRuleService.getAllAlertRules();

        return ResponseEntity.ok(alertRules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertRule> getAlertRuleById(@PathVariable Long id) {
        AlertRule alertRule = alertRuleService.getAlertRuleById(id);

        return ResponseEntity.ok(alertRule);
    }

    @GetMapping("/my")
    public ResponseEntity<List<AlertRule>> getMyAlertRules(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<AlertRule> alertRules = alertRuleService.getAllAlertRulesByUserId(userDetails.getId());

        return ResponseEntity.ok(alertRules);
    }

    @PostMapping
    public ResponseEntity<AlertRule> createAlertRule(@RequestBody AlertRule alertRule, @AuthenticationPrincipal CustomUserDetails userDetails) {
        alertRule.setUserId(userDetails.getId());

        AlertRule createdAlertRule = alertRuleService.createAlertRule(alertRule);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdAlertRule);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertRule> updateAlertRule(@PathVariable Long id, @RequestBody AlertRule alertRule, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        AlertRule updatedAlertRule = alertRuleService.updateAlertRule(id, alertRule, userDetails.getId(), isAdmin);

        return ResponseEntity.ok(updatedAlertRule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlertRule(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        alertRuleService.deleteAlertRule(id, userDetails.getId(), isAdmin);
        return ResponseEntity.noContent().build();
    }
}