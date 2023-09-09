package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class User {

    private Long id;
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;
    @NotBlank(message = "Email не может быть пустым")
    private String email;
}