package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RequestRepositoryTest {
    @Autowired
    private final RequestRepository requestRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ItemRepository itemRepository;
    private final Pageable pageable = PageRequest.of(0 / 10, 10);
    private final LocalDateTime dateTime = LocalDateTime.now();

    private final User user1 = new User(1L, "Иван Иванович", "ii@mail.ru");
    private final User user2 = new User(2L, "Петр Петрович", "pp@mail.ru");
    private final Item item1 = new Item(1L, "Вещь 1", "Описание вещи 1", true, user1, null);
    private final Request request1 = Request.builder().id(1L).description("Описание 1").user(user2).created(dateTime).build();

    @BeforeEach
    public void beforeEach() {
        userRepository.save(user1);
        userRepository.save(user2);
        requestRepository.save(request1);
        itemRepository.save(item1);
    }

    private void checkRequest(Request itemRequest, User user, LocalDateTime dateTime, Request resultRequest) {
        assertEquals(itemRequest.getId(), resultRequest.getId());
        assertEquals(itemRequest.getDescription(), resultRequest.getDescription());
        assertEquals(user.getId(), resultRequest.getUser().getId());
        assertEquals(user.getName(), resultRequest.getUser().getName());
        assertEquals(user.getEmail(), resultRequest.getUser().getEmail());
        assertEquals(dateTime, resultRequest.getCreated());
    }
}
