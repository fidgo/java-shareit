package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Controller = {}, get All Users", this.getClass().getSimpleName());
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        log.info("Controller = {}, get user with id = {}", this.getClass().getSimpleName(), userId);
        return userClient.getUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("Controller = {}, Create User = {}", this.getClass().getSimpleName(), userDto);
        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable long userId, @Validated({Update.class}) @RequestBody UserDto userDto) {
        log.info("Controller = {}, update User = {} with id = {}", this.getClass().getSimpleName(), userDto, userId);
        return userClient.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable long userId) {
        log.info("Controller = {}, delete User with id = {}", this.getClass().getSimpleName(), userId);
        return userClient.delete(userId);
    }
}
