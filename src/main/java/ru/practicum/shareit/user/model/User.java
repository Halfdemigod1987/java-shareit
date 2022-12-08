package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.concurrent.atomic.AtomicInteger;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    private static AtomicInteger USER_ID = new AtomicInteger();

    @EqualsAndHashCode.Include
    private int id;
    private String name;
    private String email;

    public void setId() {
        id = USER_ID.incrementAndGet();
    }
}
