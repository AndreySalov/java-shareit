package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.IBookingService;
import ru.practicum.shareit.item.exception.BadParameterException;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;


@Slf4j
@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String USER_ID = "X-Sharer-User-Id";
    private final IBookingService bookingService;

    @PostMapping
    public BookingDtoOut saveBooking(
            @RequestHeader(USER_ID) long userId,
            @Valid @RequestBody BookingDtoIn bookingDto) {
        log.info("В метод saveBooking передан userId {}, bookingDto.itemId {}, bookingDto.start {}, bookingDto.end {}",
                userId, bookingDto.getItemId(), bookingDto.getStart(), bookingDto.getEnd());

        return bookingService.saveBooking(userId, bookingDto, BookingStatus.WAITING);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut bookingApprove(@RequestHeader(USER_ID) long userId,
                                        @PathVariable long bookingId,
                                        @RequestParam boolean approved) {
        log.info("В метод bookingApprove передан userId {}, bookingId {}, статус подтверждения {}",
                userId, bookingId, approved);

        return bookingService.bookingApprove(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut findBookingById(@RequestHeader(USER_ID) long userId, @PathVariable long bookingId) {
        log.info("В метод findBookingById передан userId {}, bookingId {}", userId, bookingId);

        return bookingService.findBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoOut> findUserBookings(@RequestHeader(USER_ID) long userId,
                                                @RequestParam(defaultValue = "all") String state,
                                                @RequestParam(defaultValue = "0") @Min(0) int from,
                                                @RequestParam(defaultValue = "20") @Positive int size) {
        log.info("В метод findUserBookings передан userId {}, статус бронирования для поиска {}," +
                " индекс первого элемента {},количество элементов на странице {}", userId, state, from, size);

        BookingState enumState = BookingState.from(state)
                .orElseThrow(() -> new BadParameterException("Unknown state: " + state));

        return bookingService.findUserBookings(userId, enumState, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> findOwnerBookings(@RequestHeader(USER_ID) long userId,
                                                 @RequestParam(defaultValue = "all") String state,
                                                 @RequestParam(defaultValue = "0") @Min(0) int from,
                                                 @RequestParam(defaultValue = "20") @Positive int size) {
        log.info("В метод findOwnerBookings передан userId {}, статус бронирования для поиска {}, " +
                "индекс первого элемента {}, количество элементов на странице {}", userId, state, from, size);

        BookingState enumState = BookingState.from(state)
                .orElseThrow(() -> new BadParameterException("Unknown state: " + state));

        return bookingService.findOwnerBookings(userId, enumState, from, size);
    }
}