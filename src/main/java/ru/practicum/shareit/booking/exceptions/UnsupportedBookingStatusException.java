package ru.practicum.shareit.booking.exceptions;

public class UnsupportedBookingStatusException extends RuntimeException {
    public UnsupportedBookingStatusException(String message) {
        super(message);
    }
}
