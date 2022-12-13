package ru.practicum.shareit.booking.services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReturnDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.exceptions.AccessDeniedException;
import ru.practicum.shareit.booking.exceptions.NotFoundBookingException;
import ru.practicum.shareit.booking.exceptions.UnsupportedBookingStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.services.BookingService;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.exceptions.NotFoundItemException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.exceptions.NotFoundUserException;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper = BookingMapper.INSTANCE;

    @Override
    public BookingReturnDto createBooking(BookingDto bookingDto, int userId) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(String.format("User with id = %d not found", userId)));
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new IllegalArgumentException("End of the booking cannot be before start");
        }
        Booking booking = bookingMapper.bookingDtoToBooking(bookingDto);
        Item item = itemRepository
                .findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundItemException(String.format("Item with id = %d not found", bookingDto.getItemId())));
        if (!item.isAvailable()) {
            throw new IllegalArgumentException(String.format("Item %s is not available", item.getName()));
        }
        if (item.getOwner().getId() == userId) {
            throw new AccessDeniedException(String.format("User %d is owner of the item %d and cannot book it", userId, bookingDto.getItemId()));
        }
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return bookingMapper.bookingToBookingReturnDto(
                bookingRepository.save(booking));
    }

    @Override
    public BookingReturnDto changeBookingStatus(int bookingId, boolean approved, int userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundBookingException(String.format("Booking with id = %d not found", bookingId)));
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(String.format("User with id = %d not found", userId)));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new AccessDeniedException(String.format("User with id = %d is not owner of the item %s",
                    userId, booking.getItem().getName()));
        }
        if (approved) {
            if (booking.getStatus().equals(BookingStatus.APPROVED)) {
                throw new IllegalArgumentException("Cannot approve booking that is already approved");
            }
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingMapper.bookingToBookingReturnDto(
                bookingRepository.save(booking)
        );
    }

    @Override
    public BookingReturnDto findBookingById(int id, int userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundBookingException(String.format("Booking with id = %d not found", id)));
        if (userId != booking.getBooker().getId() && userId != booking.getItem().getOwner().getId()) {
            throw new AccessDeniedException(String.format("User with id = %d have not rights to access this booking", userId));
        }
        return bookingMapper.bookingToBookingReturnDto(booking);
    }

    @Override
    public List<BookingReturnDto> findAllBookings(String state, int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(String.format("User with id = %d not found", userId)));
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByBooker_Id(userId, sort);
                break;
            case "CURRENT":
                bookings = bookingRepository
                        .findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), sort);
                break;
            case "PAST":
                bookings = bookingRepository
                        .findByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(), sort);
                break;
            case "FUTURE":
                bookings = bookingRepository
                        .findByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(), sort);
                break;
            case "WAITING":
                bookings = bookingRepository
                        .findByBooker_IdAndStatus(userId, BookingStatus.WAITING, sort);
                break;
            case "REJECTED":
                bookings = bookingRepository
                    .findByBooker_IdAndStatus(userId, BookingStatus.REJECTED, sort);
                break;
            default:
                throw new UnsupportedBookingStatusException(String.format("Unknown state: %s", state));
        }
        return bookings.stream()
                .map(bookingMapper::bookingToBookingReturnDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingReturnDto> findAllOwnerBookings(String state, int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(String.format("User with id = %d not found", userId)));
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByItem_Owner_Id(userId, sort);
                break;
            case "CURRENT":
                bookings = bookingRepository
                        .findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), sort);
                break;
            case "PAST":
                bookings = bookingRepository
                        .findByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), sort);
                break;
            case "FUTURE":
                bookings = bookingRepository
                        .findByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(), sort);
                break;
            case "WAITING":
                bookings = bookingRepository
                        .findByItem_Owner_IdAndStatus(userId, BookingStatus.WAITING, sort);
                break;
            case "REJECTED":
                bookings = bookingRepository
                        .findByItem_Owner_IdAndStatus(userId, BookingStatus.REJECTED, sort);
                break;
            default:
                throw new UnsupportedBookingStatusException(String.format("Unknown state: %s", state));
        }
        return bookings.stream()
                .map(bookingMapper::bookingToBookingReturnDto)
                .collect(Collectors.toList());
    }


}
