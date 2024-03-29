package ru.practicum.shareit.item.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentReturnDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemReturnDto;
import ru.practicum.shareit.item.dto.mappers.CommentMapper;
import ru.practicum.shareit.item.dto.mappers.ItemMapper;
import ru.practicum.shareit.item.exceptions.NotFoundItemException;
import ru.practicum.shareit.item.exceptions.NotOwnerException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.services.ItemService;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.exceptions.NotFoundItemRequestException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.exceptions.NotFoundUserException;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper = ItemMapper.INSTANCE;
    private final BookingMapper bookingMapper = BookingMapper.INSTANCE;
    private final CommentMapper commentMapper = CommentMapper.INSTANCE;

    @Override
    public List<ItemReturnDto> findAllItems(int userId, Integer from, Integer size) {
        findUser(userId);
        Pageable pageable;
        if (size == null) {
            pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.ASC, "id"));
        } else {
            pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        }
        return itemRepository.findByOwner_Id(userId, pageable)
                .stream()
                .map(itemMapper::itemToItemReturnDto)
                .peek(itemReturnDto -> fillItemReturn(itemReturnDto, userId))
                .collect(Collectors.toList());
    }

    @Override
    public ItemReturnDto findItemById(int id, int userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundItemException(String.format("Item with id = %d not found", id)));
        ItemReturnDto itemDto = itemMapper.itemToItemReturnDto(item);
        fillItemReturn(itemDto, userId);
        return itemDto;
    }

    private void fillItemReturn(ItemReturnDto itemDto, int userId) {
        if (itemDto.getOwnerId() == userId) {
            Booking lastBooking = bookingRepository
                    .findTop1ByItem_idAndStartIsBefore(itemDto.getId(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"))
                    .orElse(null);
            Booking nextBooking = bookingRepository
                    .findTop1ByItem_idAndStartIsAfter(itemDto.getId(), LocalDateTime.now(), Sort.by(Sort.Direction.ASC, "start"))
                    .orElse(null);
            itemDto.setLastBooking(bookingMapper.bookingToBookingItemReturnDto(lastBooking));
            itemDto.setNextBooking(bookingMapper.bookingToBookingItemReturnDto(nextBooking));
        }
    }

    @Override
    public ItemReturnDto createItem(ItemDto itemDto, int userId) {
        User applicant = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(String.format("User with id = %d not found", userId)));
        Item item = itemMapper.itemDtoToItem(itemDto);
        item.setOwner(applicant);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(
                    () -> new NotFoundItemRequestException(String.format("Request with id = %d not found", itemDto.getRequestId()))
            );
            item.setRequest(itemRequest);
        }
        return itemMapper.itemToItemReturnDto(
                itemRepository.save(item));
    }

    @Override
    public ItemReturnDto partialUpdate(int id, Map<String, String> updates, int userId) {
        User applicant = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(String.format("User with id = %d not found", userId)));

        Item oldItem = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundItemException(String.format("Item with id = %d not found", id)));

        if (oldItem.getOwner().getId() != applicant.getId()) {
            throw new NotOwnerException(
                    String.format("User with id = %d is not the owner of item with id = %d", userId, id));
        }

        Item item = new Item();
        item.setId(oldItem.getId());
        item.setName(oldItem.getName());
        item.setDescription(oldItem.getDescription());
        item.setAvailable(oldItem.isAvailable());
        item.setRequest(oldItem.getRequest());
        item.setOwner(oldItem.getOwner());

        updates.forEach((key, value) -> {
            if (key.equalsIgnoreCase("name")) {
                item.setName(value);
            }
            if (key.equalsIgnoreCase("description")) {
                item.setDescription(value);
            }
            if (key.equalsIgnoreCase("available")) {
                item.setAvailable(Boolean.parseBoolean(value));
            }
        });

        return itemMapper.itemToItemReturnDto(
                itemRepository.save(item));
    }

    @Override
    public void deleteItem(int id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundItemException(String.format("Item with id = %d not found", id)));
        itemRepository.delete(item);
    }

    @Override
    public List<ItemReturnDto> searchItems(String text, int userId, Integer from, Integer size) {
        findUser(userId);

        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        Pageable pageable;
        if (size == null) {
            pageable = PageRequest.of(0, Integer.MAX_VALUE);
        } else {
            pageable = PageRequest.of(from / size, size);
        }

        return itemRepository.search(text, pageable)
                .stream()
                .map(itemMapper::itemToItemReturnDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentReturnDto createComment(int itemId, CommentDto commentDto, int userId) {
        Comment comment = commentMapper.commentDtoToComment(commentDto);

        bookingRepository
                .findTop1ByBooker_IdAndItem_IdAndEndIsBeforeAndStatus(userId, itemId, LocalDateTime.now(), BookingStatus.APPROVED)
                .orElseThrow(() -> new IllegalArgumentException(String.format("User %d cannot comment on item %d without booking it", userId, itemId)));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundItemException(String.format("Item with id = %d not found", itemId)));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(String.format("User with id = %d not found", userId)));
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return commentMapper.commentToCommentReturnDto(
                commentRepository.save(comment));
    }

    private void findUser(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(String.format("User with id = %d not found", userId)));
    }

}
