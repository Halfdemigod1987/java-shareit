package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@Sql(scripts = {"file:assets/scripts/test_data.sql"})
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void search() {
        List<Item> items;
        items = itemRepository.search(
                "name",
                PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.ASC, "id")));
        assertThat(items, hasSize(3));
        assertThat(items, hasItem(hasProperty("id", is(1))));
        assertThat(items, hasItem(hasProperty("id", is(2))));
        assertThat(items, hasItem(hasProperty("id", is(3))));

        items = itemRepository.search(
                "name",
                PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "id")));
        assertThat(items, hasSize(2));
        assertThat(items, hasItem(hasProperty("id", is(1))));
        assertThat(items, hasItem(hasProperty("id", is(2))));

        items = itemRepository.search(
                "description",
                PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.ASC, "id")));
        assertThat(items, hasSize(2));
        assertThat(items, hasItem(hasProperty("id", is(1))));
        assertThat(items, hasItem(hasProperty("id", is(2))));

    }
}