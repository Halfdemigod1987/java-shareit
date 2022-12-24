package ru.practicum.shareit.item.services.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemReturnDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.services.ItemService;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@Transactional
@SpringBootTest
class ItemServiceImplIntegrationTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

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

        itemRepository.save(Item.builder()
                .name("name1")
                .description("description1")
                .available(true)
                .owner(user1)
                .build());
        itemRepository.save(Item.builder()
                .name("name2")
                .description("description2")
                .available(true)
                .owner(user1)
                .build());
        itemRepository.save(Item.builder()
                .name("name3")
                .description("description3")
                .available(true)
                .owner(user2)
                .build());
        itemRepository.save(Item.builder()
                .name("name4")
                .description("description4")
                .available(true)
                .owner(user1)
                .build());
    }

    @AfterEach
    void delete() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllItems_whenInvoked_thenReturnItemsWithOwner() {
        List<User> users = userRepository.findAll();

        List<ItemReturnDto> items = itemService.findAllItems(users.get(0).getId(), null, null);

        assertThat(items, hasSize(3));
        items.forEach(
                item -> assertThat(item, hasProperty("ownerId", is(users.get(0).getId()))));

        items = itemService.findAllItems(users.get(0).getId(), 0, 2);

        assertThat(items, hasSize(2));
        items.forEach(
                item -> assertThat(item, hasProperty("ownerId", is(users.get(0).getId()))));
    }

    @Test
    void searchItems_whenInvoked_thenReturnItemsWithText() {
        List<User> users = userRepository.findAll();

        List<ItemReturnDto> items = itemService.searchItems("name1", users.get(0).getId(), null, null);

        assertThat(items, hasSize(1));
        items.forEach(
                item -> assertThat(item, anyOf(
                        hasProperty("name", containsString("name1")),
                        hasProperty("description", containsString("name1")))));

        items = itemService.searchItems("name", users.get(0).getId(), 0, 2);

        assertThat(items, hasSize(2));
        items.forEach(
                item -> assertThat(item, anyOf(
                        hasProperty("name", containsString("name")),
                        hasProperty("description", containsString("name")))));

        items = itemService.searchItems("noname", users.get(0).getId(), 0, 2);

        assertThat(items, empty());
    }
}