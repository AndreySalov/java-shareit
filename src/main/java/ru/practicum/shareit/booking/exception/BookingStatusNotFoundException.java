package ru.practicum.shareit.booking.exception;

import ru.practicum.shareit.exception.NotFoundException;

public class BookingStatusNotFoundException extends NotFoundException {
    public BookingStatusNotFoundException() {
        super("Некорректный статус бронирования");
    }
}
