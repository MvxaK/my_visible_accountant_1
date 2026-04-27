package org.cook.extracter.service.interfaces;

import org.cook.extracter.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransactionService {

    Transaction getTransactionById(Long id);
    List<Transaction> getTransactionsByUserId(Long userId);
    List<Transaction> getAllTransactions();
    Transaction createTransaction(Transaction transactionToCreate, Long requestingUserId);
    Transaction updateTransaction(Long id, Transaction transactionToUpdate, Long requestingUserId, boolean isAdmin);
    void deleteTransaction(Long id, Long requestingUserId, boolean isAdmin);

}
