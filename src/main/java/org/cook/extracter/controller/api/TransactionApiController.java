package org.cook.extracter.controller.api;

import lombok.RequiredArgsConstructor;
import org.cook.extracter.model.Transaction;
import org.cook.extracter.security.details.CustomUserDetails;
import org.cook.extracter.service.interfaces.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionApiController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();

        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Transaction>> getMyTransactions(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(transactionService.getTransactionsByUserId(userDetails.getId()));
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Transaction createdTransaction = transactionService.createTransaction(transaction, userDetails.getId());

        return ResponseEntity.ok(createdTransaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody Transaction transaction, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Transaction updatedTransaction = transactionService.updateTransaction(id, transaction, userDetails.getId(), isAdmin);

        return ResponseEntity.ok(updatedTransaction);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        transactionService.deleteTransaction(id, userDetails.getId(), isAdmin);

        return ResponseEntity.noContent().build();
    }
}