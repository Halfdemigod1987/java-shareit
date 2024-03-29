package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.booking.dto.BookingItemReturnDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@Jacksonized
public class ItemReturnDto {
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private BookingItemReturnDto lastBooking;
    private BookingItemReturnDto nextBooking;
    private List<CommentReturnDto> comments;
    private int ownerId;
    private int requestId;
}
