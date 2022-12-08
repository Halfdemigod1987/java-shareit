package ru.practicum.shareit.booking.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO Sprint add-bookings.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Booking {
    private static AtomicInteger BOOKING_ID = new AtomicInteger();

    @EqualsAndHashCode.Include
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private Status status;

    public void setId() {
        id = BOOKING_ID.incrementAndGet();
    }
}
