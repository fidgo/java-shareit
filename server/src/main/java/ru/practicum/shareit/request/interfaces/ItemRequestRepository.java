package ru.practicum.shareit.request.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestor_IdOrderByCreatedDesc(long requestorId);

    Page<ItemRequest> findAllByRequestor_IdNot(long requestorId, PageRequest pageRequest);
}
