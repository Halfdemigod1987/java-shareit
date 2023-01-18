package ru.practicum.shareit.request.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestReturnDto;
import ru.practicum.shareit.request.dto.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.exceptions.NotFoundItemRequestException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.services.ItemRequestService;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.exceptions.NotFoundUserException;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper = ItemRequestMapper.INSTANCE;

    @Override
    public ItemRequestReturnDto createItemRequest(ItemRequestDto itemRequestDto, int userId) {
        User requestor = findUser(userId);
        ItemRequest itemRequest = itemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(requestor);
        return itemRequestMapper.itemRequestToItemRequestReturnDto(
                itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestReturnDto> findRequests(int userId) {
        findUser(userId);
        return itemRequestRepository
                .findByRequestor_Id(userId, Sort.by(Sort.Direction.ASC, "created"))
                .stream()
                .map(itemRequestMapper::itemRequestToItemRequestReturnDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestReturnDto findRequestById(int requestId, int userId) {
        findUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundItemRequestException(String.format("Request with id = %d not found", requestId))
        );
        return itemRequestMapper.itemRequestToItemRequestReturnDto(itemRequest);
    }

    @Override
    public List<ItemRequestReturnDto> findAllRequests(int userId, Integer from, Integer size) {
        findUser(userId);
        Pageable pageable;
        if (size == null) {
            pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.ASC, "created"));
        } else {
            pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "created"));
        }
        return itemRequestRepository
                .findByRequestor_IdNot(userId, pageable)
                .stream()
                .map(itemRequestMapper::itemRequestToItemRequestReturnDto)
                .collect(Collectors.toList());
    }

    private User findUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(String.format("User with id = %d not found", userId)));
    }
}
