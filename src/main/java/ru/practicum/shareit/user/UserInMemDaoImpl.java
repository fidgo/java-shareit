package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.interfaces.UserDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserInMemDaoImpl implements UserDao {
    private final HashMap<Long, User> users = new HashMap<>();

    private long idGenerator = 0L;

    @Override
    public User save(User user) {
        Long newUserId = ++idGenerator;
        user.setId(newUserId);
        users.put(newUserId, user);

        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User get(long userId) {
        User fromStorage = null;
        if (users.containsKey(userId)) {
            fromStorage = users.get(userId);
        }
        return fromStorage;
    }

    @Override
    public void remove(User fromStorage) {
        users.remove(fromStorage.getId());
    }
}
