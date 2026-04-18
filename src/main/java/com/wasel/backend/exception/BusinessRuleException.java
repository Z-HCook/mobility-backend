package com.wasel.backend.exception;

public class BusinessRuleException extends ApiException {
    public BusinessRuleException(String message) {
        super(message);
    }
}