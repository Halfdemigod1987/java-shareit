package ru.practicum.shareit.item.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentReturnDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemReturnDto;
import ru.practicum.shareit.item.services.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemReturnDto>> findAllItems(
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return ResponseEntity.ok(itemService.findAllItems(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemReturnDto> findItemById(
            @PathVariable int id,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return ResponseEntity.ok(itemService.findItemById(id, userId));
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(itemService.createItem(itemDto, userId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(
            @PathVariable int id,
            @RequestBody Map<String, String> updates,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return ResponseEntity.ok(itemService.partialUpdate(id, updates, userId));
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable int id) {
        itemService.deleteItem(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(
            @RequestParam(value = "text") String text,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return ResponseEntity.ok(itemService.searchItems(text, userId));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentReturnDto> createComment(
            @PathVariable int itemId,
            @Valid @RequestBody CommentDto comment,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return ResponseEntity.ok(itemService.createComment(itemId, comment, userId));
    }

}
