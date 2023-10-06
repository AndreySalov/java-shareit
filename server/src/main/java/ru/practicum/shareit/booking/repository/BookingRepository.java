package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // поиск по заказчику
    List<Booking> findBookingsByBookerId(Long bookerId, Pageable page);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start,
                                                             LocalDateTime end, Pageable page);

    List<Booking> findAllByBookerIdAndEndBeforeAndStatus(Long userId, LocalDateTime dateTime,
                                                         BookingStatus status, Pageable page);

    List<Booking> findAllByBookerIdAndStartAfter(long userId, LocalDateTime dateTime, Pageable page);

    List<Booking> findAllByBookerIdAndStatus(long userId, BookingStatus status, Pageable page);

    // поиск по хозяину вещи
    List<Booking> findAllByItemUserId(Long ownerId, Pageable page);

    List<Booking> findAllByItemUserIdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime start,
                                                               LocalDateTime end, Pageable page);

    List<Booking> findAllByItemUserIdAndEndBeforeAndStatus(Long ownerId, LocalDateTime end,
                                                           BookingStatus status, Pageable page);

    List<Booking> findAllByItemUserIdAndStartAfter(Long ownerId, LocalDateTime start,
                                                   Pageable page);

    List<Booking> findAllByItemUserIdAndStatus(Long ownerId, BookingStatus status, Pageable page);

    // Поиск last и next booking для item
    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatus(Long itemId, LocalDateTime start,
                                                               BookingStatus status, Sort sort);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatus(Long itemId, LocalDateTime start,
                                                              BookingStatus status, Sort sort);

    List<Booking> findAllByItemUserIdAndItemIdInAndStartBefore(Long userId, List<Long> itemIds,
                                                               LocalDateTime start, Sort sort);

    List<Booking> findAllByItemUserIdAndItemIdInAndStartAfter(Long itemId, List<Long> itemIds,
                                                              LocalDateTime start, Sort sort);

    List<Booking> findAllByItemIdAndBookerIdAndStatusAndStartBeforeAndEndBefore(Long itemId, Long bookerId,
                                                                                BookingStatus status,
                                                                                LocalDateTime start,
                                                                                LocalDateTime end);


}