package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserEmailValidationException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        List<UserDto> dtoList = new ArrayList<>();
        for (User user : userRepository.getAllUsers())
            dtoList.add(UserMapper.toUserDto(user));
        return dtoList;
    }

    public UserDto getUserById(Long userId) {
        try {
            Optional<User> user = userRepository.getUserById(userId);
            return UserMapper.toUserDto(user.orElseThrow(() -> new UserNotFoundException(userId)));
        } catch (NullPointerException e) {
            throw new UserNotFoundException(userId);
        }
    }

    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        if (userRepository.isEmailExist(user.getEmail()))
            throw new UserEmailValidationException();
        return UserMapper.toUserDto(userRepository.createUser(user));
    }

    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.getUserById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        if (userDto.getEmail() == null)
            userDto.setEmail(user.getEmail());
        if (userDto.getName() == null)
            userDto.setName(user.getName());
        //проверим, что Email изменился и проверм на уинкальность.
        if (!user.getEmail().equals(userDto.getEmail()))
            if (userRepository.isEmailExist(userDto.getEmail()))
                throw new UserEmailValidationException();
        user = UserMapper.toUser(userDto);
        user.setId(userId);
        return UserMapper.toUserDto(userRepository.updateUser(userId, user));
    }

    public boolean deleteUserById(Long userId) {
        return userRepository.deleteUserById(userId);
    }
}

