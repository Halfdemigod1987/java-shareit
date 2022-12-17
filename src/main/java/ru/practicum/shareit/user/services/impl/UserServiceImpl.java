package ru.practicum.shareit.user.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mappers.UserMapper;
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
                findUser(id));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        return userMapper.userToUserDto(
                userRepository.save(user));
    }

    @Override
    public UserDto partialUpdate(int id, Map<String, String> updates) {
        User oldUser = findUser(id);
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

        return userMapper.userToUserDto(
                userRepository.save(user));
    }

    @Override
    public void deleteUser(int id) {
        User user = findUser(id);
        userRepository.delete(user);
    }

    private User findUser(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundUserException(String.format("User with id = %d not found", id)));
    }

}
