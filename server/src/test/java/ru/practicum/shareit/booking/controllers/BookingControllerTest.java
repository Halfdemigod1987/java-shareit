package ru.practicum.shareit.booking.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReturnDto;
import ru.practicum.shareit.booking.exceptions.NotFoundBookingException;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.services.BookingService;
import ru.practicum.shareit.item.dto.ItemReturnDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void createBooking_whenInvoked_thenResponseStatusIsCreatedWithBookingInBody() throws Exception {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .itemId(1)
                .build();

        when(bookingService.createBooking(any(), anyInt()))
                .thenAnswer(invocationOnMock -> BookingReturnDto.builder()
                        .id(1)
                        .start(invocationOnMock.getArgument(0, BookingDto.class).getStart())
                        .end(invocationOnMock.getArgument(0, BookingDto.class).getEnd())
                        .item(ItemReturnDto.builder()
                                .id(1)
                                .build())
                        .booker(UserDto.builder()
                                .id(invocationOnMock.getArgument(1, Integer.class))
                                .build())
                        .status(BookingStatus.WAITING)
                        .build());

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.start", containsString(bookingDto.getStart().toLocalDate().toString())))
                .andExpect(jsonPath("$.end", containsString(bookingDto.getEnd().toLocalDate().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItemId())));

        verify(bookingService, times(1))
                .createBooking(any(), anyInt());
    }

    @Test
    void changeBookingStatus_whenInvoked_thenResponseStatusIsOkWithBookingInBody() throws Exception {
        when(bookingService.changeBookingStatus(anyInt(), anyBoolean(), anyInt()))
                .thenAnswer(invocationOnMock -> BookingReturnDto.builder()
                        .id(1)
                        .status(invocationOnMock.getArgument(1, Boolean.class)
                                ? BookingStatus.APPROVED
                                : BookingStatus.REJECTED)
                        .build());

        mvc.perform(patch("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));

        mvc.perform(patch("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", "false")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("REJECTED")));

        verify(bookingService, times(2))
                .changeBookingStatus(anyInt(), anyBoolean(), anyInt());
    }

    @Test
    void findAllBookings_whenInvoked_thenResponseStatusIsOkWithBookingCollectionInBody() throws Exception {
        when(bookingService.findAllBookings("ALL", 1, null, null))
                .thenReturn(List.of(
                        BookingReturnDto.builder()
                                .id(1)
                                .build(),
                        BookingReturnDto.builder()
                                .id(2)
                                .build(),
                        BookingReturnDto.builder()
                                .id(3)
                                .build()));

        when(bookingService.findAllBookings("ALL",1, 0, 2))
                .thenReturn(List.of(
                        BookingReturnDto.builder()
                                .id(1)
                                .build(),
                        BookingReturnDto.builder()
                                .id(2)
                                .build()));

        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[*].id", hasItem(is(1))))
                .andExpect(jsonPath("$.[*].id", hasItem(is(2))))
                .andExpect(jsonPath("$.[*].id", hasItem(is(3))));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[*].id", hasItem(is(1))))
                .andExpect(jsonPath("$.[*].id", hasItem(is(2))));

        verify(bookingService, times(1))
                .findAllBookings("ALL", 1, null, null);
        verify(bookingService, times(1))
                .findAllBookings("ALL", 1, 0, 2);
    }

    @Test
    void findAllOwnerBookings_whenInvoked_thenResponseStatusIsOkWithBookingCollectionInBody() throws Exception {
        when(bookingService.findAllOwnerBookings("ALL", 1, null, null))
                .thenReturn(List.of(
                        BookingReturnDto.builder()
                                .id(1)
                                .build(),
                        BookingReturnDto.builder()
                                .id(2)
                                .build(),
                        BookingReturnDto.builder()
                                .id(3)
                                .build()));

        when(bookingService.findAllOwnerBookings("ALL",1, 0, 2))
                .thenReturn(List.of(
                        BookingReturnDto.builder()
                                .id(1)
                                .build(),
                        BookingReturnDto.builder()
                                .id(2)
                                .build()));

        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[*].id", hasItem(is(1))))
                .andExpect(jsonPath("$.[*].id", hasItem(is(2))))
                .andExpect(jsonPath("$.[*].id", hasItem(is(3))));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[*].id", hasItem(is(1))))
                .andExpect(jsonPath("$.[*].id", hasItem(is(2))));

        verify(bookingService, times(1))
                .findAllOwnerBookings("ALL", 1, null, null);
        verify(bookingService, times(1))
                .findAllOwnerBookings("ALL", 1, 0, 2);
    }

    @Test
    void findBookingById_whenFound_thenResponseStatusIsOkWithBookingInBody() throws Exception {
        when(bookingService.findBookingById(anyInt(), anyInt()))
                .thenAnswer(invocationOnMock -> BookingReturnDto.builder()
                        .id(invocationOnMock.getArgument(0, Integer.class))
                        .start(LocalDateTime.now().plusDays(2))
                        .end(LocalDateTime.now().plusDays(3))
                        .booker(UserDto.builder()
                                .id(invocationOnMock.getArgument(1, Integer.class))
                                .build())
                        .status(BookingStatus.APPROVED)
                        .build());

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", containsString(LocalDateTime.now().plusDays(2).toLocalDate().toString())))
                .andExpect(jsonPath("$.end", containsString(LocalDateTime.now().plusDays(3).toLocalDate().toString())))
                .andExpect(jsonPath("$.booker.id", is(1)))
                .andExpect(jsonPath("$.status", is("APPROVED")));

        verify(bookingService, times(1))
                .findBookingById(anyInt(), anyInt());
    }

    @Test
    void findBookingById_whenNotFound_thenThrowNotFoundBookingException() throws Exception {
        when(bookingService.findBookingById(anyInt(), anyInt()))
                .thenThrow(new NotFoundBookingException(""));

        mvc.perform(get("/bookings/99")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1))
                .findBookingById(anyInt(), anyInt());
    }
}