package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.user.interfaces.UserService;

import java.util.List;

/**
 * Контроллер для управления пользователями(users) — создания, редактирования, удаления и просмотра пользователей.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Controller = {}, get All Users", this.getClass().getSimpleName());
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        log.info("Controller = {}, get user with id = {}", this.getClass().getSimpleName(), userId);
        return userService.get(userId);
    }

    @PostMapping
    UserDto create(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("Controller = {}, Create User = {}", this.getClass().getSimpleName(), userDto);
        UserDto dto = userService.create(userDto);
        return dto;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @Validated({Update.class}) @RequestBody UserDto userDto) {
        log.info("Controller = {}, update User = {} with id = {}", this.getClass().getSimpleName(), userDto, userId);
        return userService.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Controller = {}, delete User with id = {}", this.getClass().getSimpleName(), userId);
        userService.delete(userId);
    }
}
