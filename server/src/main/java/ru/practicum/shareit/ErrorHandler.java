package ru.practicum.shareit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exceptions.AccessDeniedException;
import ru.practicum.shareit.booking.exceptions.NotFoundBookingException;
import ru.practicum.shareit.booking.exceptions.UnsupportedBookingStatusException;
import ru.practicum.shareit.item.exceptions.NotFoundItemException;
import ru.practicum.shareit.item.exceptions.NotOwnerException;
import ru.practicum.shareit.request.exceptions.NotFoundItemRequestException;
import ru.practicum.shareit.user.dto.ErrorResponse;
import ru.practicum.shareit.user.exceptions.NotFoundUserException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNotFoundItemException(final NotFoundItemException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(String.format("Validation error: %s", e.getMessage())));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNotOwnerException(final NotOwnerException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(String.format("Validation error: %s", e.getMessage())));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNotFoundUseException(final NotFoundUserException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(String.format("Validation error: %s", e.getMessage())));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNotFoundBookingException(final NotFoundBookingException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(String.format("Validation error: %s", e.getMessage())));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(final AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(String.format("Access error: %s", e.getMessage())));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(final IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(String.format("Validation error: %s", e.getMessage())));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUnsupportedBookingStatusException(final UnsupportedBookingStatusException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNotFoundItemRequestException(final NotFoundItemRequestException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }


}
