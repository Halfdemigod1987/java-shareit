package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Value
@Builder
@AllArgsConstructor
@Jacksonized
public class BookingDto {
    LocalDateTime start;
    LocalDateTime end;
    int itemId;
}
