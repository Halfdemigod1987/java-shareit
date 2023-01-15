package ru.practicum.shareit.booking.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReturnDto;
import ru.practicum.shareit.booking.services.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingReturnDto> createBooking(
            @RequestBody BookingDto booking,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingService.createBooking(booking, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingReturnDto> changeBookingStatus(
            @PathVariable int bookingId,
            @RequestParam(value = "approved") boolean approved,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return ResponseEntity.ok(bookingService.changeBookingStatus(bookingId, approved, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingReturnDto>> findAllBookings(
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return ResponseEntity.ok(bookingService.findAllBookings(state, userId, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingReturnDto>> findAllOwnerBookings(
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return ResponseEntity.ok(bookingService.findAllOwnerBookings(state, userId, from, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingReturnDto> findBookingById(
            @PathVariable int id,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return ResponseEntity.ok(bookingService.findBookingById(id, userId));
    }

}
