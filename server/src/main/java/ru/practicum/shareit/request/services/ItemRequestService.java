package ru.practicum.shareit.request.services;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestReturnDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestReturnDto createItemRequest(ItemRequestDto itemRequest, int userId);

    List<ItemRequestReturnDto> findAllRequests(int userId, Integer from, Integer size);

    List<ItemRequestReturnDto> findRequests(int userId);

    ItemRequestReturnDto findRequestById(int requestId, int userId);
}
