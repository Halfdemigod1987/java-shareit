package ru.practicum.shareit.user.model;

import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;
    private String name;
    @Column(unique = true)
    private String email;
    @OneToMany(mappedBy = "requestor")
    private Set<ItemRequest> itemRequests;
    @OneToMany(mappedBy = "booker")
    private Set<Booking> bookings;
    @OneToMany(mappedBy = "author")
    private Set<Comment> comments;
    @OneToMany(mappedBy = "owner")
    @ToString.Exclude
    private Set<Item> items;
}
