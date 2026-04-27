package org.cook.extracter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Document {

    private Long id;
    private Long userId;
    private String name;
    private String filePath;
    private String documentType;
    private LocalDateTime createdAt;

}
