package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDtoIn;
import ru.practicum.shareit.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoDated;

import java.util.List;

public interface IItemService {
    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, ItemDto itemDto, long itemId);

    ItemDtoDated getItemById(long userId, long itemId);

    public List<ItemDtoDated> getUserItems(long userId, int from, int size);

    public List<ItemDto> search(String text, int from, int size);

    CommentDtoOut saveComment(long userId, long itemId, CommentDtoIn comment);

    void deleteItem(long id);
}
