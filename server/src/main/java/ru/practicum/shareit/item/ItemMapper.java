package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoDated;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;

import java.util.List;

@UtilityClass
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static ItemDtoDated toItemDto(Item item,
                                         BookingDtoForItem lastBooking,
                                         BookingDtoForItem nextBooking,
                                         List<CommentDtoOut> comments) {
        return new ItemDtoDated(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                lastBooking,
                nextBooking,
                comments
        );
    }

    public static Item toItem(ItemDto itemDto, User user, Request request) {
        return new Item(
                itemDto.getId(),
                (itemDto.getName() != null && !itemDto.getName().isBlank()) ? itemDto.getName() : null,
                (itemDto.getDescription() != null && !itemDto.getDescription().isBlank())
                        ? itemDto.getDescription() : null,
                itemDto.getAvailable() != null ? itemDto.getAvailable() : null,
                user,
                request
        );
    }

    public static Item toItem(ItemDto itemDto, Item item, Request request) {
        return new Item(
                itemDto.getId(),
                (itemDto.getName() != null && !itemDto.getName().isBlank()) ? itemDto.getName() : item.getName(),
                (itemDto.getDescription() != null && !itemDto.getDescription().isBlank())
                        ? itemDto.getDescription() : item.getDescription(),
                itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getIsAvailable(),
                item.getUser(),
                request
        );
    }
}
