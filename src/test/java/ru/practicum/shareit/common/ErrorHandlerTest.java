package ru.practicum.shareit.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.exception.BadParameterException;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @BeforeEach
    void beforeEach() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void shouldCallHandleEmailExistExc() {
        String message = "Пользователь с такой почтой уже зарегистрирован";
        AlreadyExistException exc = new AlreadyExistException("Пользователь с такой почтой уже зарегистрирован");

        ErrorResponse errorResponse = errorHandler.handleAlreadyExistException(exc);

        assertEquals(message, errorResponse.getError());
    }

    @Test
    void shouldCallHandleBadParameterExc() {
        String message = "Ошибка";
        BadParameterException exc = new BadParameterException(message);

        ErrorResponse errorResponse = errorHandler.handleItemNotAvailableExc(exc);

        assertEquals(message, errorResponse.getError());
    }

    @Test
    void shouldCallHandleNotFoundExc() {
        String message = "Такого элемента не зарегистрировано";
        NotFoundException exc = new NotFoundException(message);

        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exc);

        assertEquals(message, errorResponse.getError());
    }

    @Test
    void shouldCallHandleOtherExc() {
        String message = "Ошибка сервера";
        Throwable exc = new Throwable(message);

        ErrorResponse errorResponse = errorHandler.handleOtherExc(exc);

        assertNotNull(errorResponse.getError());
    }

    @Test
    void shouldCallHandleConstraintViolationExc() {
        ConstraintViolationException exc = new ConstraintViolationException(null);

        ErrorResponse errorResponse = errorHandler.handleOtherExc(exc);

        assertNotNull(errorResponse.getError());
    }
}
