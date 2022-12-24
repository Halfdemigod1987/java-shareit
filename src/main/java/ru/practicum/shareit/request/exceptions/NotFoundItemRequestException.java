package ru.practicum.shareit.request.exceptions;

public class NotFoundItemRequestException extends RuntimeException {
    public NotFoundItemRequestException(String message) {
        super(message);
    }
}
