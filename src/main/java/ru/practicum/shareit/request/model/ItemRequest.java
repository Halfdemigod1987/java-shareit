package ru.practicum.shareit.request.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "requests")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", referencedColumnName = "id")
    private User requestor;
    @Transient
    private LocalDateTime created;
    @OneToMany(mappedBy = "request")
    private Set<Item> items;
}
