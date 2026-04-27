package org.cook.extracter.controller.web;

import lombok.RequiredArgsConstructor;
import org.cook.extracter.model.AlertRule;
import org.cook.extracter.model.Category;
import org.cook.extracter.security.details.CustomUserDetails;
import org.cook.extracter.service.interfaces.AlertRuleService;
import org.cook.extracter.service.interfaces.CategoryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertRuleWebController {

    private final AlertRuleService alertRuleService;
    private final CategoryService categoryService;

    @GetMapping
    public String alertsPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<AlertRule> alerts = isAdmin ? alertRuleService.getAllAlertRules() : alertRuleService.getAllAlertRulesByUserId(userDetails.getId());

        List<Category> categories = isAdmin ? categoryService.getAllCategories() : categoryService.getAllCategoriesByUserId(userDetails.getId());

        model.addAttribute("alerts", alerts);
        model.addAttribute("categories", categories);
        model.addAttribute("isAdmin", isAdmin);

        return "alerts/list";
    }

    @GetMapping("/{id}/edit")
    public String editAlertPage(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        List<Category> categories = categoryService.getAllCategoriesByUserId(userDetails.getId());

        model.addAttribute("alert", alertRuleService.getAlertRuleById(id));
        model.addAttribute("categories", categories);

        return "alerts/edit";
    }
}