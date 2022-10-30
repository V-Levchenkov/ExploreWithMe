package ru.practicum.exceptions;

public class ValidationException extends RuntimeException {

    private String message;
    private Long id;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Long id) {
        super(message);
    }
}
