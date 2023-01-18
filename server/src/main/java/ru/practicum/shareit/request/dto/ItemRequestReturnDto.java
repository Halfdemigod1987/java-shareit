package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.item.dto.ItemReturnDto;

import java.time.LocalDateTime;
import java.util.Set;

@Value
@Builder
@AllArgsConstructor
@Jacksonized
public class ItemRequestReturnDto {
    int id;
    String description;
    LocalDateTime created;
    Set<ItemReturnDto> items;
}
