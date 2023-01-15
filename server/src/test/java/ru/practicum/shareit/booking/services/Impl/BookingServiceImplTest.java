package ru.practicum.shareit.booking.services.Impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReturnDto;
import ru.practicum.shareit.booking.exceptions.AccessDeniedException;
import ru.practicum.shareit.booking.exceptions.NotFoundBookingException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.services.BookingService;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private BookingRepository bookingRepository;

    @BeforeEach
    void initiate() {
        when(userRepository.findById(1))
                .thenReturn(Optional.ofNullable(User.builder()
                        .id(1)
                        .build()));
        when(userRepository.findById(2))
                .thenReturn(Optional.ofNullable(User.builder()
                        .id(2)
                        .build()));
        when(itemRepository.findById(1))
                .thenReturn(Optional.ofNullable(Item.builder()
                        .id(1)
                        .available(true)
                        .owner(User.builder().id(1).build())
                        .build()));
    }

    @Test
    void createBooking_whenInvoked_thenReturnBooking() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .itemId(1)
                .build();

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocationOnMock -> Booking.builder()
                        .id(1)
                        .start(invocationOnMock.getArgument(0, Booking.class).getStart())
                        .end(invocationOnMock.getArgument(0, Booking.class).getEnd())
                        .item(Item.builder()
                                .id(invocationOnMock.getArgument(0, Booking.class).getItem().getId())
                                .build())
                        .booker(User.builder()
                                .id(invocationOnMock.getArgument(0, Booking.class).getBooker().getId())
                                .build())
                        .status(invocationOnMock.getArgument(0, Booking.class).getStatus())
                        .build());

        BookingReturnDto bookingReturnDto = bookingService.createBooking(bookingDto, 2);

        assertThat(bookingReturnDto, hasProperty("start", is(bookingDto.getStart())));
        assertThat(bookingReturnDto, hasProperty("end", is(bookingDto.getEnd())));
        assertThat(bookingReturnDto.getItem(), hasProperty("id", is(1)));
        assertThat(bookingReturnDto.getBooker(), hasProperty("id", is(2)));
        assertThat(bookingReturnDto, hasProperty("status", is(BookingStatus.WAITING)));

        verify(bookingRepository, times(1))
                .save(any(Booking.class));
    }

    @Test
    void changeBookingStatus_whenApproved_thenReturnBookingWithApprovedStatus() {
        when(bookingRepository.findById(1))
                .thenReturn(Optional.ofNullable(Booking.builder()
                        .id(1)
                        .item(Item.builder()
                                .id(1)
                                .owner(User.builder()
                                        .id(1)
                                        .build())
                                .build())
                        .booker(User.builder()
                                .id(2)
                                .build())
                        .status(BookingStatus.WAITING)
                        .build()));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocationOnMock -> Booking.builder()
                        .id(1)
                        .start(invocationOnMock.getArgument(0, Booking.class).getStart())
                        .end(invocationOnMock.getArgument(0, Booking.class).getEnd())
                        .item(Item.builder()
                                .id(invocationOnMock.getArgument(0, Booking.class).getItem().getId())
                                .build())
                        .booker(User.builder()
                                .id(invocationOnMock.getArgument(0, Booking.class).getBooker().getId())
                                .build())
                        .status(invocationOnMock.getArgument(0, Booking.class).getStatus())
                        .build());

        BookingReturnDto bookingReturnDto = bookingService.changeBookingStatus(1, true, 1);

        assertThat(bookingReturnDto, hasProperty("status", is(BookingStatus.APPROVED)));

        verify(bookingRepository, times(1))
                .save(any(Booking.class));
    }

    @Test
    void changeBookingStatus_whenRejected_thenReturnBookingWithRejectedStatus() {
         when(bookingRepository.findById(1))
                .thenReturn(Optional.ofNullable(Booking.builder()
                        .id(1)
                        .item(Item.builder()
                                .id(1)
                                .owner(User.builder()
                                        .id(1)
                                        .build())
                                .build())
                        .booker(User.builder()
                                .id(2)
                                .build())
                        .status(BookingStatus.WAITING)
                        .build()));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocationOnMock -> Booking.builder()
                        .id(1)
                        .start(invocationOnMock.getArgument(0, Booking.class).getStart())
                        .end(invocationOnMock.getArgument(0, Booking.class).getEnd())
                        .item(Item.builder()
                                .id(invocationOnMock.getArgument(0, Booking.class).getItem().getId())
                                .build())
                        .booker(User.builder()
                                .id(invocationOnMock.getArgument(0, Booking.class).getBooker().getId())
                                .build())
                        .status(invocationOnMock.getArgument(0, Booking.class).getStatus())
                        .build());

        BookingReturnDto bookingReturnDto = bookingService.changeBookingStatus(1, false, 1);
        assertThat(bookingReturnDto, hasProperty("status", is(BookingStatus.REJECTED)));

        verify(bookingRepository, times(1))
                .save(any(Booking.class));
    }

    @Test
    void findBookingById_whenFound_thenReturnBooking() {
        when(bookingRepository.findById(1))
                .thenReturn(Optional.ofNullable(Booking.builder()
                        .id(1)
                        .item(Item.builder()
                                .id(1)
                                .owner(User.builder()
                                        .id(1)
                                        .build())
                                .build())
                        .booker(User.builder()
                                .id(2)
                                .build())
                        .status(BookingStatus.WAITING)
                        .build()));

        BookingReturnDto bookingReturnDto = bookingService.findBookingById(1, 1);
        assertThat(bookingReturnDto, hasProperty("id", is(1)));

        verify(bookingRepository, times(1))
                .findById(1);
    }

    @Test
    void findBookingById_whenNotFound_thenThrowNotFoundBookingException() {
        assertThrows(NotFoundBookingException.class, () -> bookingService.findBookingById(99, 1));
    }

    @Test
    void findBookingById_whenUserNotBookerNotOwner_thenThrowAccessDeniedException() {
        when(bookingRepository.findById(1))
                .thenReturn(Optional.ofNullable(Booking.builder()
                        .id(1)
                        .item(Item.builder()
                                .id(1)
                                .owner(User.builder()
                                        .id(1)
                                        .build())
                                .build())
                        .booker(User.builder()
                                .id(2)
                                .build())
                        .status(BookingStatus.WAITING)
                        .build()));

        assertThrows(AccessDeniedException.class, () -> bookingService.findBookingById(1, 3));
    }
}