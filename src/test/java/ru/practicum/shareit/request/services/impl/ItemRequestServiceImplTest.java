package ru.practicum.shareit.request.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestReturnDto;
import ru.practicum.shareit.request.exceptions.NotFoundItemRequestException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.services.ItemRequestService;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService itemRequestService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void initiate() {
        when(userRepository.findById(1))
                .thenReturn(Optional.ofNullable(User.builder()
                        .id(1)
                        .name("name1")
                        .build()));
        when(userRepository.findById(2))
                .thenReturn(Optional.ofNullable(User.builder()
                        .id(2)
                        .name("name1")
                        .build()));
        when(itemRepository.findById(1))
                .thenReturn(Optional.ofNullable(Item.builder()
                        .id(1)
                        .name("name")
                        .description("description")
                        .owner(User.builder()
                                .id(1)
                                .build())
                        .build()));
    }

    @Test
    void createItemRequest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("description1")
                .build();

        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenAnswer(invocationOnMock -> ItemRequest.builder()
                        .id(1)
                        .description(invocationOnMock.getArgument(0, ItemRequest.class).getDescription())
                        .build());

        ItemRequestReturnDto itemRequest = itemRequestService.createItemRequest(itemRequestDto, 1);

        assertThat(itemRequest, hasProperty("description", is(itemRequestDto.getDescription())));

        verify(itemRequestRepository, times(1))
                .save(any(ItemRequest.class));
    }

    @Test
    void findRequestById_whenFound_thenReturnItemRequest() {
        when(itemRequestRepository.findById(1))
                .thenReturn(Optional.ofNullable(ItemRequest.builder()
                        .id(1)
                        .build()));

        ItemRequestReturnDto itemRequestReturnDto = itemRequestService.findRequestById(1, 1);
        assertThat(itemRequestReturnDto, hasProperty("id", is(1)));

        verify(itemRequestRepository, times(1))
                .findById(1);
    }

    @Test
    void findRequestById_whenNotFound_thenThrowNotFoundItemRequestException() {
        assertThrows(NotFoundItemRequestException.class, () -> itemRequestService.findRequestById(99, 1));
    }
}