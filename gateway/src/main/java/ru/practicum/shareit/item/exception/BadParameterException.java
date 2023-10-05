package ru.practicum.shareit.item.exception;

public class BadParameterException extends RuntimeException {
    public BadParameterException(String message) {
        super(message);
    }
}