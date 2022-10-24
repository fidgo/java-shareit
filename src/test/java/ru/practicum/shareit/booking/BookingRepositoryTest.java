package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.PageRequestFrom;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
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
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private LocalDateTime start1;
    private LocalDateTime end1;
    private User owner;
    private User booker;
    private Item item1;

    private Booking booking1;

    private ItemRequest item1Request;

    @BeforeEach
    void beforeEach() {
        start1 = LocalDateTime.now().minusDays(1);
        end1 = LocalDateTime.now().plusDays(2);
        owner = new User(1L, "user1", "user1@mail.ru");
        booker = new User(2L, "user2", "user2@mail.ru");
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item1Request = new ItemRequest(1L, "descr", booker, LocalDateTime.now());
        item1Request = itemRequestRepository.save(item1Request);
        item1 = new Item(1L, "car", "very fast", true, owner, item1Request);
        item1 = itemRepository.save(item1);
        booking1 = new Booking(1L, start1, end1, item1, booker, BookingState.WAITING);
        booking1 = bookingRepository.save(booking1);
    }

    @Test
    void findFirstByBooker_IdAndItem_IdAndEndBefore() {
        Optional<Booking> optBooking = bookingRepository.findFirstByBooker_IdAndItem_IdAndEndBefore(booker.getId(),
                item1.getId(), LocalDateTime.now().plusDays(10));

        assertNotNull(optBooking);
        assertEquals(true, optBooking.isPresent());
        Booking from = optBooking.get();
        assertEquals(from.getId(), booking1.getId());
        assertEquals(from.getEnd(), booking1.getEnd());
        assertEquals(from.getStart(), booking1.getStart());
        assertEquals(from.getItem().getId(), booking1.getItem().getId());
        assertEquals(from.getItem().getDescription(), booking1.getItem().getDescription());
        assertEquals(from.getItem().getAvailable(), booking1.getItem().getAvailable());
        assertEquals(from.getItem().getName(), booking1.getItem().getName());
        assertEquals(from.getBooker().getId(), booking1.getBooker().getId());
        assertEquals(from.getBooker().getName(), booking1.getBooker().getName());
        assertEquals(from.getBooker().getEmail(), booking1.getBooker().getEmail());

    }

    @Test
    void findTop2ByItem_Owner_IdAndItem_IdOrderByStartAsc() {
        List<Booking> lsBooking =
                bookingRepository.findTop2ByItem_Owner_IdAndItem_IdOrderByStartAsc(item1.getOwner().getId(),
                        item1.getId());

        assertNotNull(lsBooking);
        assertEquals(1, lsBooking.size());
        Booking from = lsBooking.get(0);
        assertEquals(from.getId(), booking1.getId());
        assertEquals(from.getEnd(), booking1.getEnd());
        assertEquals(from.getStart(), booking1.getStart());
        assertEquals(from.getItem().getId(), booking1.getItem().getId());
        assertEquals(from.getItem().getDescription(), booking1.getItem().getDescription());
        assertEquals(from.getItem().getAvailable(), booking1.getItem().getAvailable());
        assertEquals(from.getItem().getName(), booking1.getItem().getName());
        assertEquals(from.getBooker().getId(), booking1.getBooker().getId());
        assertEquals(from.getBooker().getName(), booking1.getBooker().getName());
        assertEquals(from.getBooker().getEmail(), booking1.getBooker().getEmail());
    }

    @Test
    void findAllByBooker_IdOrderByStartDesc() {
        List<Booking> lsBooking =
                bookingRepository.findAllByBooker_IdOrderByStartDesc(booker.getId());

        assertNotNull(lsBooking);
        assertEquals(1, lsBooking.size());
        Booking from = lsBooking.get(0);
        assertEquals(from.getId(), booking1.getId());
        assertEquals(from.getEnd(), booking1.getEnd());
        assertEquals(from.getStart(), booking1.getStart());
        assertEquals(from.getItem().getId(), booking1.getItem().getId());
        assertEquals(from.getItem().getDescription(), booking1.getItem().getDescription());
        assertEquals(from.getItem().getAvailable(), booking1.getItem().getAvailable());
        assertEquals(from.getItem().getName(), booking1.getItem().getName());
        assertEquals(from.getBooker().getId(), booking1.getBooker().getId());
        assertEquals(from.getBooker().getName(), booking1.getBooker().getName());
        assertEquals(from.getBooker().getEmail(), booking1.getBooker().getEmail());
    }

    @Test
    void findAllByBooker_Id() {
        PageRequest pageRequest = new PageRequestFrom(10, 0, Sort.unsorted());
        List<Booking> lsBooking =
                bookingRepository.findAllByBooker_Id(booker.getId(), pageRequest).toList();

        assertNotNull(lsBooking);
        assertEquals(1, lsBooking.size());
        Booking from = lsBooking.get(0);
        assertEquals(from.getId(), booking1.getId());
        assertEquals(from.getEnd(), booking1.getEnd());
        assertEquals(from.getStart(), booking1.getStart());
        assertEquals(from.getItem().getId(), booking1.getItem().getId());
        assertEquals(from.getItem().getDescription(), booking1.getItem().getDescription());
        assertEquals(from.getItem().getAvailable(), booking1.getItem().getAvailable());
        assertEquals(from.getItem().getName(), booking1.getItem().getName());
        assertEquals(from.getBooker().getId(), booking1.getBooker().getId());
        assertEquals(from.getBooker().getName(), booking1.getBooker().getName());
        assertEquals(from.getBooker().getEmail(), booking1.getBooker().getEmail());
    }

    @Test
    void findAllByItem_Owner_Id() {
        PageRequest pageRequest = new PageRequestFrom(10, 0, Sort.unsorted());
        List<Booking> lsBooking =
                bookingRepository.findAllByItem_Owner_Id(owner.getId(), pageRequest).toList();

        assertNotNull(lsBooking);
        assertEquals(1, lsBooking.size());
        Booking from = lsBooking.get(0);
        assertEquals(from.getId(), booking1.getId());
        assertEquals(from.getEnd(), booking1.getEnd());
        assertEquals(from.getStart(), booking1.getStart());
        assertEquals(from.getItem().getId(), booking1.getItem().getId());
        assertEquals(from.getItem().getDescription(), booking1.getItem().getDescription());
        assertEquals(from.getItem().getAvailable(), booking1.getItem().getAvailable());
        assertEquals(from.getItem().getName(), booking1.getItem().getName());
        assertEquals(from.getBooker().getId(), booking1.getBooker().getId());
        assertEquals(from.getBooker().getName(), booking1.getBooker().getName());
        assertEquals(from.getBooker().getEmail(), booking1.getBooker().getEmail());
    }

    @Test
    void findAllByBooker_IdAndStatusIs() {
        PageRequest pageRequest = new PageRequestFrom(10, 0, Sort.unsorted());
        List<Booking> lsBooking =
                bookingRepository.findAllByItem_Owner_Id(owner.getId(), pageRequest).toList();

        assertNotNull(lsBooking);
        assertEquals(1, lsBooking.size());
        Booking from = lsBooking.get(0);
        assertEquals(from.getId(), booking1.getId());
        assertEquals(from.getEnd(), booking1.getEnd());
        assertEquals(from.getStart(), booking1.getStart());
        assertEquals(from.getItem().getId(), booking1.getItem().getId());
        assertEquals(from.getItem().getDescription(), booking1.getItem().getDescription());
        assertEquals(from.getItem().getAvailable(), booking1.getItem().getAvailable());
        assertEquals(from.getItem().getName(), booking1.getItem().getName());
        assertEquals(from.getBooker().getId(), booking1.getBooker().getId());
        assertEquals(from.getBooker().getName(), booking1.getBooker().getName());
        assertEquals(from.getBooker().getEmail(), booking1.getBooker().getEmail());
        assertEquals(from.getStatus(), booking1.getStatus());
    }

    @Test
    void findAllByItem_Owner_IdAndStatusIs() {
        PageRequest pageRequest = new PageRequestFrom(10, 0, Sort.unsorted());
        List<Booking> lsBooking =
                bookingRepository.findAllByItem_Owner_IdAndStatusIs(owner.getId(),
                        BookingState.WAITING, pageRequest).toList();

        assertNotNull(lsBooking);
        assertEquals(1, lsBooking.size());
        Booking from = lsBooking.get(0);
        assertEquals(from.getId(), booking1.getId());
        assertEquals(from.getEnd(), booking1.getEnd());
        assertEquals(from.getStart(), booking1.getStart());
        assertEquals(from.getItem().getId(), booking1.getItem().getId());
        assertEquals(from.getItem().getDescription(), booking1.getItem().getDescription());
        assertEquals(from.getItem().getAvailable(), booking1.getItem().getAvailable());
        assertEquals(from.getItem().getName(), booking1.getItem().getName());
        assertEquals(from.getBooker().getId(), booking1.getBooker().getId());
        assertEquals(from.getBooker().getName(), booking1.getBooker().getName());
        assertEquals(from.getBooker().getEmail(), booking1.getBooker().getEmail());
        assertEquals(from.getStatus(), booking1.getStatus());
    }

    @Test
    void findAllByBooker_IdAndStartAfter() {
        LocalDateTime ldt = LocalDateTime.now().minusDays(15);
        PageRequest pageRequest = new PageRequestFrom(10, 0, Sort.unsorted());
        List<Booking> lsBooking =
                bookingRepository.findAllByBooker_IdAndStartAfter(booker.getId(), ldt, pageRequest).toList();

        assertNotNull(lsBooking);
        assertEquals(1, lsBooking.size());
        Booking from = lsBooking.get(0);
        assertEquals(from.getId(), booking1.getId());
        assertEquals(from.getEnd(), booking1.getEnd());
        assertEquals(from.getStart(), booking1.getStart());
        assertEquals(from.getItem().getId(), booking1.getItem().getId());
        assertEquals(from.getItem().getDescription(), booking1.getItem().getDescription());
        assertEquals(from.getItem().getAvailable(), booking1.getItem().getAvailable());
        assertEquals(from.getItem().getName(), booking1.getItem().getName());
        assertEquals(from.getBooker().getId(), booking1.getBooker().getId());
        assertEquals(from.getBooker().getName(), booking1.getBooker().getName());
        assertEquals(from.getBooker().getEmail(), booking1.getBooker().getEmail());
        assertEquals(from.getStatus(), booking1.getStatus());
    }

    @Test
    void findAllByItem_Owner_IdAndStartAfter() {
        LocalDateTime ldt = LocalDateTime.now().minusDays(15);
        PageRequest pageRequest = new PageRequestFrom(10, 0, Sort.unsorted());
        List<Booking> lsBooking =
                bookingRepository.findAllByItem_Owner_IdAndStartAfter(owner.getId(), ldt, pageRequest).toList();

        assertNotNull(lsBooking);
        assertEquals(1, lsBooking.size());
        Booking from = lsBooking.get(0);
        assertEquals(from.getId(), booking1.getId());
        assertEquals(from.getEnd(), booking1.getEnd());
        assertEquals(from.getStart(), booking1.getStart());
        assertEquals(from.getItem().getId(), booking1.getItem().getId());
        assertEquals(from.getItem().getDescription(), booking1.getItem().getDescription());
        assertEquals(from.getItem().getAvailable(), booking1.getItem().getAvailable());
        assertEquals(from.getItem().getName(), booking1.getItem().getName());
        assertEquals(from.getBooker().getId(), booking1.getBooker().getId());
        assertEquals(from.getBooker().getName(), booking1.getBooker().getName());
        assertEquals(from.getBooker().getEmail(), booking1.getBooker().getEmail());
        assertEquals(from.getStatus(), booking1.getStatus());
    }

    @Test
    void findAllByBooker_IdAndEndBefore() {
        LocalDateTime ldt = LocalDateTime.now().plusDays(15);
        PageRequest pageRequest = new PageRequestFrom(10, 0, Sort.unsorted());
        List<Booking> lsBooking =
                bookingRepository.findAllByBooker_IdAndEndBefore(booker.getId(), ldt, pageRequest).toList();

        assertNotNull(lsBooking);
        assertEquals(1, lsBooking.size());
        Booking from = lsBooking.get(0);
        assertEquals(from.getId(), booking1.getId());
        assertEquals(from.getEnd(), booking1.getEnd());
        assertEquals(from.getStart(), booking1.getStart());
        assertEquals(from.getItem().getId(), booking1.getItem().getId());
        assertEquals(from.getItem().getDescription(), booking1.getItem().getDescription());
        assertEquals(from.getItem().getAvailable(), booking1.getItem().getAvailable());
        assertEquals(from.getItem().getName(), booking1.getItem().getName());
        assertEquals(from.getBooker().getId(), booking1.getBooker().getId());
        assertEquals(from.getBooker().getName(), booking1.getBooker().getName());
        assertEquals(from.getBooker().getEmail(), booking1.getBooker().getEmail());
        assertEquals(from.getStatus(), booking1.getStatus());
    }

    @Test
    void findAllByItem_Owner_IdAndEndBefore() {
        LocalDateTime ldt = LocalDateTime.now().plusDays(15);
        PageRequest pageRequest = new PageRequestFrom(10, 0, Sort.unsorted());
        List<Booking> lsBooking =
                bookingRepository.findAllByItem_Owner_IdAndEndBefore(owner.getId(), ldt, pageRequest).toList();

        assertNotNull(lsBooking);
        assertEquals(1, lsBooking.size());
        Booking from = lsBooking.get(0);
        assertEquals(from.getId(), booking1.getId());
        assertEquals(from.getEnd(), booking1.getEnd());
        assertEquals(from.getStart(), booking1.getStart());
        assertEquals(from.getItem().getId(), booking1.getItem().getId());
        assertEquals(from.getItem().getDescription(), booking1.getItem().getDescription());
        assertEquals(from.getItem().getAvailable(), booking1.getItem().getAvailable());
        assertEquals(from.getItem().getName(), booking1.getItem().getName());
        assertEquals(from.getBooker().getId(), booking1.getBooker().getId());
        assertEquals(from.getBooker().getName(), booking1.getBooker().getName());
        assertEquals(from.getBooker().getEmail(), booking1.getBooker().getEmail());
        assertEquals(from.getStatus(), booking1.getStatus());
    }

    @Test
    void findCurrentBookingFromBooker() {
        LocalDateTime ldt = LocalDateTime.now();
        PageRequest pageRequest = new PageRequestFrom(10, 0, Sort.unsorted());
        List<Booking> lsBooking =
                bookingRepository.findCurrentBookingFromBooker(booker.getId(), ldt, pageRequest).toList();

        assertNotNull(lsBooking);
        assertEquals(1, lsBooking.size());
        Booking from = lsBooking.get(0);
        assertEquals(from.getId(), booking1.getId());
        assertEquals(from.getEnd(), booking1.getEnd());
        assertEquals(from.getStart(), booking1.getStart());
        assertEquals(from.getItem().getId(), booking1.getItem().getId());
        assertEquals(from.getItem().getDescription(), booking1.getItem().getDescription());
        assertEquals(from.getItem().getAvailable(), booking1.getItem().getAvailable());
        assertEquals(from.getItem().getName(), booking1.getItem().getName());
        assertEquals(from.getBooker().getId(), booking1.getBooker().getId());
        assertEquals(from.getBooker().getName(), booking1.getBooker().getName());
        assertEquals(from.getBooker().getEmail(), booking1.getBooker().getEmail());
        assertEquals(from.getStatus(), booking1.getStatus());
    }

    @Test
    void findCurrentBookingFromOwner() {
        LocalDateTime ldt = LocalDateTime.now();
        PageRequest pageRequest = new PageRequestFrom(10, 0, Sort.unsorted());
        List<Booking> lsBooking =
                bookingRepository.findCurrentBookingFromOwner(owner.getId(), ldt, pageRequest).toList();

        assertNotNull(lsBooking);
        assertEquals(1, lsBooking.size());
        Booking from = lsBooking.get(0);
        assertEquals(from.getId(), booking1.getId());
        assertEquals(from.getEnd(), booking1.getEnd());
        assertEquals(from.getStart(), booking1.getStart());
        assertEquals(from.getItem().getId(), booking1.getItem().getId());
        assertEquals(from.getItem().getDescription(), booking1.getItem().getDescription());
        assertEquals(from.getItem().getAvailable(), booking1.getItem().getAvailable());
        assertEquals(from.getItem().getName(), booking1.getItem().getName());
        assertEquals(from.getBooker().getId(), booking1.getBooker().getId());
        assertEquals(from.getBooker().getName(), booking1.getBooker().getName());
        assertEquals(from.getBooker().getEmail(), booking1.getBooker().getEmail());
        assertEquals(from.getStatus(), booking1.getStatus());
    }


    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}
