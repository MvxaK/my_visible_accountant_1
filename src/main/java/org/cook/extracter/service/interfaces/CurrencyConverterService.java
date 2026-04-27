package org.cook.extracter.service.interfaces;

import org.cook.extracter.entity.TransactionEntity;
import org.cook.extracter.model.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface CurrencyConverterService {

    BigDecimal convert(Transaction transaction);
    BigDecimal convert(TransactionEntity transaction);

}
