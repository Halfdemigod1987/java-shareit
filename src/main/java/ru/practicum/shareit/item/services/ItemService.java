package ru.practicum.shareit.item.services;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentReturnDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemReturnDto;

import java.util.List;
import java.util.Map;

public interface ItemService {
    List<ItemReturnDto> findAllItems(int userId);

    ItemReturnDto findItemById(int id, int userId);

    ItemDto createItem(ItemDto itemDto, int userId);

    ItemDto partialUpdate(int id, Map<String, String> updates, int userId);

    void deleteItem(int id);

    List<ItemDto> searchItems(String text, int userId);

    CommentReturnDto createComment(int itemId, CommentDto commentDto, int userId);
}
