package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Value
@AllArgsConstructor
@Jacksonized
public class BookingDto {
    @FutureOrPresent
    private LocalDateTime start;
    @FutureOrPresent
    private LocalDateTime end;
    private int itemId;
}
