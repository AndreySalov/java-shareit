package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserServiceTest {

    private final EntityManager em;
    private final UserService service;


    @Test
    void shouldUpdateUserWithWrongId() {
        UserDto userDtoForUpdate = makeUserDto("Петр Петрович", "pp@mail.ru");
        try {
            service.updateUser(userDtoForUpdate, 2L);
        } catch (NotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с ID " + 2L + " не зарегистрирован"));
        }
    }


    @Test
    void shouldGetUserByIdWithWrongUserId() {
        try {
            service.getUserById(2L);
        } catch (NotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с ID " + 2L + " не зарегистрирован"));
        }
    }


    @Test
    void shouldDeleteUserWithWrongId() {
        try {
            service.deleteUser(2L);
        } catch (NotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с ID " + 2L + " не зарегистрирован"));
        }
    }


    private UserDto makeUserDto(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        return userDto;
    }
}
