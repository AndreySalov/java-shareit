package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemOwnershipException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public List<ItemDto> getAllItemsByUser(Long userId) {
        userRepository.getUserById(userId).orElseThrow(() ->
                new UserNotFoundException(userId));
        return itemRepository.getAllItemsByUser(userId);
    }

    public ItemDto getItemById(Long itemId) {
        return itemRepository.getItemDtoById(itemId).orElseThrow(() ->
                new ItemNotFoundException(itemId));
    }

    public ItemDto createItem(ItemDto itemDto, Long userId) {
        return itemRepository.createItem(itemDto, userRepository.getUserById(userId).orElseThrow(() ->
                new UserNotFoundException(userId)));
    }

    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        User user = userRepository.getUserById(userId).orElseThrow(() ->
                new UserNotFoundException(userId));
        Item item = itemRepository.getItemById(itemId).orElseThrow(() ->
                new ItemNotFoundException(itemId));
        checkItemOwnership(user, item);
        return itemRepository.updateItem(itemId, itemDto, user);
    }

    public List<ItemDto> findItems(String text, Long userId) {
        if (text.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return itemRepository.findItems(text, userId);
    }

    private void checkItemOwnership(User user, Item item) {
        if (!item.getOwner().equals(user)) {
            throw new ItemOwnershipException("Пользователь с id = " + user.getId() +
                    " не является владельцем вещи c id = " + item.getId());
        }
    }
}

