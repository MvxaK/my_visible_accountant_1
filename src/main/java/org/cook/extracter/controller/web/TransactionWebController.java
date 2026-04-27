package org.cook.extracter.controller.web;

import lombok.RequiredArgsConstructor;
import org.cook.extracter.model.Category;
import org.cook.extracter.model.Transaction;
import org.cook.extracter.security.details.CustomUserDetails;
import org.cook.extracter.service.interfaces.CategoryService;
import org.cook.extracter.service.interfaces.TransactionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionWebController {

    private final TransactionService transactionService;
    private final CategoryService categoryService;

    @GetMapping
    public String transactionsPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<Transaction> transactions = isAdmin ? transactionService.getAllTransactions() : transactionService.getTransactionsByUserId(userDetails.getId());

        List<Category> categories = isAdmin ? categoryService.getAllCategories() : categoryService.getAllCategoriesByUserId(userDetails.getId());

        Map<Long, String> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        model.addAttribute("transactions", transactions);
        model.addAttribute("categoryMap", categoryMap);
        model.addAttribute("categories", categories);
        model.addAttribute("isAdmin", isAdmin);

        return "transactions/list";
    }

    @GetMapping("/{id}")
    public String transactionDetailPage(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        List<Category> categories = categoryService.getAllCategoriesByUserId(userDetails.getId());
        Transaction transaction = transactionService.getTransactionById(id);

        model.addAttribute("transaction", transaction);
        model.addAttribute("categories", categories);

        return "transactions/detail";
    }
}