package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@AllArgsConstructor
@Jacksonized
public class UserDto {
    private int id;
    private String name;
    private String email;
}
