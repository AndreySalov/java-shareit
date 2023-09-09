package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllItemsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemDto> items = itemService.getAllItemsByUser(userId);
        return items;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        ItemDto item = itemService.getItemById(itemId);
        return item;
    }

    @PostMapping
    @Validated
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemDto createdItem = itemService.createItem(itemDto, userId);
        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId, @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemDto updatedItem = itemService.updateItem(itemId, itemDto, userId);
        return updatedItem;
    }

    @GetMapping("/search")
    public List<ItemDto> findItems(@RequestParam String text,
                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemDto> items = itemService.findItems(text, userId);
        return items;
    }
}