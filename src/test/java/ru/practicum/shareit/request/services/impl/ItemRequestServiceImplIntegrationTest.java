package ru.practicum.shareit.request.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestReturnDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.services.ItemRequestService;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;

@Transactional
@SpringBootTest
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void initiate() {
        User user1 = userRepository.save(User.builder()
                .name("email1@test.com")
                .email("name1")
                .build());
        User user2 = userRepository.save(User.builder()
                .name("email2@test.com")
                .email("name2")
                .build());

        ItemRequest itemRequest1 = itemRequestRepository.save(ItemRequest.builder()
                .description("description1")
                .requestor(user2)
                .created(LocalDateTime.now().plusDays(1))
                .build());
        ItemRequest itemRequest2 = itemRequestRepository.save(ItemRequest.builder()
                .description("description2")
                .requestor(user2)
                .created(LocalDateTime.now().plusDays(2))
                .build());
        ItemRequest itemRequest3 = itemRequestRepository.save(ItemRequest.builder()
                .description("description3")
                .requestor(user1)
                .created(LocalDateTime.now().plusDays(3))
                .build());
        ItemRequest itemRequest4 = itemRequestRepository.save(ItemRequest.builder()
                .description("description4")
                .requestor(user2)
                .created(LocalDateTime.now().plusDays(4))
                .build());

        itemRepository.save(Item.builder()
                .name("name1")
                .description("description1")
                .available(true)
                .owner(user1)
                .request(itemRequest1)
                .build());
        itemRepository.save(Item.builder()
                .name("name2")
                .description("description2")
                .available(true)
                .owner(user1)
                .request(itemRequest2)
                .build());
        itemRepository.save(Item.builder()
                .name("name3")
                .description("description3")
                .available(true)
                .owner(user2)
                .request(itemRequest3)
                .build());
        itemRepository.save(Item.builder()
                .name("name4")
                .description("description4")
                .available(true)
                .owner(user1)
                .request(itemRequest4)
                .build());
    }

    @Test
    void findAllRequests_whenInvoked_thenReturnItemsRequestCollectionWithRequestor() {
        List<User> users = userRepository.findAll();

        List<ItemRequestReturnDto> itemRequests = itemRequestService.findAllRequests(users.get(0).getId(), null, null);

        assertThat(itemRequests, hasSize(3));
        assertThat(itemRequests, hasItem(hasProperty("description", is("description1"))));
        assertThat(itemRequests, hasItem(hasProperty("description", is("description2"))));
        assertThat(itemRequests, hasItem(hasProperty("description", is("description4"))));

        itemRequests = itemRequestService.findAllRequests(users.get(0).getId(), 0, 2);

        assertThat(itemRequests, hasSize(2));
        assertThat(itemRequests, hasItem(hasProperty("description", is("description1"))));
        assertThat(itemRequests, hasItem(hasProperty("description", is("description2"))));
    }

    @Test
    void findRequests() {
        List<User> users = userRepository.findAll();

        List<ItemRequestReturnDto> itemRequests = itemRequestService.findRequests(users.get(1).getId());

        assertThat(itemRequests, hasSize(3));
        assertThat(itemRequests, hasItem(hasProperty("description", is("description1"))));
        assertThat(itemRequests, hasItem(hasProperty("description", is("description2"))));
        assertThat(itemRequests, hasItem(hasProperty("description", is("description4"))));
    }
}