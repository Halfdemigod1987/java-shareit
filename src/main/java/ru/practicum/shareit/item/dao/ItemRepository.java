package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> findAll();

    Optional<Item> findById(int id);

    Item create(Item item);

    Item update(int id, Item item);

    void delete(int id);
}
