package ru.practicum.shareit.user.services.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.services.UserService;

import javax.transaction.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllUsers_whenInvoked_thenReturnUserCollection() {
        User user1 = userRepository.save(User.builder()
                .name("email1@test.com")
                .email("name1")
                .build());
        User user2 = userRepository.save(User.builder()
                .name("email2@test.com")
                .email("name2")
                .build());
        User user3 = userRepository.save(User.builder()
                .name("email3@test.com")
                .email("name3")
                .build());

        List<UserDto> users = userService.findAllUsers();
        assertThat(users, hasSize(3));
        assertThat(users, hasItem(hasProperty("id", is(user1.getId()))));
        assertThat(users, hasItem(hasProperty("id", is(user2.getId()))));
        assertThat(users, hasItem(hasProperty("id", is(user3.getId()))));
    }
}