package ru.practicum.shareit.booking.exceptions;

public class NotFoundBookingException extends RuntimeException {
    public NotFoundBookingException(String message) {
        super(message);
    }
}
