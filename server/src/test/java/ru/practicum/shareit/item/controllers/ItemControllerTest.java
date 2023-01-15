package ru.practicum.shareit.item.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentReturnDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemReturnDto;
import ru.practicum.shareit.item.exceptions.NotFoundItemException;
import ru.practicum.shareit.item.services.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void findAllItems_whenInvoked_thenResponseStatusIsOkWithItemCollectionInBody() throws Exception {
        when(itemService.findAllItems(1, null, null))
                .thenReturn(List.of(
                        ItemReturnDto.builder()
                                .id(1)
                                .build(),
                        ItemReturnDto.builder()
                                .id(2)
                                .build(),
                        ItemReturnDto.builder()
                                .id(3)
                                .build()));

        when(itemService.findAllItems(1, 0, 2))
                .thenReturn(List.of(
                        ItemReturnDto.builder()
                                .id(1)
                                .build(),
                        ItemReturnDto.builder()
                                .id(2)
                                .build()));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[*].id", hasItem(is(1))))
                .andExpect(jsonPath("$.[*].id", hasItem(is(2))))
                .andExpect(jsonPath("$.[*].id", hasItem(is(3))));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[*].id", hasItem(is(1))))
                .andExpect(jsonPath("$.[*].id", hasItem(is(2))));

        verify(itemService, times(1))
                .findAllItems(1, null, null);
        verify(itemService, times(1))
                .findAllItems(1, 0, 2);
    }

    @Test
    void findItemById_whenFound_thenResponseStatusIsOkWithItemInBody() throws Exception {
        when(itemService.findItemById(1, 1))
                .thenReturn(ItemReturnDto.builder()
                                .id(1)
                                .name("name1")
                                .description("description1")
                                .ownerId(1)
                                .build());

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name1")))
                .andExpect(jsonPath("$.description", is("description1")))
                .andExpect(jsonPath("$.ownerId", is(1)));

        verify(itemService, times(1))
                .findItemById(1, 1);
    }

    @Test
    void findItemById_whenNotFound_thenThrowNotFoundItemException() throws Exception {
        when(itemService.findItemById(anyInt(), anyInt()))
                .thenThrow(new NotFoundItemException(""));

        mvc.perform(get("/items/99")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());

        verify(itemService, times(1))
                .findItemById(anyInt(), anyInt());
    }

    @Test
    void createItem_whenInvoked_thenResponseStatusIsCreatedWithItemInBody() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .name("name1")
                .description("description1")
                .available(true)
                .build();

        when(itemService.createItem(any(), anyInt()))
                .thenAnswer(invocationOnMock -> ItemReturnDto.builder()
                        .id(1)
                        .name(invocationOnMock.getArgument(0, ItemDto.class).getName())
                        .description(invocationOnMock.getArgument(0, ItemDto.class).getDescription())
                        .available(invocationOnMock.getArgument(0, ItemDto.class).getAvailable())
                        .ownerId(invocationOnMock.getArgument(1, Integer.class))
                        .build());

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(1)));

        verify(itemService, times(1))
                .createItem(any(), anyInt());
    }

    @Test
    void updateItem_whenInvoked_thenResponseStatusIsOkWithItemInBody() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .name("name_updated")
                .build();

        when(itemService.partialUpdate(anyInt(), anyMap(), anyInt()))
                .thenAnswer(invocationOnMock -> ItemReturnDto.builder()
                        .id(invocationOnMock.getArgument(0, Integer.class))
                        .name((String) invocationOnMock.getArgument(1, HashMap.class).get("name"))
                        .build());

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(itemDto.getName())));

        verify(itemService, times(1))
                .partialUpdate(anyInt(), anyMap(), anyInt());

    }

    @Test
    void deleteItem_whenInvoked_thenResponseStatusIsOk() throws Exception {

        mvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());

        verify(itemService, times(1))
                .deleteItem(anyInt());
    }

    @Test
    void searchItems_whenInvoked_thenResponseStatusIsOkWithItemCollectionInBody() throws Exception {

        when(itemService.searchItems("name", 1, 0, 2))
                .thenReturn(List.of(
                        ItemReturnDto.builder()
                                .id(1)
                                .build(),
                        ItemReturnDto.builder()
                                .id(2)
                                .build()));

        when(itemService.searchItems("name1", 1, null, null))
                .thenReturn(List.of(
                        ItemReturnDto.builder()
                                .id(1)
                                .build()));

        when(itemService.searchItems("noname1", 1, null, null))
                .thenReturn(List.of());

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", "1")
                        .param("text", "name1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[*].id", hasItem(is(1))));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", "1")
                        .param("text", "name")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[*].id", hasItem(is(1))))
                .andExpect(jsonPath("$.[*].id", hasItem(is(2))));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", "1")
                        .param("text", "noname1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));

        verify(itemService, times(1))
                .searchItems("name1", 1, null, null);
        verify(itemService, times(1))
                .searchItems("name", 1, 0, 2);
        verify(itemService, times(1))
                .searchItems("noname1", 1, null, null);
    }

    @Test
    void createComment_whenInvoked_thenResponseStatusIsOkWithCommentInBody() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .text("text1")
                .build();

        when(itemService.createComment(anyInt(), any(), anyInt()))
                .thenAnswer(invocationOnMock -> CommentReturnDto.builder()
                        .id(1)
                        .text(invocationOnMock.getArgument(1, CommentDto.class).getText())
                        .build());

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));

        verify(itemService, times(1))
                .createComment(anyInt(), any(), anyInt());
    }
}