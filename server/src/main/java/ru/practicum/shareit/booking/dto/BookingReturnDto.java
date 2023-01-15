package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemReturnDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Value
@Builder
@AllArgsConstructor
@Jacksonized
public class BookingReturnDto {
    int id;
    LocalDateTime start;
    LocalDateTime end;
    ItemReturnDto item;
    UserDto booker;
    BookingStatus status;
}
