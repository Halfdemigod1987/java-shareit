package ru.practicum.shareit.user.services;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<UserDto> findAllUsers();

    UserDto findUserById(int id);

    UserDto createUser(UserDto userDto);

    void deleteUser(int id);

    UserDto partialUpdate(int id, Map<String, String> updates);
}
