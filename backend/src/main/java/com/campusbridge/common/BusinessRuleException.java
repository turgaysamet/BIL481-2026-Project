package com.campusbridge.common;

/**
 * Thrown when a request violates a business rule of the requirements document
 * (e.g. joining a full event, more than 5 active invitation codes). Mapped to HTTP 409/422.
 */
public class BusinessRuleException extends RuntimeException {

    private final String code;

    public BusinessRuleException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
