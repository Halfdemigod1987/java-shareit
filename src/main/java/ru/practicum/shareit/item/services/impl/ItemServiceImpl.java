package ru.practicum.shareit.item.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mappers.ItemMapper;
import ru.practicum.shareit.item.exceptions.NotFoundItemException;
import ru.practicum.shareit.item.exceptions.NotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.services.ItemService;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.exceptions.NotFoundUserException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper = ItemMapper.INSTANCE;

    @Override
    public List<ItemDto> findAllItems(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(String.format("User with id = %d not found", userId)));
        return itemRepository.findAll().stream()
                .filter(user -> user.getOwner().getId() == userId)
                .map(itemMapper::itemToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto findItemById(int id) {
        return itemMapper.itemToItemDto(
                itemRepository.findById(id)
                        .orElseThrow(() -> new NotFoundItemException(String.format("Item with id = %d not found", id))));
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, int userId) {
        User applicant = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(String.format("User with id = %d not found", userId)));
        Item item = itemMapper.itemDtoToItem(itemDto);
        item.setOwner(applicant);
        return itemMapper.itemToItemDto(
                itemRepository.create(item));
    }

    @Override
    public ItemDto partialUpdate(int id, Map<String, String> updates, int userId) {
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

        return itemMapper.itemToItemDto(
                itemRepository.update(id, item));
    }

    @Override
    public void deleteItem(int id) {
        itemRepository.delete(id);
    }

    @Override
    public List<ItemDto> searchItems(String text, int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(String.format("User with id = %d not found", userId)));

        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemRepository.findAll().stream()
                .filter(item -> item.isAvailable()
                        && (item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .map(itemMapper::itemToItemDto)
                .collect(Collectors.toList());
    }
}