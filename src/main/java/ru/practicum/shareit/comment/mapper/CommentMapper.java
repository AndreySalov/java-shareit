package ru.practicum.shareit.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDtoIn;
import ru.practicum.shareit.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {

    public static Comment toComment(CommentDtoIn comment, Item item, User user) {
        return new Comment(
                comment.getId(),
                comment.getText(),
                item,
                user,
                LocalDateTime.now()
        );
    }

    public static CommentDtoOut toCommentDto(Comment comment) {

        return new CommentDtoOut(
                comment.getId(),
                comment.getText(),
                ItemMapper.toItemDto(comment.getItem()),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}