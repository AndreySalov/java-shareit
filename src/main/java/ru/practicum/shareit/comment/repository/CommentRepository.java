package ru.practicum.shareit.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.comment.Comment;


import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItemUserIdInOrderByCreatedDesc(List<Long> userIds);

    List<Comment> findAllByItemIdOrderByCreatedDesc(Long itemId);

}