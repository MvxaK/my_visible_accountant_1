package org.cook.extracter.controller.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cook.extracter.model.AlertRule;
import org.cook.extracter.model.Transaction;
import org.cook.extracter.model.User;
import org.cook.extracter.security.auth.RegisterRequest;
import org.cook.extracter.security.details.CustomUserDetails;
import org.cook.extracter.service.interfaces.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserWebController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final AlertRuleService alertRuleService;

    @GetMapping("/profile")
    public String getProfilePage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userService.getUserById(userDetails.getId());

        List<Transaction> transactions = transactionService.getTransactionsByUserId(userDetails.getId());
        List<AlertRule> alertRules = alertRuleService.getAllAlertRulesByUserId(userDetails.getId());

        List<Transaction> recent = transactions.stream()
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .limit(5)
                .toList();

        model.addAttribute("user", user);
        model.addAttribute("transactionCount", transactions.size());
        model.addAttribute("alertRuleCount", alertRules.size());
        model.addAttribute("recentTransactions", recent);

        return "profile";
    }

}
