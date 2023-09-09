package ru.practicum.shareit.user.exception;

import ru.practicum.shareit.util.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(Long userId) {
        super("Пользователь с идентификатором " + userId + " не найден.");
    }
}

