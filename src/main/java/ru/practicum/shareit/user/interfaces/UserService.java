package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, long userId);

    List<UserDto> getAll();

    UserDto get(long userId);

    void delete(long userId);

}
