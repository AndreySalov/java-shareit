package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.exception.BadParameterException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService implements IBookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public BookingDtoOut saveBooking(long userId, BookingDtoIn bookingDto) {
        User user = checkUser(userId);
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмета с ID " + bookingDto.getItemId()
                        + " не зарегистрировано"));
        if (!item.getIsAvailable()) {
            throw new BadParameterException("вещь недоступна для аренды");
        }
        if (userId == (item.getUser().getId())) {
            throw new NotFoundException("ошибка: запрос аренды отправлен от владельца вещи");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, user, item);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }


    @Transactional
    @Override
    public BookingDtoOut bookingApprove(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Запроса на аренду с ID "
                        + bookingId + " не зарегистрировано"));
        if (booking.getItem().getUser().getId() != userId) {
            throw new NotFoundException("У пользователя с ID " + userId + " нет запроса на аренду с ID " + bookingId);
        }
        if (booking.getStatus() == BookingStatus.WAITING) {
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
        } else {
            throw new BadParameterException("У запроса на аренду с ID " + bookingId +
                    " нельзя поменять статус. Текущий статус: " + booking.getStatus());
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDtoOut findBookingById(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Запроса на аренду с ID "
                        + bookingId + " не зарегистрировано"));
        if (booking.getBooker().getId() != userId) {
            if (booking.getItem().getUser().getId() != userId) {
                throw new NotFoundException("Пользователь " + userId + " не создавал бронь с ID " + bookingId +
                        " и не является владельцем вещи " + booking.getItem().getId());
            }
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDtoOut> findUserBookings(long userId, BookingState state) {
        User user = checkUser(userId);
        switch (state) {
            case ALL:
                return bookingRepository.findByBookerIdOrderByEndDesc(userId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(userId, LocalDateTime.now(), LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByBookerIdAndEndIsAfter(userId, LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findByBookerIdAndStatusOrderByEndDesc(userId, BookingStatus.WAITING).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatusOrderByEndDesc(userId, BookingStatus.REJECTED).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                throw new NotFoundException("Некорректно выбран тип бронирования" + state);
        }
    }

    @Override
    public List<BookingDtoOut> findOwnerBookings(long userId, BookingState state) {
        User user = checkUser(userId);
        if (itemRepository.findAllItemsByUserIdOrderById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь " + userId + " не является хозяином ни одной вещи");
        }
        switch (state) {
            case ALL:
                return bookingRepository.findByOwnerIdOrderByStartDesc(userId,
                                Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentByOwnerId(userId, LocalDateTime.now(), Sort.by(Sort.Direction.ASC, "start")).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllPastByOwnerId(userId, LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllFutureByOwnerId(userId, LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllWaitingByOwnerId(userId,
                                Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllRejectedByOwnerId(userId,
                                Sort.by(Sort.Direction.DESC, "start")).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                throw new NotFoundException("Некорректно выбран тип бронирования" + state);
        }
    }

    private User checkUser(long userId) {
        checkId(userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
    }

    private void checkId(long userId) {
        if (userId <= 0) {
            throw new BadParameterException("id must be positive");
        }
    }
}
