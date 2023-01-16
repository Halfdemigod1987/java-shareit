package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @Valid @RequestBody ItemRequestDto itemRequest,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        log.info("Creating item request {}, userId={}", itemRequest, userId);
        return itemRequestClient.createItemRequest(userId, itemRequest);
    }

    @GetMapping
    public ResponseEntity<Object> findRequests(
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        log.info("Get item requests with userId={}", userId);
        return itemRequestClient.findRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequests(
            @RequestParam(required = false) @Min(0) Integer from,
            @RequestParam(required = false) @Min(1) Integer size,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Get item requests with userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.findAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findRequestById(
            @PathVariable int requestId,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        log.info("Get item request {}, userId={}", requestId, userId);
        return itemRequestClient.findRequestById(userId, requestId);
    }
}
