package ru.practicum.shareit.item;

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
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.interfaces.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User owner;

    private User requestor;
    private Item item1;

    private ItemRequest item1Request;

    @BeforeEach
    void beforeEach() {
        owner = new User(1L, "user1", "user1@mail.ru");
        requestor = new User(2L, "user2", "user2@mail.ru");
        owner = userRepository.save(owner);
        requestor = userRepository.save(requestor);
        item1Request = new ItemRequest(1L, "need a car", requestor, LocalDateTime.now());

        item1Request.setRequestor(requestor);
        item1Request = itemRequestRepository.save(item1Request);
        item1 = new Item(1L, "car", "very fast", true, owner, item1Request);
        item1 = itemRepository.save(item1);
    }

    @Test
    void findAllByOwner() {
        PageRequest pageRequest = new PageRequestFrom(10, 0, Sort.unsorted());
        List<Item> listFrom = itemRepository.findAllByOwner(owner, pageRequest).toList();
        assertNotNull(listFrom);
        assertEquals(1, listFrom.size());
        Item iFrom = listFrom.get(0);
        assertEquals(item1.getId(), iFrom.getId());
        assertEquals(item1.getOwner().getId(), iFrom.getOwner().getId());
        assertEquals(item1.getOwner().getName(), iFrom.getOwner().getName());
        assertEquals(item1.getOwner().getEmail(), iFrom.getOwner().getEmail());
        assertEquals(item1.getDescription(), iFrom.getDescription());
        assertEquals(item1.getAvailable(), iFrom.getAvailable());
    }

    @Test
    void findFirstByOwner() {
        Optional<Item> optFrom = itemRepository.findFirstByOwner(owner);
        assertNotNull(optFrom);
        assertEquals(true, optFrom.isPresent());
        Item iFrom = optFrom.get();
        assertEquals(item1.getId(), iFrom.getId());
        assertEquals(item1.getOwner().getId(), iFrom.getOwner().getId());
        assertEquals(item1.getOwner().getName(), iFrom.getOwner().getName());
        assertEquals(item1.getOwner().getEmail(), iFrom.getOwner().getEmail());
        assertEquals(item1.getDescription(), iFrom.getDescription());
        assertEquals(item1.getAvailable(), iFrom.getAvailable());
    }

    @Test
    void search() {
        PageRequest pageRequest = new PageRequestFrom(10, 0, Sort.unsorted());
        List<Item> itemList = itemRepository.search("car", pageRequest).toList();
        assertNotNull(itemList);
        assertEquals(1, itemList.size());
        Item iFrom = itemList.get(0);
        assertEquals(item1.getId(), iFrom.getId());
        assertEquals(item1.getOwner().getId(), iFrom.getOwner().getId());
        assertEquals(item1.getOwner().getName(), iFrom.getOwner().getName());
        assertEquals(item1.getOwner().getEmail(), iFrom.getOwner().getEmail());
        assertEquals(item1.getDescription(), iFrom.getDescription());
        assertEquals(item1.getAvailable(), iFrom.getAvailable());
    }

    @Test
    void findAllByRequest_Id() {
        List<Item> itemsFrom = itemRepository.findAllByRequest_Id(item1Request.getId());
        assertNotNull(itemsFrom);
        assertEquals(1, itemsFrom.size());
        Item iFrom = itemsFrom.get(0);
        assertEquals(item1.getId(), iFrom.getId());
        assertEquals(item1.getOwner().getId(), iFrom.getOwner().getId());
        assertEquals(item1.getOwner().getName(), iFrom.getOwner().getName());
        assertEquals(item1.getOwner().getEmail(), iFrom.getOwner().getEmail());
        assertEquals(item1.getDescription(), iFrom.getDescription());
        assertEquals(item1.getAvailable(), iFrom.getAvailable());
    }

    @AfterEach
    void afterEach() {
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}
