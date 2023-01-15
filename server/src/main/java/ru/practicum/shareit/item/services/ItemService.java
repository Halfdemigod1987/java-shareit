package ru.practicum.shareit.item.services;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentReturnDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemReturnDto;

import java.util.List;
import java.util.Map;

public interface ItemService {
    List<ItemReturnDto> findAllItems(int userId, Integer from, Integer size);

    ItemReturnDto findItemById(int id, int userId);

    ItemReturnDto createItem(ItemDto itemDto, int userId);

    ItemReturnDto partialUpdate(int id, Map<String, String> updates, int userId);

    void deleteItem(int id);

    List<ItemReturnDto> searchItems(String text, int userId, Integer from, Integer size);

    CommentReturnDto createComment(int itemId, CommentDto commentDto, int userId);
}
