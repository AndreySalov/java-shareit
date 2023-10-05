package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {

    public static Request toItemRequest(RequestDtoIn requestDto, User user) {
        return new Request(
                requestDto.getId(),
                requestDto.getDescription(),
                user,
                LocalDateTime.now()
        );
        //return Request.builder().id(requestDto.getId()).description(requestDto.getDescription()).user(user).build();
    }

    public static RequestDtoOut toItemRequestDto(Request request, List<ItemDto> items) {
        return new RequestDtoOut(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                items != null ? items : Collections.emptyList()
        );
    }
}
