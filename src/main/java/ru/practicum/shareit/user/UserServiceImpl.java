package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.NoSuchElemException;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.interfaces.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {

        User user = UserMapper.toUser(userDto);
        User save = userRepository.save(user);

        return UserMapper.toUserDto(save);

    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, long userId) {
        User fromStorage = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("Нет такого пользователя"));
        UserDto byId = UserMapper.toUserDto(fromStorage);
        String emailFromInput = userDto.getEmail();

        UserDto toUpdate = getToUpdate(byId, userDto);
        User user = UserMapper.toUser(toUpdate);
        return UserMapper.toUserDto(userRepository.save(user));

    }

    @Override
    @Transactional
    public List<UserDto> getAll() {
        return UserMapper.toUsersDto(userRepository.findAll());
    }

    @Override
    @Transactional
    public UserDto get(long userId) {
        User fromStorage = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("Нет такого пользователя"));
        return UserMapper.toUserDto(fromStorage);
    }

    @Override
    @Transactional
    public void delete(long userId) {
        User fromStorage = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("Нет такого пользователя"));
        userRepository.delete(fromStorage);
    }


    private UserDto getToUpdate(UserDto byId, UserDto userDto) {
        return new UserDto(
                byId.getId(),
                userDto.getName() == null ? byId.getName() : userDto.getName(),
                userDto.getEmail() == null ? byId.getEmail() : userDto.getEmail()
        );
    }
}
