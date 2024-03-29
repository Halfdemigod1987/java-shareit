package ru.practicum.shareit.request.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestReturnDto;
import ru.practicum.shareit.request.services.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestReturnDto> createItemRequest(
            @RequestBody ItemRequestDto itemRequest,
            @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return ResponseEntity.ok(itemRequestService.createItemRequest(itemRequest, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestReturnDto>> findRequests(
            @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return ResponseEntity.ok(itemRequestService.findRequests(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestReturnDto>> findAllRequests(
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size,
            @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return ResponseEntity.ok(itemRequestService.findAllRequests(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestReturnDto> findRequestById(
            @PathVariable Integer requestId,
            @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return ResponseEntity.ok(itemRequestService.findRequestById(requestId, userId));
    }
}
