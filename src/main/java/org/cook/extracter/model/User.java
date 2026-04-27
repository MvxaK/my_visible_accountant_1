package org.cook.extracter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;
    private String email;
    private LocalDateTime createdAt;
    private Role role;

}
