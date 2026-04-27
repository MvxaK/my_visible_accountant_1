package org.cook.extracter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertRule {

    private Long id;
    private Long userId;
    private Long categoryId;
    private BigDecimal thresholdAmount;
    private Currency currency;
    private Boolean isActive;
    private LocalDateTime createdAt;

}
