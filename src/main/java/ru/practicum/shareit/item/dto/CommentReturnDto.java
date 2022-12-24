package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Value
@Builder
@AllArgsConstructor
@Jacksonized
public class CommentReturnDto {
    int id;
    String text;
    String authorName;
    LocalDateTime created;
}
