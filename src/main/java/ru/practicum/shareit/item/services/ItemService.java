package ru.practicum.shareit.item.services;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemService {
    List<Item> findAllItems(int userId);

    Item findItemById(int id);

    Item createItem(Item item, int userId);

    Item partialUpdate(int id, Map<String, String> updates, int userId);

    void deleteItem(int id);

    List<Item> searchItems(String text, int userId);
}
