package com.coachly.feedback.common.exception;

public class ForbiddenException extends AppException {
    public ForbiddenException(String code, String message) {
        super(code, message);
    }
}