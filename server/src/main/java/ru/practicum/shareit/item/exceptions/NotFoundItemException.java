package ru.practicum.shareit.item.exceptions;

public class NotFoundItemException extends RuntimeException {
    public NotFoundItemException(String message) {
        super(message);
    }
}
