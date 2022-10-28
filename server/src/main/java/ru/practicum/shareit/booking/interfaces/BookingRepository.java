package ru.practicum.shareit.booking.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    Page<Booking> findAllByBooker_Id(long bookerId, PageRequest pageRequest);

    Page<Booking> findAllByItem_Owner_Id(long ownerId, PageRequest pageRequest);

    Page<Booking> findAllByBooker_IdAndStatusIs(long bookerId, BookingState status, PageRequest pageRequest);

    Page<Booking> findAllByItem_Owner_IdAndStatusIs(long ownerId, BookingState status, PageRequest pageRequest);

    Page<Booking> findAllByBooker_IdAndStartAfter(long bookerId, LocalDateTime after, PageRequest pageRequest);

    Page<Booking> findAllByItem_Owner_IdAndStartAfter(long ownerId, LocalDateTime after, PageRequest pageRequest);

    Page<Booking> findAllByBooker_IdAndEndBefore(long bookerId, LocalDateTime before, PageRequest pageRequest);

    Page<Booking> findAllByItem_Owner_IdAndEndBefore(long ownerId, LocalDateTime before, PageRequest pageRequest);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2 ")
    Page<Booking> findCurrentBookingFromBooker(long bookerId, LocalDateTime now, PageRequest pageRequest);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?2 ")
    Page<Booking> findCurrentBookingFromOwner(long ownerId, LocalDateTime now, PageRequest pageRequest);
}
