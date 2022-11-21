package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.concurrent.atomic.AtomicInteger;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Item {
    private static AtomicInteger ITEM_ID = new AtomicInteger();

    @EqualsAndHashCode.Include
    private int id;
    private String name;
    private String description;
    private boolean available;
    private ItemRequest request;
    private User owner;
    public void setId() {
        id = ITEM_ID.incrementAndGet();
    }

}
