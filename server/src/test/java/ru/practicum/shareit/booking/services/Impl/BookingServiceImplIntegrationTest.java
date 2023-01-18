package ru.practicum.shareit.booking.services.Impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingReturnDto;
import ru.practicum.shareit.booking.exceptions.UnsupportedBookingStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.services.BookingService;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void initiate() {
        User user1 = userRepository.save(User.builder()
                .name("email1@test.com")
                .email("name1")
                .build());
        User user2 = userRepository.save(User.builder()
                .name("email2@test.com")
                .email("name2")
                .build());

        Item item1 = itemRepository.save(Item.builder()
                .name("name1")
                .description("description1")
                .available(true)
                .owner(user1)
                .build());
        Item item2 = itemRepository.save(Item.builder()
                .name("name2")
                .description("description2")
                .available(true)
                .owner(user1)
                .build());
        Item item3 = itemRepository.save(Item.builder()
                .name("name3")
                .description("description3")
                .available(true)
                .owner(user2)
                .build());
        Item item4 = itemRepository.save(Item.builder()
                .name("name4")
                .description("description4")
                .available(true)
                .owner(user1)
                .build());
        Item item5 = itemRepository.save(Item.builder()
                .name("name5")
                .description("description5")
                .available(true)
                .owner(user2)
                .build());
        Item item6 = itemRepository.save(Item.builder()
                .name("name6")
                .description("description6")
                .available(true)
                .owner(user2)
                .build());

        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .item(item1)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build());
        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(2))
                .item(item2)
                .booker(user2)
                .status(BookingStatus.APPROVED)
                .build());
        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .item(item3)
                .booker(user1)
                .status(BookingStatus.WAITING)
                .build());
        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item4)
                .booker(user2)
                .status(BookingStatus.REJECTED)
                .build());
        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(2))
                .item(item5)
                .booker(user1)
                .status(BookingStatus.APPROVED)
                .build());
        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item6)
                .booker(user1)
                .status(BookingStatus.REJECTED)
                .build());
    }

    @AfterEach
    void delete() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllBookings_whenInvokedWithAllState_thenReturnBookingCollectionWithBooker() {

        List<User> users = userRepository.findAll();

        List<BookingReturnDto> bookings = bookingService.findAllBookings("ALL", users.get(0).getId(), null, null);

        assertThat(bookings, hasSize(3));
        bookings.forEach(
                booking -> assertThat(booking.getBooker(), hasProperty("id", is(users.get(0).getId()))));

        bookings = bookingService.findAllBookings("ALL", users.get(0).getId(), 0, 2);

        assertThat(bookings, hasSize(2));
        bookings.forEach(
                booking -> assertThat(booking.getBooker(), hasProperty("id", is(users.get(0).getId()))));

    }

    @Test
    void findAllBookings_whenInvokedWithCurrentState_thenReturnCurrentBookingCollection() {

        List<User> users = userRepository.findAll();

        List<BookingReturnDto> bookings = bookingService.findAllBookings("CURRENT", users.get(0).getId(), null, null);

        assertThat(bookings, hasSize(1));
        bookings.forEach(
                booking -> {
                    assertThat(booking, hasProperty("start", lessThanOrEqualTo(LocalDateTime.now())));
                    assertThat(booking, hasProperty("end", greaterThanOrEqualTo(LocalDateTime.now())));
                });
    }

    @Test
    void findAllBookings_whenInvokedWithPastState_thenReturnPastBookingCollection() {

        List<User> users = userRepository.findAll();

        List<BookingReturnDto> bookings = bookingService.findAllBookings("PAST", users.get(0).getId(), null, null);

        assertThat(bookings, hasSize(1));
        bookings.forEach(
                booking -> assertThat(booking, hasProperty("end", lessThanOrEqualTo(LocalDateTime.now()))));
    }

    @Test
    void findAllBookings_whenInvokedWithFutureState_thenReturnFutureBookingCollection() {

        List<User> users = userRepository.findAll();

        List<BookingReturnDto> bookings = bookingService.findAllBookings("FUTURE", users.get(0).getId(), null, null);

        assertThat(bookings, hasSize(1));
        bookings.forEach(
                booking -> assertThat(booking, hasProperty("start", greaterThanOrEqualTo(LocalDateTime.now()))));
    }

    @Test
    void findAllBookings_whenInvokedWithWaitingState_thenReturnWaitingBookingCollection() {

        List<User> users = userRepository.findAll();

        List<BookingReturnDto> bookings = bookingService.findAllBookings("WAITING", users.get(0).getId(), null, null);

        assertThat(bookings, hasSize(1));
        bookings.forEach(
                booking -> assertThat(booking, hasProperty("status", is(BookingStatus.WAITING))));
    }

    @Test
    void findAllBookings_whenInvokedWithRejectedState_thenReturnRejectedBookingCollection() {

        List<User> users = userRepository.findAll();

        List<BookingReturnDto> bookings = bookingService.findAllBookings("REJECTED", users.get(0).getId(), null, null);

        assertThat(bookings, hasSize(1));
        bookings.forEach(
                booking -> assertThat(booking, hasProperty("status", is(BookingStatus.REJECTED))));
    }

    @Test
    void findAllBookings_whenInvokedWithUnsupportedState_thenThrowUnsupportedBookingStatusException() {

        List<User> users = userRepository.findAll();

        assertThrows(UnsupportedBookingStatusException.class,
                () -> bookingService.findAllBookings("NEW", users.get(0).getId(), null, null));
    }

    @Test
    void findAllOwnerBookings_whenInvokedWithAllState_thenReturnBookingCollection() {
        List<User> users = userRepository.findAll();

        List<BookingReturnDto> bookings = bookingService.findAllOwnerBookings("ALL", users.get(0).getId(), null, null);

        assertThat(bookings, hasSize(3));
        bookings.forEach(
                booking -> assertThat(booking.getItem(), hasProperty("ownerId", is(users.get(0).getId()))));

        bookings = bookingService.findAllOwnerBookings("ALL", users.get(0).getId(), 0, 2);

        assertThat(bookings, hasSize(2));
        bookings.forEach(
                booking -> assertThat(booking.getItem(), hasProperty("ownerId", is(users.get(0).getId()))));
    }

    @Test
    void findAllOwnerBookings_whenInvokedWithCurrentState_thenReturnCurrentBookingCollection() {

        List<User> users = userRepository.findAll();

        List<BookingReturnDto> bookings = bookingService.findAllOwnerBookings("CURRENT", users.get(0).getId(), null, null);

        assertThat(bookings, hasSize(1));
        bookings.forEach(
                booking -> {
                    assertThat(booking, hasProperty("start", lessThanOrEqualTo(LocalDateTime.now())));
                    assertThat(booking, hasProperty("end", greaterThanOrEqualTo(LocalDateTime.now())));
                });
    }

    @Test
    void findAllOwnerBookings_whenInvokedWithPastState_thenReturnPastBookingCollection() {

        List<User> users = userRepository.findAll();

        List<BookingReturnDto> bookings = bookingService.findAllOwnerBookings("PAST", users.get(0).getId(), null, null);

        assertThat(bookings, hasSize(1));
        bookings.forEach(
                booking -> assertThat(booking, hasProperty("end", lessThanOrEqualTo(LocalDateTime.now()))));
    }

    @Test
    void findAllOwnerBookings_whenInvokedWithFutureState_thenReturnFutureBookingCollection() {

        List<User> users = userRepository.findAll();

        List<BookingReturnDto> bookings = bookingService.findAllOwnerBookings("FUTURE", users.get(0).getId(), null, null);

        assertThat(bookings, hasSize(1));
        bookings.forEach(
                booking -> assertThat(booking, hasProperty("start", greaterThanOrEqualTo(LocalDateTime.now()))));
    }

    @Test
    void findAllOwnerBookings_whenInvokedWithWaitingState_thenReturnWaitingBookingCollection() {

        List<User> users = userRepository.findAll();

        List<BookingReturnDto> bookings = bookingService.findAllOwnerBookings("WAITING", users.get(0).getId(), null, null);

        assertThat(bookings, hasSize(1));
        bookings.forEach(
                booking -> assertThat(booking, hasProperty("status", is(BookingStatus.WAITING))));
    }

    @Test
    void findAllOwnerBookings_whenInvokedWithRejectedState_thenReturnRejectedBookingCollection() {

        List<User> users = userRepository.findAll();

        List<BookingReturnDto> bookings = bookingService.findAllOwnerBookings("REJECTED", users.get(0).getId(), null, null);

        assertThat(bookings, hasSize(1));
        bookings.forEach(
                booking -> assertThat(booking, hasProperty("status", is(BookingStatus.REJECTED))));
    }

    @Test
    void findAllOwnerBookings_whenInvokedWithUnsupportedState_thenThrowUnsupportedBookingStatusException() {

        List<User> users = userRepository.findAll();

        assertThrows(UnsupportedBookingStatusException.class,
                () -> bookingService.findAllOwnerBookings("NEW", users.get(0).getId(), null, null));
    }
}