package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User userFromRepos = userRepository.save(user);
        return UserMapper.toUserDto(userFromRepos);
    }


    public UserDto updateUser(UserDto userDto, long userId) {
        checkId(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
        User userFromDto = UserMapper.toUser(userDto, user);
        userFromDto.setId(userId);
        return UserMapper.toUserDto(userRepository.save(userFromDto));
    }


    public UserDto getUserById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
        return UserMapper.toUserDto(user);
    }


    public void deleteUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
        userRepository.deleteById(userId);
    }


    public List<UserDto> getAllUsers() {
        List<User> usersList = userRepository.findAll();
        return usersList.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private void checkId(long userId) {
        if (userId <= 0) {
            throw new NotFoundException("id должно быть положительным.");
        }
    }
}