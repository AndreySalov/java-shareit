package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService implements IBookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDtoOut saveBooking(long userId, BookingDtoIn bookingDto, BookingStatus status) {
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
        booking.setStatus(status);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoOut bookingApprove(long userId, long bookingId, boolean approved) {
        checkUser(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Запроса на аренду с ID "
                        + bookingId + " не зарегистрировано"));
        if (booking.getItem().getUser().getId() != userId) {
            throw new NotFoundException("Пользователь ID " + userId + " не является владельцем вещи с ID "
                    + booking.getItem().getId() + " и не может менять одобрить/отклонить запрос на аренду этой вещи");
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
    public List<BookingDtoOut> findUserBookings(long userId, BookingState state, int from, int size) {
        checkUser(userId);

        Pageable page = PageRequest.of(from / size, size , Sort.by(Sort.Direction.DESC, "start"));

        List<BookingDtoOut> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findBookingsByBookerId(userId, page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId,
                                LocalDateTime.now(), LocalDateTime.now(), page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeAndStatus(userId,
                                LocalDateTime.now(), BookingStatus.APPROVED, page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(userId,
                                LocalDateTime.now(), page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING,
                                page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED,
                                page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            default:
                throw new NotFoundException("Некорректно выбран тип бронирования" + state);
        }
        return bookings;
    }

    @Override
    public List<BookingDtoOut> findOwnerBookings(long userId, BookingState state, int from, int size) {
        checkUser(userId);

        Pageable page = PageRequest.of(from / size, size , Sort.by(Sort.Direction.DESC , "start"));

        List<BookingDtoOut> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemUserId(userId, page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemUserIdAndStartBeforeAndEndAfter(userId,
                                LocalDateTime.now(), LocalDateTime.now(), page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemUserIdAndEndBeforeAndStatus(userId,
                                LocalDateTime.now(), BookingStatus.APPROVED, page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemUserIdAndStartAfter(userId,
                                LocalDateTime.now(), page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemUserIdAndStatus(userId, BookingStatus.WAITING,
                                page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemUserIdAndStatus(userId,
                                BookingStatus.REJECTED, page)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            default:
                throw new NotFoundException("Некорректно выбран тип бронирования" + state);
        }
        return bookings;
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