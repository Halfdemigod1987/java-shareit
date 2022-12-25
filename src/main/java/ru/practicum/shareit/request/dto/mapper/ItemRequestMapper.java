package ru.practicum.shareit.request.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.mappers.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestReturnDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(uses = {ItemMapper.class})
public interface ItemRequestMapper {

    ItemRequestMapper INSTANCE = Mappers.getMapper(ItemRequestMapper.class);

    ItemRequestReturnDto itemRequestToItemRequestReturnDto(ItemRequest itemRequest);

    ItemRequest itemRequestDtoToItemRequest(ItemRequestDto itemRequestDto);

}
