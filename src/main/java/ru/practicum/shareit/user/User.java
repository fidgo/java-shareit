package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Модель пользователя(user)
 */
@Getter
@Setter
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
}
