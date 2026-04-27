package org.cook.extracter.controller.web;

import lombok.RequiredArgsConstructor;
import org.cook.extracter.model.*;
import org.cook.extracter.security.details.CustomUserDetails;
import org.cook.extracter.service.interfaces.AlertRuleService;
import org.cook.extracter.service.interfaces.CategoryService;
import org.cook.extracter.service.interfaces.CurrencyConverterService;
import org.cook.extracter.service.interfaces.TransactionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.format.TextStyle;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardWebController {

    private final TransactionService transactionService;
    private final CurrencyConverterService currencyConverterService;
    private final CategoryService categoryService;
    private final AlertRuleService alertRuleService;

    @GetMapping
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getId();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<Transaction> transactions = isAdmin ? transactionService.getAllTransactions() : transactionService.getTransactionsByUserId(userId);

        List<Category> categories = isAdmin ? categoryService.getAllCategories() : categoryService.getAllCategoriesByUserId(userId);

        List<AlertRule> alertRules = isAdmin ? alertRuleService.getAllAlertRules() : alertRuleService.getAllAlertRulesByUserId(userId);

        Map<Long, Category> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, c -> c, (a, b) -> a));

        List<Transaction> validTx = transactions.stream()
                .filter(t -> t.getCategoryId() != null && categoryMap.containsKey(t.getCategoryId()))
                .toList();

        Map<Boolean, List<Transaction>> partitioned = validTx.stream()
                .collect(Collectors.partitioningBy(t -> categoryMap.get(t.getCategoryId()).getType() == TransactionType.INCOME));

        List<Transaction> incomeTransactions = partitioned.getOrDefault(true, List.of());
        List<Transaction> expenseTransactions = partitioned.getOrDefault(false, List.of());

        BigDecimal totalIncome = incomeTransactions.stream()
                .map(currencyConverterService::convert)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = expenseTransactions.stream()
                .map(currencyConverterService::convert)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);

        Map<String, BigDecimal> incomeByMonth = incomeTransactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getCreatedAt().format(formatter),
                        LinkedHashMap::new,
                        Collectors.reducing(BigDecimal.ZERO, currencyConverterService::convert, BigDecimal::add)
                ));

        Map<String, BigDecimal> expenseByMonth = expenseTransactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getCreatedAt().format(formatter),
                        LinkedHashMap::new,
                        Collectors.reducing(BigDecimal.ZERO, currencyConverterService::convert, BigDecimal::add)
                ));

        Set<String> allMonths = new LinkedHashSet<>();
        allMonths.addAll(incomeByMonth.keySet());
        allMonths.addAll(expenseByMonth.keySet());

        Map<String, BigDecimal> expenseByCategory = expenseTransactions.stream()
                .collect(Collectors.groupingBy(
                        t -> categoryMap.get(t.getCategoryId()).getName(),
                        Collectors.reducing(BigDecimal.ZERO, currencyConverterService::convert, BigDecimal::add)
                ));

        Map<String, BigDecimal> incomeByCategory = incomeTransactions.stream()
                .collect(Collectors.groupingBy(
                        t -> categoryMap.get(t.getCategoryId()).getName(),
                        Collectors.reducing(BigDecimal.ZERO, currencyConverterService::convert, BigDecimal::add)
                ));

        Map<Long, BigDecimal> categorySpendingMap = expenseTransactions.stream()
                .collect(Collectors.groupingBy(Transaction::getCategoryId,
                        Collectors.reducing(BigDecimal.ZERO, currencyConverterService::convert, BigDecimal::add)));

        long triggeredCount = alertRules.stream()
                .filter(AlertRule::getIsActive)
                .filter(rule -> categorySpendingMap.getOrDefault(rule.getCategoryId(), BigDecimal.ZERO)
                        .compareTo(rule.getThresholdAmount()) > 0)
                .count();

        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalSpending", totalExpense);
        model.addAttribute("netBalance", totalIncome.subtract(totalExpense));

        model.addAttribute("monthLabels", allMonths);

        model.addAttribute("incomeData", allMonths.stream()
                .map(m -> incomeByMonth.getOrDefault(m, BigDecimal.ZERO))
                .toList());

        model.addAttribute("expenseData", allMonths.stream()
                .map(m -> expenseByMonth.getOrDefault(m, BigDecimal.ZERO))
                .toList());

        model.addAttribute("categoryLabels", expenseByCategory.keySet());
        model.addAttribute("categoryData", expenseByCategory.values());

        model.addAttribute("incomeCategoryLabels", incomeByCategory.keySet());
        model.addAttribute("incomeCategoryData", incomeByCategory.values());

        model.addAttribute("recentTransactions", transactions.stream()
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .limit(5)
                .toList());

        model.addAttribute("categories", categories);
        model.addAttribute("categoryMap", categoryMap);
        model.addAttribute("triggeredAlerts", triggeredCount);
        model.addAttribute("alertCount", alertRules.size());

        return "dashboard";
    }
}
