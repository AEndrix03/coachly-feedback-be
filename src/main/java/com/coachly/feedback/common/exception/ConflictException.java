package com.coachly.feedback.common.exception;

public class ConflictException extends AppException {
    public ConflictException(String code, String message) {
        super(code, message);
    }
}