package ru.practicum.shareit.booking.dto.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemReturnDto;
import ru.practicum.shareit.booking.dto.BookingReturnDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.mappers.ItemMapper;
import ru.practicum.shareit.user.dto.mappers.UserMapper;

@Mapper(uses = {ItemMapper.class, UserMapper.class})
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    BookingReturnDto bookingToBookingReturnDto(Booking booking);

    @Mapping(source = "booker.id", target = "bookerId")
    BookingItemReturnDto bookingToBookingItemReturnDto(Booking booking);

    Booking bookingDtoToBooking(BookingDto bookingDto);

}
