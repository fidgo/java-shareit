package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserDao {
    User save(User user);

    User update(User user);

    List<User> getAll();

    User get(long userId);

    void remove(User fromStorage);

}
