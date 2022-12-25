package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBooker_Id(int bookerId, Pageable pageable);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(int bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByBooker_IdAndEndIsBefore(int bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findByBooker_IdAndStartIsAfter(int bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findByBooker_IdAndStatus(int bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findByItem_Owner_Id(int userId, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(int userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndEndIsBefore(int userId, LocalDateTime end, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStartIsAfter(int userId, LocalDateTime end, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStatus(int userId, BookingStatus status, Pageable pageable);

    Optional<Booking> findTop1ByItem_idAndStartIsBefore(int itemId, LocalDateTime end, Sort sort);

    Optional<Booking> findTop1ByItem_idAndStartIsAfter(int itemId, LocalDateTime end, Sort sort);

    Optional<Booking> findTop1ByBooker_IdAndItem_IdAndEndIsBeforeAndStatus(int userId, int itemId, LocalDateTime end, BookingStatus status);
}
