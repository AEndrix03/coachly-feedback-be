package it.aredegalli.coachly.feedback.exception;

public class BadRequestException extends AppException {
    public BadRequestException(String code, String message) {
        super(code, message);
    }
}


