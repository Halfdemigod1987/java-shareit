package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@AllArgsConstructor
@Jacksonized
public class BookingItemReturnDto {
    int id;
    int bookerId;
}
