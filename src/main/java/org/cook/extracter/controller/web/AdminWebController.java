package org.cook.extracter.controller.web;

import lombok.RequiredArgsConstructor;
import org.cook.extracter.model.Transaction;
import org.cook.extracter.model.User;
import org.cook.extracter.service.interfaces.DocumentService;
import org.cook.extracter.service.interfaces.TransactionService;
import org.cook.extracter.service.interfaces.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminWebController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final DocumentService documentService;

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("userCount", userService.getAllUsers().size());
        model.addAttribute("transactionCount", transactionService.getAllTransactions().size());
        model.addAttribute("documentCount", documentService.getAllDocuments().size());

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String adminUsersPage(Model model) {
        model.addAttribute("users", userService.getAllUsers());

        return "admin/users";
    }

    @GetMapping("/users/{id}/profile")
    public String viewUserProfile(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);

        List<Transaction> transactions = transactionService.getTransactionsByUserId(id);

        List<Transaction> recent = transactions.stream()
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .limit(5)
                .toList();

        model.addAttribute("user", user);

        model.addAttribute("transactionCount", transactionService.getTransactionsByUserId(id).size());
        model.addAttribute("recentTransactions", recent);

        model.addAttribute("isAdminView", true);

        return "profile";
    }
}