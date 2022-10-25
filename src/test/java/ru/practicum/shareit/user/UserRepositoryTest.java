package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user1;


    @BeforeEach
    void beforeEach() {
        user1 = new User(1L, "user1", "user1@mail.ru");

        user1 = userRepository.save(user1);
    }

    @Test
    void findById() {
        Optional<User> userRepOpt = userRepository.findById(user1.getId());
        assertTrue(userRepOpt.isPresent());

        User userRep = userRepOpt.get();
        assertEquals(user1.getId(), userRep.getId());
        assertEquals(user1.getName(), userRep.getName());
        assertEquals(user1.getEmail(), userRep.getEmail());
    }

    @Test
    void findAll() {
        List<User> users = userRepository.findAll();
        assertNotNull(users);
        assertEquals(1, users.size());
        User userFrom = users.get(0);
        assertEquals(user1.getId(), userFrom.getId());
        assertEquals(user1.getName(), userFrom.getName());
        assertEquals(user1.getEmail(), userFrom.getEmail());

    }

    @Test
    void save() {
        User user2 = new User(2L, "user2", "user2@mail.ru");
        user2 = userRepository.save(user2);
        Optional<User> userFromOpt = userRepository.findById(user2.getId());

        assertTrue(userFromOpt.isPresent());
        User userFrom = userFromOpt.get();
        assertEquals(user2.getId(), userFrom.getId());
        assertEquals(user2.getName(), userFrom.getName());
        assertEquals(user2.getEmail(), userFrom.getEmail());
    }

    @Test
    void delete() {
        userRepository.delete(user1);
        List<User> users = userRepository.findAll();
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @AfterEach
    void afterEach() {

        userRepository.deleteAll();
    }
}
