package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.error.NoSuchElemException;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.interfaces.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void getAll() {
        User user1 = new User(1L, "user1", "user1@mail.ru");
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user1));

        List<UserDto> dtos = userService.getAll();

        verify(userRepository, Mockito.times(1)).findAll();
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        UserDto dto = dtos.get(0);
        assertEquals(user1.getId(), dto.getId());
        assertEquals(user1.getName(), dto.getName());
        assertEquals(user1.getEmail(), dto.getEmail());
    }

    @Test
    void get() {
        User user1 = new User(1L, "user1", "user1@mail.ru");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        UserDto userFrom = userService.get(1L);
        verify(userRepository, Mockito.times(1)).findById(1L);
        assertNotNull(userFrom);
        assertEquals(user1.getId(), userFrom.getId());
        assertEquals(user1.getName(), userFrom.getName());
        assertEquals(user1.getEmail(), userFrom.getEmail());

    }

    @Test
    void getInvalid() {
        User user1 = new User(1L, "user1", "user1@mail.ru");
        when(userRepository.findById(100L)).thenThrow(new NoSuchElemException("Нет такого пользователя"));

        NoSuchElemException e = assertThrows(NoSuchElemException.class, () -> {
            userService.get(100L);
        });
        verify(userRepository, Mockito.times(1)).findById(100L);
        assertEquals("Нет такого пользователя", e.getMessage());
    }

    @Test
    void create() {
        User user1 = new User(1L, "user1", "user1@mail.ru");
        UserDto user1Dto = new UserDto(1L, "user1", "user1@mail.ru");
        when(userRepository.save(any(User.class))).thenReturn(user1);

        UserDto userFrom = userService.create(user1Dto);
        verify(userRepository, Mockito.times(1)).save(any(User.class));
        assertEquals(user1.getId(), userFrom.getId());
        assertEquals(user1.getName(), userFrom.getName());
        assertEquals(user1.getEmail(), userFrom.getEmail());
    }

    @Test
    void update() {
        User user1 = new User(1L, "user1Changed", "user1@mail.ru");
        UserDto user1Dto = new UserDto(1L, "user1Changed", "user1@mail.ru");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenReturn(user1);

        UserDto userFrom = userService.update(user1Dto, 1L);
        verify(userRepository, Mockito.times(1)).save(any(User.class));
        verify(userRepository, Mockito.times(1)).findById(1L);
        assertEquals(user1.getId(), userFrom.getId());
        assertEquals(user1.getName(), userFrom.getName());
        assertEquals(user1.getEmail(), userFrom.getEmail());
    }

    @Test
    void updateIvalid() {
        User user1 = new User(1L, "user1Changed", "user1@mail.ru");
        UserDto user1Dto = new UserDto(1L, "user1Changed", "user1@mail.ru");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenReturn(user1);

        NoSuchElemException e = assertThrows(NoSuchElemException.class, () -> {
            userService.update(user1Dto, 100L);
        });
        assertEquals("Нет такого пользователя", e.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(userRepository, Mockito.times(1)).findById(100L);

    }

    @Test
    void delete() {
        User user1 = new User(1L, "user1Changed", "user1@mail.ru");
        UserDto user1Dto = new UserDto(1L, "user1Changed", "user1@mail.ru");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        userService.delete(1L);
        verify(userRepository, Mockito.times(1)).findById(1L);
        verify(userRepository, Mockito.times(1)).delete(any(User.class));
    }

    @Test
    void deleteInvalid() {
        User user1 = new User(1L, "user1Changed", "user1@mail.ru");
        UserDto user1Dto = new UserDto(1L, "user1Changed", "user1@mail.ru");
        when(userRepository.findById(100L)).thenThrow(new NoSuchElemException("Нет такого пользователя"));

        NoSuchElemException e = assertThrows(NoSuchElemException.class, () -> {
            userService.delete(100L);
        });
        assertEquals("Нет такого пользователя", e.getMessage());

        verify(userRepository, Mockito.times(1)).findById(100L);
        verify(userRepository, never()).delete(any(User.class));
    }

}
