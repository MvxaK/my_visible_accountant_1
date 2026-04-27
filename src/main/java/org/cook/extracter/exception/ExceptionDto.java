package org.cook.extracter.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ExceptionDto {

    private String message;
    private String details;
    private LocalDateTime timestamp;

}
