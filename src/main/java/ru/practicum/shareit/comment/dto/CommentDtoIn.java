package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.common.dto.Create;
import ru.practicum.shareit.common.dto.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentDtoIn {

    private Long id;

    @NotBlank(groups = {Create.class})
    @Size(max = 500, groups = {Create.class, Update.class})
    private String text;
}