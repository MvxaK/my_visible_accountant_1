package org.cook.extracter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    private Long id;
    private Long userId;
    private String name;
    private String description;
    private TransactionType type;

}
