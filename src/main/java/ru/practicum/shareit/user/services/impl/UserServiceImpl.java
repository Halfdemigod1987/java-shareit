package ru.practicum.shareit.user.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.exceptions.DuplicateEmailException;
import ru.practicum.shareit.user.exceptions.NotFoundUserException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.services.UserService;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundUserException(String.format("User with id = %d not found", id)));
    }

    @Override
    public User createUser(User user) {
        validateUser(user);
        return userRepository.create(user);
    }

    @Override
    public User partialUpdate(int id, Map<String, String> updates) {
        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundUserException(String.format("User with id = %d not found", id)));
        User user = new User();
        user.setId(oldUser.getId());
        user.setName(oldUser.getName());
        user.setEmail(oldUser.getEmail());

        updates.forEach((key, value) -> {
            if (key.equalsIgnoreCase("name")) {
                user.setName(value);
            }
            if (key.equalsIgnoreCase("email")) {
                user.setEmail(value);
            }
        });

        validateUser(user);
        return userRepository.update(id, user);
    }

    private void validateUser(User user) {
        boolean duplicateEmail = userRepository.findAll().stream()
                .anyMatch(otherUser -> (!otherUser.getEmail().isBlank()
                        && otherUser.getId() != user.getId())
                        && (otherUser.getEmail().equals(user.getEmail())));
        if (duplicateEmail) {
            throw new DuplicateEmailException(String.format("Email %s is already used", user.getEmail()));
        }
    }

    @Override
    public void deleteUser(int id) {
        userRepository.delete(id);
    }
}
