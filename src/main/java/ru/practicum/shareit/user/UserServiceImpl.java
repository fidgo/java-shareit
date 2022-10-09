package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.AlreadyExistException;
import ru.practicum.shareit.error.NoSuchElemException;
import ru.practicum.shareit.user.interfaces.UserDao;
import ru.practicum.shareit.user.interfaces.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final Set<String> uniqEmails = new HashSet<>();

    @Override
    public UserDto create(UserDto userDto) {
        if (uniqEmails.contains(userDto.getEmail())) {
            throw new AlreadyExistException("Такая почта уже существует");
        }
        uniqEmails.add(userDto.getEmail());

        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userDao.save(user));
    }

    @Override
    public UserDto update(UserDto userDto, long userId) {
        UserDto byId = get(userId);
        String emailFromInput = userDto.getEmail();

        if ((emailFromInput != null)
                && !(emailFromInput.equals(byId.getEmail()))
                && (uniqEmails.contains(emailFromInput))) {
            throw new AlreadyExistException("Такая почта уже существует");
        }
        uniqEmails.remove(byId.getEmail());
        uniqEmails.add(emailFromInput);


        UserDto toUpdate = getToUpdate(byId, userDto);

        User user = UserMapper.toUser(toUpdate);
        return UserMapper.toUserDto(userDao.update(user));
    }


    @Override
    public List<UserDto> getAll() {
        return UserMapper.toUsersDto(userDao.getAll());
    }

    @Override
    public UserDto get(long userId) {
        User fromStorage = userDao.get(userId);
        if (fromStorage == null) {
            throw new NoSuchElemException("Нет такого пользователя");
        }
        return UserMapper.toUserDto(fromStorage);
    }

    @Override
    public void delete(long userId) {
        User fromStorage = userDao.get(userId);
        if (fromStorage == null) {
            throw new NoSuchElemException("Нет такого пользователя");
        }
        userDao.remove(fromStorage);
        uniqEmails.remove(fromStorage.getEmail());
    }


    private UserDto getToUpdate(UserDto byId, UserDto userDto) {
        return new UserDto(
                byId.getId(),
                userDto.getName() == null ? byId.getName() : userDto.getName(),
                userDto.getEmail() == null ? byId.getEmail() : userDto.getEmail()
        );
    }
}
