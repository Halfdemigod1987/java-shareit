package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
@AllArgsConstructor
@Jacksonized
public class UserDto {
    int id;
    String name;
    @Email
    @NotBlank String email;
}
