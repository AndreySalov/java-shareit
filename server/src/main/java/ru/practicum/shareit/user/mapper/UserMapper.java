package ru.practicum.shareit.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

@UtilityClass
public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName() != null ? user.getName() : null,
                user.getEmail()
        );
    }

    public static User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName() != null && !userDto.getName().isBlank() ? userDto.getName() : null,
                userDto.getEmail() != null && !userDto.getEmail().isBlank() ? userDto.getEmail() : null
        );
    }

    public static User toUser(UserDto userDto, User user) {
        return new User(
                userDto.getId(),
                userDto.getName() != null && !userDto.getName().isBlank() ? userDto.getName() : user.getName(),
                userDto.getEmail() != null && !userDto.getEmail().isBlank() ? userDto.getEmail() : user.getEmail()
        );
    }
}
