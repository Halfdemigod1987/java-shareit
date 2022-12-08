package ru.practicum.shareit.request.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemRequest {
    private static AtomicInteger REQUEST_ID = new AtomicInteger();

    @EqualsAndHashCode.Include
    private int id;
    private String description;
    private User requestor;
    private LocalDateTime created;

    public void setId() {
        id = REQUEST_ID.incrementAndGet();
    }
}
