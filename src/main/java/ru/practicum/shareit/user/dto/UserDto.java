package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
@Builder
@AllArgsConstructor
@Jacksonized
public class UserDto {
    private int id;
    private String name;
    @Email
    @NotBlank
    private String email;
}
