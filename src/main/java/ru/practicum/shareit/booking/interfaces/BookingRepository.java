package ru.practicum.shareit.booking.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findFirstByBooker_IdAndItem_IdAndEndBefore(long bookerId, long itemId, LocalDateTime now);

    List<Booking> findTop2ByItem_Owner_IdAndItem_IdOrderByStartAsc(long userId, long itemId);

    List<Booking> findAllByBooker_IdOrderByStartDesc(long bookerId);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(long ownerId);

    List<Booking> findAllByBooker_IdAndStatusIsOrderByStartDesc(long bookerId, BookingState status);

    List<Booking> findAllByItem_Owner_IdAndStatusIsOrderByStartDesc(long ownerId, BookingState status);

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime after);

    List<Booking> findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(long ownerId, LocalDateTime after);

    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime before);

    List<Booking> findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime before);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2 ")
    List<Booking> findCurrentBookingFromBooker(long bookerId, LocalDateTime now);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?2 ")
    List<Booking> findCurrentBookingFromOwner(long ownerId, LocalDateTime now);
}
