package com.coachly.feedback.common.exception;

public class BadRequestException extends AppException {
    public BadRequestException(String code, String message) {
        super(code, message);
    }
}