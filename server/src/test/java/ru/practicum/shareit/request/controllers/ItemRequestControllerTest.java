package ru.practicum.shareit.request.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestReturnDto;
import ru.practicum.shareit.request.exceptions.NotFoundItemRequestException;
import ru.practicum.shareit.request.services.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void createItemRequest_whenInvoked_thenResponseStatusIsOkWithItemRequestInBody() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("description1")
                .build();

        when(itemRequestService.createItemRequest(any(), anyInt()))
                .thenAnswer(invocationOnMock -> ItemRequestReturnDto.builder()
                        .id(1)
                        .description(invocationOnMock.getArgument(0, ItemRequestDto.class).getDescription())
                        .build());

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));

        verify(itemRequestService, times(1))
                .createItemRequest(any(), anyInt());
    }

    @Test
    void findRequests_whenInvoked_thenResponseStatusIsOkWithItemRequestCollectionInBody() throws Exception {
        when(itemRequestService.findRequests(anyInt()))
                .thenReturn(List.of(
                        ItemRequestReturnDto.builder()
                                .id(1)
                                .build(),
                        ItemRequestReturnDto.builder()
                                .id(2)
                                .build(),
                        ItemRequestReturnDto.builder()
                                .id(3)
                                .build()));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[*].id", hasItem(is(1))))
                .andExpect(jsonPath("$.[*].id", hasItem(is(2))))
                .andExpect(jsonPath("$.[*].id", hasItem(is(3))));

        verify(itemRequestService, times(1))
                .findRequests(anyInt());
    }

    @Test
    void findAllRequests_whenInvoked_thenResponseStatusIsOkWithItemRequestCollectionInBody() throws Exception {
        when(itemRequestService.findAllRequests(1, null, null))
                .thenReturn(List.of(
                        ItemRequestReturnDto.builder()
                                .id(1)
                                .build(),
                        ItemRequestReturnDto.builder()
                                .id(2)
                                .build(),
                        ItemRequestReturnDto.builder()
                                .id(3)
                                .build()));

        when(itemRequestService.findAllRequests(1, 0, 2))
                .thenReturn(List.of(
                        ItemRequestReturnDto.builder()
                                .id(1)
                                .build(),
                        ItemRequestReturnDto.builder()
                                .id(2)
                                .build()));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[*].id", hasItem(is(1))))
                .andExpect(jsonPath("$.[*].id", hasItem(is(2))))
                .andExpect(jsonPath("$.[*].id", hasItem(is(3))));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[*].id", hasItem(is(1))))
                .andExpect(jsonPath("$.[*].id", hasItem(is(2))));

        verify(itemRequestService, times(1))
                .findAllRequests(1, null, null);
        verify(itemRequestService, times(1))
                .findAllRequests(1, 0, 2);
    }

    @Test
    void findRequestById_whenFound_thenResponseStatusIsOkWithItemRequestInBody() throws Exception {
        when(itemRequestService.findRequestById(anyInt(), anyInt()))
                .thenAnswer(invocationOnMock -> ItemRequestReturnDto.builder()
                        .id(invocationOnMock.getArgument(0, Integer.class))
                        .build());

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(itemRequestService, times(1))
                .findRequestById(anyInt(), anyInt());
    }

    @Test
    void findRequestById_whenNotFound_thenThrowNotFoundItemRequestException() throws Exception {
        when(itemRequestService.findRequestById(anyInt(), anyInt()))
                .thenThrow(new NotFoundItemRequestException(""));

        mvc.perform(get("/requests/99")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1))
                .findRequestById(anyInt(), anyInt());
    }
}