package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.AdviceController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")

public class UserController extends AdviceController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return users;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        UserDto user = userService.getUserById(userId);
        return user;
    }

    @PostMapping
    @Validated
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        return createdUser;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateUser(userId, userDto);
        return updatedUser;
    }

    @DeleteMapping("/{userId}")
    public Boolean deleteUserById(@PathVariable Long userId) {
        Boolean deleted = userService.deleteUserById(userId);
        return deleted;
    }
}
