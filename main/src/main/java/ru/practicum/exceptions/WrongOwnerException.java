package ru.practicum.exceptions;

import lombok.Getter;

@Getter
public class WrongOwnerException extends RuntimeException {

    private String massage;
    private long id;

    public WrongOwnerException(String massage, Long userId, Long commentId) {
    }
}
