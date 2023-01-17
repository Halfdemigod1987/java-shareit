package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Map;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findAllItems(
            @RequestParam(required = false) @Min(0) Integer from,
            @RequestParam(required = false) @Min(1) Integer size,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Get items with userId={}, from={}, size={}", userId, from, size);
        return itemClient.findAllItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(
            @PathVariable Integer itemId,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.findItemById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @PathVariable Integer itemId,
            @RequestBody Map<String, String> updates,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Updating item {}, {}", userId, updates);
        return itemClient.updateItem(userId, itemId, updates);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(
            @PathVariable Integer itemId,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Deleting item {}", userId);
        return itemClient.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestParam(value = "text") String text,
            @RequestParam(required = false) @Min(0) Integer from,
            @RequestParam(required = false) @Min(1) Integer size,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Search items with text={}, userId={}, from={}, size={}", text, userId, from, size);
        return itemClient.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @PathVariable Integer itemId,
            @Valid @RequestBody CommentDto comment,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Creating comment {}, itemId={}, userId={}", comment, itemId, userId);
        return itemClient.createComment(userId, itemId, comment);
    }
}
