package org.cook.extracter.service.impl;

import org.cook.extracter.entity.TransactionEntity;
import org.cook.extracter.model.Currency;
import org.cook.extracter.model.Transaction;
import org.cook.extracter.service.interfaces.CurrencyConverterService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CurrencyConverterServiceImpl implements CurrencyConverterService {

    @Override
    public BigDecimal convert(Transaction transaction) {
        BigDecimal amount = transaction.getAmount();

        if(transaction.getCurrency().equals(Currency.RUB))
            amount = amount.multiply(BigDecimal.valueOf(6));
        else if (transaction.getCurrency().equals(Currency.USD)) {
            amount = amount.multiply(BigDecimal.valueOf(478));
        }else if(transaction.getCurrency().equals(Currency.EUR)){
            amount = amount.multiply(BigDecimal.valueOf(559));
        }

        return amount;
    }

    @Override
    public BigDecimal convert(TransactionEntity transaction) {
        BigDecimal amount = transaction.getAmount();

        if(transaction.getCurrency().equals(Currency.RUB))
            amount = amount.multiply(BigDecimal.valueOf(6));
        else if (transaction.getCurrency().equals(Currency.USD)) {
            amount = amount.multiply(BigDecimal.valueOf(478));
        }else if(transaction.getCurrency().equals(Currency.EUR)){
            amount = amount.multiply(BigDecimal.valueOf(559));
        }

        return amount;
    }
}
