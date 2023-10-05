package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//@Sql(scripts = "classpath:data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class BookingRepositoryTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final Pageable pageable = PageRequest.of(0 / 10, 10, Sort.by(Sort.Direction.DESC, "start"));
    private final LocalDateTime dateTime = LocalDateTime.now();

    private final User user1 = new User(1L, "Иван Иванович", "ii@mail.ru");
    private final User user2 = new User(2L, "Петр Петрович", "pp@mail.ru");
    private final Item item1 = new Item(1L, "Вещь 1", "Описание вещи 1", true, user1, null);
    private final Booking bookingPast = new Booking(1L, dateTime.minusYears(10), dateTime.minusYears(9), item1, user2, BookingStatus.APPROVED);
    private final Booking bookingCurrent = new Booking(2L, dateTime.minusYears(5), dateTime.plusYears(5), item1, user2, BookingStatus.APPROVED);
    private final Booking bookingFuture = new Booking(3L, dateTime.plusYears(8), dateTime.plusYears(9), item1, user2, BookingStatus.WAITING);
    private final Booking bookingRejected = new Booking(4L, dateTime.plusYears(9), dateTime.plusYears(10), item1, user2, BookingStatus.REJECTED);

    @BeforeEach
    public void beforeEach() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        bookingRepository.save(bookingPast);
        bookingRepository.save(bookingCurrent);
        bookingRepository.save(bookingFuture);
        bookingRepository.save(bookingRejected);
    }

    @Test
    public void shouldGetAllbyUserId() {
        List<Booking> result = bookingRepository.findAllByItemUserId(user1.getId(), pageable);
        assertEquals(4, result.size());
        assertEquals(bookingRejected.getId(), result.get(0).getId());
        assertEquals(bookingFuture.getId(), result.get(1).getId());
        assertEquals(bookingCurrent.getId(), result.get(2).getId());
        assertEquals(bookingPast.getId(), result.get(3).getId());
    }

    @Test
    public void shouldGetEmpty() {
        List<Booking> result = bookingRepository.findAllByItemUserId(user2.getId(), pageable);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldGetCurrent() {
        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStartBeforeAndEndAfter(user2.getId(), dateTime,
                        dateTime, pageable);
        assertEquals(1, result.size());
        assertEquals(bookingCurrent.getId(), result.get(0).getId());
    }

    @Test
    public void shouldGetPast() {
        List<Booking> result = bookingRepository
                .findAllByBookerIdAndEndBeforeAndStatus(user2.getId(), dateTime,
                        BookingStatus.APPROVED, pageable);

        assertEquals(1, result.size());
        assertEquals(bookingPast.getId(), result.get(0).getId());
    }

    @Test
    public void shouldGetFuture() {
        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStartAfter(user2.getId(), dateTime, pageable);

        assertEquals(2, result.size());
        assertEquals(bookingRejected.getId(), result.get(0).getId());
        assertEquals(bookingFuture.getId(), result.get(1).getId());
    }

    @Test
    public void shouldGetWaiting() {
        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStatus(user2.getId(), BookingStatus.WAITING, pageable);

        assertEquals(1, result.size());
        assertEquals(bookingFuture.getId(), result.get(0).getId());
    }

    @Test
    public void shouldGetRejected() {
        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStatus(user2.getId(), BookingStatus.REJECTED, pageable);

        assertEquals(1, result.size());
        assertEquals(bookingRejected.getId(), result.get(0).getId());
    }

    @Test
    public void shouldGetAllByItemOwner() {
        List<Booking> result = bookingRepository.findBookingsByBookerId(user2.getId(), pageable);
        assertEquals(4, result.size());
        assertEquals(bookingRejected.getId(), result.get(0).getId());
        assertEquals(bookingFuture.getId(), result.get(1).getId());
        assertEquals(bookingCurrent.getId(), result.get(2).getId());
        assertEquals(bookingPast.getId(), result.get(3).getId());
    }

    @Test
    public void shouldGetFinishedBookings() {
        List<Booking> result = bookingRepository.findAllByItemIdAndBookerIdAndStatusAndStartBeforeAndEndBefore(
                item1.getId(), user2.getId(), BookingStatus.APPROVED, dateTime, dateTime);

        assertEquals(1, result.size());
        assertEquals(bookingPast.getId(), result.get(0).getId());
    }

    @Test
    public void shouldGetNextBookings() {
        Booking result = bookingRepository.findFirstByItemIdAndStartAfterAndStatus(
                item1.getId(), dateTime, BookingStatus.WAITING, Sort.unsorted()).orElse(null);

        assertEquals(bookingFuture.getId(), result.getId());
    }
}