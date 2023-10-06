package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDtoOut;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoDated;
import ru.practicum.shareit.item.exception.BadParameterException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService implements IItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @Override
    public void deleteItem(long id) {
        checkId(id);
        itemRepository.deleteById(id);
    }

    @Transactional
    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User user = checkUser(userId);
        Request request = null;
        if (itemDto.getRequestId() != null) {
            request = requestRepository.findById(itemDto.getRequestId()).orElseThrow(() -> new NotFoundException("Запроса с ID " + itemDto.getRequestId() + " нет в базе"));
        }
        Item itemFromDto = ItemMapper.toItem(itemDto, user, request);
        Item item = itemRepository.save(itemFromDto);
        return ItemMapper.toItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        checkId(itemId);
        checkUser(userId);

        Item itemFromRep = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмета с ID " + itemId + " не зарегистрировано"));
        if (itemFromRep.getUser().getId() != userId) {
            throw new NotFoundException("Пользователь с ID " + userId + " не является владельцем вещи c ID " + itemId + ". Изменение запрещено");
        }
        Request request = null;
        if (itemDto.getRequestId() != null) {
            request = requestRepository.findById(itemDto.getRequestId()).orElseThrow(() -> new NotFoundException("Запроса с ID " + itemDto.getRequestId() + " нет в базе"));
        }
        Item item = ItemMapper.toItem(itemDto, itemFromRep, request);
        item.setId(itemId);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> search(String text, int from, int size) {
        if (text.isBlank()) {
            return List.of();
        }

        Pageable page = PageRequest.of(from / size, size);
        List<Item> itemsList = itemRepository.findByNameOrDescription(text, page);
        return itemsList.stream().map(ItemMapper::toItemDto).collect(toList());
    }

    @Override
    public ItemDtoDated getItemById(long userId, long itemId) {
        checkId(itemId);
        checkUser(userId);

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмета с ID " + itemId + " не зарегистрировано"));
        List<CommentDtoOut> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(itemId).stream().map(CommentMapper::toCommentDto).collect(toList());
        if (item.getUser().getId() != userId) {
            return ItemMapper.toItemDto(item, null, null, comments);
        }
        Booking lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeAndStatus(itemId, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "start")).orElse(null);
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatus(itemId, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "start")).orElse(null);

        return ItemMapper.toItemDto(item, BookingMapper.toItemBookingDto(lastBooking), BookingMapper.toItemBookingDto(nextBooking), comments);
    }

    @Override
    public List<ItemDtoDated> getUserItems(long userId, int from, int size) {
        checkUser(userId);

        Pageable page = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.findAllItemsByUserIdOrderById(userId, page);

        if (items.isEmpty()) {
            throw new NotFoundException("Пользователь " + userId + " не является хозяином ни одной вещи");
        }

        List<Long> itemIds = new ArrayList<>();
        for (Item item : items) {
            itemIds.add(item.getId());
        }
        List<ItemDtoDated> datedItemList = new ArrayList<>();
        Map<Item, List<Booking>> lastBookingsMap = bookingRepository.findAllByItemUserIdAndItemIdInAndStartBefore(userId, itemIds, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")).stream().collect(groupingBy(Booking::getItem, toList()));
        Map<Item, List<Booking>> nextBookingsMap = bookingRepository.findAllByItemUserIdAndItemIdInAndStartAfter(userId, itemIds, LocalDateTime.now(), Sort.by(Sort.Direction.ASC, "start")).stream().collect(groupingBy(Booking::getItem, toList()));
        Map<Item, List<Comment>> comments = commentRepository.findAllByItemUserIdInOrderByCreatedDesc(itemIds).stream().collect(groupingBy(Comment::getItem, toList()));

        for (Item item : items) {
            Booking lastBooking = null;
            Booking nextBooking = null;

            if (lastBookingsMap.get(item) != null && lastBookingsMap.get(item).get(0) != null) {
                lastBooking = lastBookingsMap.get(item).get(0);
            }
            if (nextBookingsMap.get(item) != null && nextBookingsMap.get(item).get(0) != null) {
                nextBooking = nextBookingsMap.get(item).get(0);
            }
            List<CommentDtoOut> commentsList = comments.getOrDefault(item, List.of()).stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
            datedItemList.add(ItemMapper.toItemDto(item, BookingMapper.toItemBookingDto(lastBooking), BookingMapper.toItemBookingDto(nextBooking), commentsList));
        }
        return datedItemList;
    }

    @Transactional
    @Override
    public CommentDtoOut saveComment(long userId, long itemId, CommentDtoIn commentDto) {
        checkId(itemId);

        User user = checkUser(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмета с ID " + itemId + " не зарегистрировано"));
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndStatusAndStartBeforeAndEndBefore(itemId, userId, BookingStatus.APPROVED, LocalDateTime.now(), LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new BadParameterException("Пользователь " + userId + " не арендовал вещь " + itemId + ". Не имеет права писать отзыв");
        }
        Comment comment = CommentMapper.toComment(commentDto, item, user);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private User checkUser(long userId) {
        checkId(userId);
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
    }

    private void checkId(long userId) {
        if (userId <= 0) {
            throw new BadParameterException("id must be positive");
        }
    }
}