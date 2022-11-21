package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@AllArgsConstructor
@Jacksonized
public class ItemDto {
    int id;
    @NotBlank String name;
    @NotBlank String description;
    @NotNull Boolean available;
}
