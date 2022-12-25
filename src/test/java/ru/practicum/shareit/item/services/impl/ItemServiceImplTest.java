package ru.practicum.shareit.item.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentReturnDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemReturnDto;
import ru.practicum.shareit.item.exceptions.NotOwnerException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.services.ItemService;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private BookingRepository bookingRepository;

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
    void findItemById_whenFound_thenReturnItem() {
        ItemReturnDto itemReturnDto = itemService.findItemById(1, 1);
        assertThat(itemReturnDto, hasProperty("id", is(1)));
        assertThat(itemReturnDto, hasProperty("ownerId", is(1)));

        verify(itemRepository, times(1))
                .findById(1);
    }

    @Test
    void createItem_whenInvoked_thenReturnItem() {
        ItemDto itemDto = ItemDto.builder()
                .name("name1")
                .description("description1")
                .available(true)
                .build();

        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocationOnMock -> Item.builder()
                        .id(1)
                        .name(invocationOnMock.getArgument(0, Item.class).getName())
                        .description(invocationOnMock.getArgument(0, Item.class).getDescription())
                        .available(invocationOnMock.getArgument(0, Item.class).isAvailable())
                        .owner(invocationOnMock.getArgument(0, Item.class).getOwner())
                        .build());

        ItemReturnDto itemReturnDto = itemService.createItem(itemDto, 1);

        assertThat(itemReturnDto, hasProperty("name", is(itemDto.getName())));
        assertThat(itemReturnDto, hasProperty("description", is(itemDto.getDescription())));
        assertThat(itemReturnDto, hasProperty("available", is(itemDto.getAvailable())));
        assertThat(itemReturnDto, hasProperty("ownerId", is(1)));

        verify(itemRepository, times(1))
                .save(any(Item.class));
    }

    @Test
    void partialUpdate_whenInvoked_thenReturnItem() {
        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocationOnMock -> Item.builder()
                        .id(1)
                        .name(invocationOnMock.getArgument(0, Item.class).getName())
                        .description(invocationOnMock.getArgument(0, Item.class).getDescription())
                        .available(invocationOnMock.getArgument(0, Item.class).isAvailable())
                        .owner(invocationOnMock.getArgument(0, Item.class).getOwner())
                        .build());

        Map<String, String> update = new HashMap<>();
        update.put("name", "nameUpdated");
        update.put("description", "descriptionUpdated");

        ItemReturnDto itemReturnDto = itemService.partialUpdate(1, update, 1);

        assertThat(itemReturnDto, hasProperty("name", is(update.get("name"))));
        assertThat(itemReturnDto, hasProperty("description", is(update.get("description"))));

        verify(itemRepository, times(1))
                .save(any(Item.class));
    }

    @Test
    void partialUpdate_whenNotOwner_thenThrowNotOwnerException() {
        Map<String, String> update = new HashMap<>();
        assertThrows(NotOwnerException.class, () -> itemService.partialUpdate(1, update, 2));
    }

    @Test
    void deleteItem_whenInvoked_thenNotThrowErrors() {
        itemService.deleteItem(1);
    }

    @Test
    void createComment_whenInvoked_thenReturnComment() {
        CommentDto commentDto = CommentDto.builder()
                .text("text1")
                .build();

        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocationOnMock -> Comment.builder()
                        .id(1)
                        .text(invocationOnMock.getArgument(0, Comment.class).getText())
                        .author(invocationOnMock.getArgument(0, Comment.class).getAuthor())
                        .build());

        when(bookingRepository
                .findTop1ByBooker_IdAndItem_IdAndEndIsBeforeAndStatus(anyInt(), anyInt(), any(LocalDateTime.class), any(BookingStatus.class)))
                .thenReturn(Optional.ofNullable(Booking.builder().id(1).build()));

        CommentReturnDto commentReturnDto = itemService.createComment(1, commentDto, 1);

        assertThat(commentReturnDto, hasProperty("text", is(commentDto.getText())));
        assertThat(commentReturnDto, hasProperty("authorName", is("name1")));

        verify(commentRepository, times(1))
                .save(any(Comment.class));
    }
}