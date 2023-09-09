package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAllUsers();

    Optional<User> getUserById(Long userId);

    User createUser(User user);

    User updateUser(Long userId, User user);

    boolean deleteUserById(Long userId);

    Optional<User> isUserExist(User user);

    Boolean isEmailExist(String email);
}
