package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Value
@Builder
@AllArgsConstructor
@Jacksonized
public class BookingDto {
    @FutureOrPresent LocalDateTime start;
    @FutureOrPresent LocalDateTime end;
    int itemId;
}
