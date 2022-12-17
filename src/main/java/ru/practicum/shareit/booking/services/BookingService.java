package ru.practicum.shareit.booking.services;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReturnDto;

import java.util.List;

public interface BookingService {
    BookingReturnDto createBooking(BookingDto booking, int userId);

    BookingReturnDto changeBookingStatus(int bookingId, boolean approved, int userId);

    BookingReturnDto findBookingById(int id, int userId);

    List<BookingReturnDto> findAllBookings(String state, int userId);

    List<BookingReturnDto> findAllOwnerBookings(String state, int userId);
}
