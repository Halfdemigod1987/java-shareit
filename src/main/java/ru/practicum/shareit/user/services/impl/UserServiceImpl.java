package ru.practicum.shareit.user.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mappers.UserMapper;
import ru.practicum.shareit.user.exceptions.DuplicateEmailException;
import ru.practicum.shareit.user.exceptions.NotFoundUserException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.services.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(int id) {
        return userMapper.userToUserDto(
                userRepository.findById(id)
                        .orElseThrow(() -> new NotFoundUserException(String.format("User with id = %d not found", id))));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        validateUser(user);
        return userMapper.userToUserDto(
                userRepository.create(user));
    }

    @Override
    public UserDto partialUpdate(int id, Map<String, String> updates) {
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
        return userMapper.userToUserDto(
                userRepository.update(id, user));
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
