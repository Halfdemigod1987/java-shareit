package ru.practicum.shareit.user.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.services.UserService;

import javax.transaction.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void initiate() {
        when(userRepository.findById(1))
                .thenReturn(Optional.ofNullable(User.builder()
                        .id(1)
                        .build()));
    }

    @Test
    void findUserById_whenInvoked_thenReturnUser() {
        UserDto userDto = userService.findUserById(1);
        assertThat(userDto, hasProperty("id", is(1)));

        verify(userRepository, times(1))
                .findById(1);
    }

    @Test
    void createUser_whenInvoked_thanReturnUser() {
        UserDto userDto = UserDto.builder()
                .name("name1")
                .email("email1")
                .build();

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocationOnMock -> User.builder()
                        .id(1)
                        .name(invocationOnMock.getArgument(0, User.class).getName())
                        .email(invocationOnMock.getArgument(0, User.class).getEmail())
                        .build());

        UserDto result = userService.createUser(userDto);

        assertThat(result, hasProperty("id", is(1)));
        assertThat(result, hasProperty("name", is(userDto.getName())));
        assertThat(result, hasProperty("email", is(userDto.getEmail())));

        verify(userRepository, times(1))
                .save(any(User.class));
    }

    @Test
    void partialUpdate_whenInvoked_thanReturnUserWithUpdate() {
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocationOnMock -> User.builder()
                        .id(1)
                        .name(invocationOnMock.getArgument(0, User.class).getName())
                        .email(invocationOnMock.getArgument(0, User.class).getEmail())
                        .build());

        Map<String, String> update = new HashMap<>();
        update.put("name", "nameUpdated");
        update.put("email", "emailUpdated");

        UserDto userDto = userService.partialUpdate(1, update);

        assertThat(userDto, hasProperty("name", is(update.get("name"))));
        assertThat(userDto, hasProperty("email", is(update.get("email"))));

        verify(userRepository, times(1))
                .save(any(User.class));
    }

    @Test
    void deleteUser_whenInvoked_thenNotThrowErrors() {
        userService.deleteUser(1);
    }
}