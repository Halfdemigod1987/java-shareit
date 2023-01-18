package ru.practicum.shareit.item.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentReturnDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemReturnDto;
import ru.practicum.shareit.item.services.ItemService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemReturnDto>> findAllItems(
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size,
            @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return ResponseEntity.ok(itemService.findAllItems(userId, from, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemReturnDto> findItemById(
            @PathVariable Integer id,
            @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return ResponseEntity.ok(itemService.findItemById(id, userId));
    }

    @PostMapping
    public ResponseEntity<ItemReturnDto> createItem(
            @RequestBody ItemDto itemDto,
            @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(itemService.createItem(itemDto, userId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemReturnDto> updateItem(
            @PathVariable Integer id,
            @RequestBody Map<String, String> updates,
            @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return ResponseEntity.ok(itemService.partialUpdate(id, updates, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Integer id) {
        itemService.deleteItem(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemReturnDto>> searchItems(
            @RequestParam String text,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size,
            @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return ResponseEntity.ok(itemService.searchItems(text, userId, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentReturnDto> createComment(
            @PathVariable Integer itemId,
            @RequestBody CommentDto comment,
            @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return ResponseEntity.ok(itemService.createComment(itemId, comment, userId));
    }

}
