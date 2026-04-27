package org.cook.extracter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    private Long id;
    private Long documentId;
    private Long userId;
    private Long categoryId;
    private BigDecimal amount;
    private Currency currency;
    private LocalDateTime createdAt;

}
