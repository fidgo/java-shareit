package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.PageRequestFrom;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.interfaces.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User requestor;
    private Item item1;
    private Item item2;

    private ItemRequest item1Request;
    private ItemRequest item2Request;

    @BeforeEach
    void beforeEach() {
        owner = new User(1L, "user1", "user1@mail.ru");
        requestor = new User(2L, "user2", "user2@mail.ru");
        owner = userRepository.save(owner);
        requestor = userRepository.save(requestor);
        item1Request = new ItemRequest(1L, "need a car", requestor, LocalDateTime.now());
        item1Request = itemRequestRepository.save(item1Request);
        item2Request = new ItemRequest(2L, "need a ball", requestor, LocalDateTime.now());
        item2Request = itemRequestRepository.save(item2Request);
        item1 = new Item(1L, "car", "very fast", true, owner, item1Request);
        item1 = itemRepository.save(item1);
        item2 = new Item(2L, "ball", "round", true, owner, item2Request);
    }

    @Test
    void findAllByRequestor_IdOrderByCreatedDesc() {
        List<ItemRequest> iReqLs = itemRequestRepository.findAllByRequestor_IdOrderByCreatedDesc(requestor.getId());
        assertNotNull(iReqLs);
        assertEquals(2, iReqLs.size());
        ItemRequest iR1 = iReqLs.get(0);
        ItemRequest iR2 = iReqLs.get(1);
        assertEquals(true, iR1.getCreated().isAfter(iR2.getCreated()));
        assertEquals(item1Request.getId(), iR2.getId());
        assertEquals(item1Request.getDescription(), iR2.getDescription());
        assertEquals(item1Request.getRequestor().getId(), iR2.getRequestor().getId());
        assertEquals(item1Request.getRequestor().getName(), iR2.getRequestor().getName());
        assertEquals(item1Request.getRequestor().getEmail(), iR2.getRequestor().getEmail());

        assertEquals(item2Request.getId(), iR1.getId());
        assertEquals(item2Request.getDescription(), iR1.getDescription());
        assertEquals(item2Request.getRequestor().getId(), iR1.getRequestor().getId());
        assertEquals(item2Request.getRequestor().getName(), iR1.getRequestor().getName());
        assertEquals(item2Request.getRequestor().getEmail(), iR1.getRequestor().getEmail());

    }

    @Test
    void findAllByRequestor_IdNot() {
        PageRequest pageRequest = new PageRequestFrom(10, 0, Sort.by("created").descending());

        List<ItemRequest> iReqLs =
                itemRequestRepository.findAllByRequestor_IdNot(requestor.getId(), pageRequest).toList();
        assertNotNull(iReqLs);
        assertEquals(0, iReqLs.size());
    }

    @AfterEach
    void afterEach() {
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}
