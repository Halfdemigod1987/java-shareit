package ru.practicum.shareit.user.services;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<User> findAllUsers();

    User findUserById(int id);

    User createUser(User userDtoToUser);

    void deleteUser(int id);

    User partialUpdate(int id, Map<String, String> updates);
}
