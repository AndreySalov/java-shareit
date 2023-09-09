package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemOwnershipException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    public List<ItemDto> getAllItemsByUser(Long userId) {
        userService.getUserById(userId);
        List<ItemDto> itemDto = new ArrayList<>();
        List<Item> items = itemRepository.getAllItemsByUser(userId);
        for (Item item : items)
            itemDto.add(ItemMapper.toItemDto(item));
        return itemDto;
    }

    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(itemRepository.getItemById(itemId).orElseThrow(() ->
                new ItemNotFoundException(itemId)));
    }

    public ItemDto createItem(ItemDto itemDto, Long userId) {
        UserDto userDto = userService.getUserById(userId);
        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto, user);
        return ItemMapper.toItemDto(itemRepository.createItem(item, user));
    }

    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        User user = UserMapper.toUser(userService.getUserById(userId));
        Item item = itemRepository.getItemById(itemId).orElseThrow(() ->
                new ItemNotFoundException(itemId));
        checkItemOwnership(user, item);
        item = ItemMapper.toItem(itemDto, user);
        return ItemMapper.toItemDto(itemRepository.updateItem(itemId, item, user));
    }

    public List<ItemDto> findItems(String text, Long userId) {
        if (text.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<ItemDto> itemDto = new ArrayList<>();
        List<Item> items = itemRepository.findItems(text);
        for (Item item : items)
            itemDto.add(ItemMapper.toItemDto(item));
        return itemDto;
    }

    private void checkItemOwnership(User user, Item item) {
        if (!item.getOwner().equals(user)) {
            throw new ItemOwnershipException("Пользователь с id = " + user.getId() +
                    " не является владельцем вещи c id = " + item.getId());
        }
    }
}

