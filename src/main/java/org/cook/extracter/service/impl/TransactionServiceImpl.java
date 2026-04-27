package org.cook.extracter.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.cook.extracter.entity.CategoryEntity;
import org.cook.extracter.entity.DocumentEntity;
import org.cook.extracter.entity.TransactionEntity;
import org.cook.extracter.entity.UserEntity;
import org.cook.extracter.mapper.TransactionMapper;
import org.cook.extracter.model.Category;
import org.cook.extracter.model.Transaction;
import org.cook.extracter.repository.CategoryRepository;
import org.cook.extracter.repository.DocumentRepository;
import org.cook.extracter.repository.TransactionRepository;
import org.cook.extracter.repository.UserRepository;
import org.cook.extracter.service.EmailService;
import org.cook.extracter.service.interfaces.AlertRuleService;
import org.cook.extracter.service.interfaces.CategoryService;
import org.cook.extracter.service.interfaces.CurrencyConverterService;
import org.cook.extracter.service.interfaces.TransactionService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AlertRuleService alertRuleService;
    private final CategoryService categoryService;
    private final EmailService emailService;
    private final CurrencyConverterService currencyConverter;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final DocumentRepository documentRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toModel)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id -> " + id));
    }

    @Override
    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findAllByUserId(userId)
                .stream()
                .map(transactionMapper::toModel)
                .toList();
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(transactionMapper::toModel)
                .toList();
    }

    @Override
    @Transactional
    public Transaction createTransaction(Transaction transactionToCreate, Long requestingUserId) {
        UserEntity userEntity = userRepository.findById(requestingUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id -> " + requestingUserId));

        DocumentEntity documentEntity = documentRepository.findById(transactionToCreate.getDocumentId())
                .orElseThrow(() -> new EntityNotFoundException("Document not found with id -> " + transactionToCreate.getDocumentId()));

        if (!documentEntity.getUser().getId().equals(requestingUserId))
            throw new AccessDeniedException("Document does not belong to user with id -> " + requestingUserId);

        CategoryEntity categoryEntity = categoryRepository.findById(transactionToCreate.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id -> " + transactionToCreate.getCategoryId()));

        TransactionEntity transactionEntity = transactionMapper.toEntity(transactionToCreate);
        transactionEntity.setDocument(documentEntity);
        transactionEntity.setUser(userEntity);
        transactionEntity.setCategory(categoryEntity);
        transactionEntity.setCreatedAt(LocalDateTime.now());

        Transaction transaction = transactionMapper.toModel(transactionRepository.save(transactionEntity));

        checkAndNotify(transaction, userEntity);

        return transaction;
    }

    private void checkAndNotify(Transaction transaction, UserEntity userEntity) {
        alertRuleService.findByUserIdAndCategoryId(userEntity.getId(), transaction.getCategoryId()).ifPresent(rule -> {
            if (!rule.getIsActive())
                return;

            BigDecimal monthlySpending = calculateMonthlySpending(userEntity.getId(), transaction.getCategoryId());

            if (monthlySpending.compareTo(rule.getThresholdAmount()) > 0) {
                Category category = categoryService.getCategoryById(transaction.getCategoryId());
                emailService.sendLimitExceededAlert(
                        userEntity.getEmail(),
                        category.getName(),
                        monthlySpending,
                        rule.getThresholdAmount()
                );
            }
        });
    }

    private BigDecimal calculateMonthlySpending(Long userId, Long categoryId) {
        List<Transaction> transactions = getTransactionsByUserId(userId);

        return transactions.stream()
                .filter(t -> t.getCategoryId().equals(categoryId))
                .filter(t -> t.getCreatedAt().getMonth() == LocalDateTime.now().getMonth())
                .map(currencyConverter::convert)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional
    public Transaction updateTransaction(Long id, Transaction transactionToUpdate, Long requestingUserId, boolean isAdmin) {
        TransactionEntity transactionEntity = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id -> " + id));

        if (!isAdmin && !transactionEntity.getUser().getId().equals(requestingUserId))
            throw new AccessDeniedException("You don't have permission to update this transaction");

        if (transactionToUpdate.getCategoryId() != null) {
            CategoryEntity categoryEntity = categoryRepository.findById(transactionToUpdate.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id -> " + transactionToUpdate.getCategoryId()));
            transactionEntity.setCategory(categoryEntity);
        }

        transactionEntity.setAmount(transactionToUpdate.getAmount());
        transactionEntity.setCurrency(transactionToUpdate.getCurrency());

        return transactionMapper.toModel(transactionRepository.save(transactionEntity));
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id, Long requestingUserId, boolean isAdmin) {
        TransactionEntity transactionEntity = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id -> " + id));

        if (!isAdmin && !transactionEntity.getUser().getId().equals(requestingUserId))
            throw new AccessDeniedException("You don't have permission to delete this transaction");

        transactionRepository.deleteById(id);
    }
}