package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoDated;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceWithContextTest {

    private final EntityManager em;

    private final ItemService itemService;

    private final UserService userService;

    private final BookingService bookingService;

    private ItemDto itemDto;
    private BookingDtoIn bookingLastDtoIn;
    private BookingDtoIn bookingNextDtoIn;
    private UserDto userDto;
    private CommentDtoIn comment;
    private Item item;
    private User user;

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDto(1L, "Вещь 1", "Описание вещи 1", true, null);
        bookingLastDtoIn = new BookingDtoIn(1L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(5), 1L);
        bookingNextDtoIn = new BookingDtoIn(2L, LocalDateTime.now().plusHours(12),
                LocalDateTime.now().plusDays(1), 1L);
        userDto = new UserDto(1L, "Иван Иванович", "ii@mail.ru");
        user = new User(1L, "Иван Иванович", "ii@mail.ru");
        item = new Item(1L, "Вещь 1", "Описание вещи 1", true, user, null);
        comment = new CommentDtoIn(1L, "Коммент 1");

        userService.createUser(userDto);
    }

    @Test
    void shouldSearch() {
        itemService.createItem(userDto.getId(), itemDto);
        int from = 0;
        int size = 5;
        String text = "ещ";
        List<ItemDto> itemsList = itemService.search(text, from, size);
        TypedQuery<Item> query = em.createQuery("select i from Item i " +
                "where upper(i.name) like upper(concat('%', :text, '%')) " +
                "or upper(i.description) like upper(concat('%', :text, '%')) " +
                "and i.isAvailable = true ", Item.class);
        List<Item> itemsFromDb = query.setParameter("text", text)
                .getResultList();

        assertThat(itemsList.size(), equalTo(itemsFromDb.size()));
        assertThat(itemsList.get(0).getId(), equalTo(itemsFromDb.get(0).getId()));
        assertThat(itemsList.get(0).getName(), equalTo(itemsFromDb.get(0).getName()));
        assertThat(itemsList.get(0).getDescription(), equalTo(itemsFromDb.get(0).getDescription()));
        assertThat(itemsList.get(0).getAvailable(), equalTo(itemsFromDb.get(0).getIsAvailable()));
    }

    @Test
    void shouldSearchForNoItems() {
        itemService.createItem(userDto.getId(), itemDto);
        int from = 0;
        int size = 5;
        String text = "вещьстакойстрокойненайти";
        List<ItemDto> itemsList = itemService.search(text, from, size);

        assertThat(itemsList, empty());
    }
}
