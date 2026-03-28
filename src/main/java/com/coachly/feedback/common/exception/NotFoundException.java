package com.coachly.feedback.common.exception;

public class NotFoundException extends AppException {
    public NotFoundException(String code, String message) {
        super(code, message);
    }
}