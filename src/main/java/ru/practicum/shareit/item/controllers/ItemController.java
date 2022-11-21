package ru.practicum.shareit.item.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mappers.ItemMapper;
import ru.practicum.shareit.item.services.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemDto>> findAllItems(
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return ResponseEntity.ok(
                itemService.findAllItems(userId)
                        .stream()
                        .map(ItemMapper.INSTANCE::itemToItemDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> findItemById(@PathVariable int id) {
        return ResponseEntity
                .ok(
                        ItemMapper.INSTANCE.itemToItemDto(
                                itemService.findItemById(id)
                        )
                );
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ItemMapper.INSTANCE.itemToItemDto(
                                itemService.createItem(ItemMapper.INSTANCE.itemDtoToItem(itemDto), userId)
                        )
                );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(
            @PathVariable int id,
            @RequestBody Map<String, String> updates,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return ResponseEntity
                .ok(
                        ItemMapper.INSTANCE.itemToItemDto(
                                itemService.partialUpdate(id, updates, userId)
                        )
                );
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable int id) {
        itemService.deleteItem(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(
            @RequestParam(value = "text") String text,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return ResponseEntity
                .ok(
                        itemService.searchItems(text, userId)
                                .stream()
                                .map(ItemMapper.INSTANCE::itemToItemDto)
                                .collect(Collectors.toList())
                );
    }

}
