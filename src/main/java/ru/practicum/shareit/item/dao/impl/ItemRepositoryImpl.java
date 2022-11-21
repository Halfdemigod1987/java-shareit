package ru.practicum.shareit.item.dao.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final HashMap<Integer, Item> items = new HashMap<>();
    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Optional<Item> findById(int id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item create(Item item) {
        item.setId();
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item update(int id, Item item) {
        items.put(id, item);
        return items.get(id);
    }

    @Override
    public void delete(int id) {
        items.remove(id);
    }
}
