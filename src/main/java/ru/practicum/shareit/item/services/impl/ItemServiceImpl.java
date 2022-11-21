package ru.practicum.shareit.item.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemRepository;
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
    @Override
    public List<Item> findAllItems(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(String.format("User with id = %d not found", userId)));
        return itemRepository.findAll().stream()
                .filter(user -> user.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item findItemById(int id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundItemException(String.format("Item with id = %d not found", id)));
    }

    @Override
    public Item createItem(Item item, int userId) {
        User applicant = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(String.format("User with id = %d not found", userId)));
        item.setOwner(applicant);
        return itemRepository.create(item);
    }

    @Override
    public Item partialUpdate(int id, Map<String, String> updates, int userId) {
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

        return itemRepository.update(id, item);
    }

    @Override
    public void deleteItem(int id) {
        itemRepository.delete(id);
    }

    @Override
    public List<Item> searchItems(String text, int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(String.format("User with id = %d not found", userId)));

        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemRepository.findAll().stream()
                .filter(item -> item.isAvailable()
                        && (item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
    }
}
