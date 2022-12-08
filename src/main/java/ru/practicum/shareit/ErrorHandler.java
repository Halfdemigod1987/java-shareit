package ru.practicum.shareit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.exceptions.NotFoundItemException;
import ru.practicum.shareit.item.exceptions.NotOwnerException;
import ru.practicum.shareit.user.dto.ErrorResponse;
import ru.practicum.shareit.user.exceptions.DuplicateEmailException;
import ru.practicum.shareit.user.exceptions.NotFoundUserException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNotFoundException(final NotFoundItemException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(String.format("Validation error: %s", e.getMessage())));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNotOwnerException(final NotOwnerException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(String.format("Validation error: %s", e.getMessage())));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleDuplicateEmailException(final DuplicateEmailException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(String.format("Validation error: %s", e.getMessage())));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNotFoundException(final NotFoundUserException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(String.format("Validation error: %s", e.getMessage())));
    }

}
