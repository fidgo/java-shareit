package ru.practicum.shareit.request.interfaces;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponsesDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestResponsesDto> getOwn(long userId);

    ItemRequestResponsesDto getById(long userId, long requestId);

    List<ItemRequestResponsesDto> getAll(long userId, PageRequest pageRequest);

}
