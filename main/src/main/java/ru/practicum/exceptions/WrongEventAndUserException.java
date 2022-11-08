package ru.practicum.exceptions;

import lombok.Getter;


@Getter
public class WrongEventAndUserException extends RuntimeException {

    private String massage;
    private long id;

    public WrongEventAndUserException(String massage, Long userId, Long eventId) {
    }
}
