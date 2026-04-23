

package com.wasel.backend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(404).body(
                Map.of(
                        "status", 404,
                        "error", "Not Found",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(400).body(
                Map.of(
                        "status", 400,
                        "error", "Bad Request",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception ex) {
        return ResponseEntity.status(500).body(
                Map.of(
                        "status", 500,
                        "error", "Internal Server Error",
                        "message", ex.getMessage()
                )
        );
    }
}