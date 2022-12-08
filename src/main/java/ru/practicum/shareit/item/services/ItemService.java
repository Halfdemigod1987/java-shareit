package ru.practicum.shareit.item.services;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

public interface ItemService {
    List<ItemDto> findAllItems(int userId);

    ItemDto findItemById(int id);

    ItemDto createItem(ItemDto itemDto, int userId);

    ItemDto partialUpdate(int id, Map<String, String> updates, int userId);

    void deleteItem(int id);

    List<ItemDto> searchItems(String text, int userId);
}
