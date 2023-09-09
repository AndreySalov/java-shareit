package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, List<Item>> items = new HashMap<>();
    private long itemId = 0;

    @Override
    public List<Item> getAllItemsByUser(Long userId) {
        return new ArrayList<>(items.get(userId));
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getId().equals(itemId))
                .findFirst();
    }

    @Override
    public Item createItem(Item item, User user) {
        itemId++;
        item.setId(itemId);
        List<Item> userItems = items.get(user.getId());
        if (userItems == null) {
            userItems = new ArrayList<>();
        }
        userItems.add(item);
        items.put(user.getId(), userItems);
        return item;
    }

    @Override
    public Item updateItem(Long itemId, Item updItem, User user) {
        Item oldItem = getItemById(itemId).get();
        if (updItem.getName() != null)
            oldItem.setName(updItem.getName());
        if (updItem.getDescription() != null)
            oldItem.setDescription(updItem.getDescription());
        if (updItem.getAvailable() != null)
            oldItem.setAvailable(updItem.getAvailable());
        return oldItem;
    }

    @Override
    public List<Item> findItems(String text) {
        text = text.toLowerCase();
        List<Item> itemsFind = new ArrayList<>();
        for (List<Item> itemList : items.values()) {
            for (Item item : itemList) {
                if ((item.getName().toLowerCase().contains(text) || item.getDescription().toLowerCase().contains(text))
                        && item.getAvailable()) {
                    itemsFind.add(item);
                }
            }
        }
        return itemsFind;
    }


}

