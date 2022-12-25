package ru.practicum.shareit.user.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.NotFoundUserException;
import ru.practicum.shareit.user.services.UserService;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void findAllUsers_whenInvoked_thenResponseStatusIsOkWithUserCollectionInBody() throws Exception {
        when(userService.findAllUsers())
                .thenReturn(List.of(
                        UserDto.builder()
                                .id(1)
                                .build(),
                        UserDto.builder()
                                .id(2)
                                .build(),
                        UserDto.builder()
                                .id(3)
                                .build()));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[*].id", hasItem(is(1))))
                .andExpect(jsonPath("$.[*].id", hasItem(is(2))))
                .andExpect(jsonPath("$.[*].id", hasItem(is(3))));

        verify(userService, times(1))
                .findAllUsers();
    }

    @Test
    void findUserById_whenFound_thenResponseStatusIsOkWithUserInBody() throws Exception {
        when(userService.findUserById(anyInt()))
                .thenAnswer(invocationOnMock -> UserDto.builder()
                        .id(invocationOnMock.getArgument(0, Integer.class))
                        .build());

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(userService, times(1))
                .findUserById(anyInt());
    }

    @Test
    void findUserById_whenNotFound_thenThrowNotFoundUserException() throws Exception {
        when(userService.findUserById(anyInt()))
                .thenThrow(new NotFoundUserException(""));

        mvc.perform(get("/users/99")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());

        verify(userService, times(1))
                .findUserById(anyInt());
    }

    @Test
    void createUser_whenInvoked_thenResponseStatusIsCreatedWithUserInBody() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("name1")
                .email("email1@test.com")
                .build();

        when(userService.createUser(any()))
                .thenAnswer(invocationOnMock -> UserDto.builder()
                        .id(1)
                        .name(invocationOnMock.getArgument(0, UserDto.class).getName())
                        .email(invocationOnMock.getArgument(0, UserDto.class).getEmail())
                        .build());

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1))
                .createUser(any());
    }

    @Test
    void updateUser_whenInvoked_thenResponseStatusIsOkWithUserInBody() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("name_updated")
                .build();

        when(userService.partialUpdate(anyInt(), anyMap()))
                .thenAnswer(invocationOnMock -> UserDto.builder()
                        .id(invocationOnMock.getArgument(0, Integer.class))
                        .name((String) invocationOnMock.getArgument(1, HashMap.class).get("name"))
                        .build());

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(userDto.getName())));

        verify(userService, times(1))
                .partialUpdate(anyInt(), anyMap());
    }

    @Test
    void deleteUser_whenInvoked_thenResponseStatusIsOk() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1))
                .deleteUser(anyInt());
    }
}