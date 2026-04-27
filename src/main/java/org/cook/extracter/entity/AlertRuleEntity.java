package org.cook.extracter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cook.extracter.model.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts_rules")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertRuleEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @OneToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private CategoryEntity category;

    private BigDecimal thresholdAmount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private Boolean isActive;
    private LocalDateTime createdAt;

}