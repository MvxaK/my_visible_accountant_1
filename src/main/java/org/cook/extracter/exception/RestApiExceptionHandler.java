package org.cook.extracter.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "org.cook.extracter.controller.api")
public class RestApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e){
        ExceptionDto exception = new ExceptionDto(e.getMessage(), "Bad request", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(exception);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException e){
        ExceptionDto exception = new ExceptionDto(e.getMessage(), "Can not perform this action", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(exception);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFound(EntityNotFoundException e){
        ExceptionDto exception = new ExceptionDto(e.getMessage(), "Entity not found", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(exception);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<?> handleEntityAccessDenied(AuthorizationDeniedException e){
        ExceptionDto exception = new ExceptionDto(e.getMessage(), "You don`t have rights to perform this action", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(exception);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleInternal(Exception e){
        ExceptionDto exception = new ExceptionDto(e.getMessage(), "Internal Server error", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exception);
    }
}
