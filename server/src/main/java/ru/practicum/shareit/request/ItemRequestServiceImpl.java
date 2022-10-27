package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.NoSuchElemException;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponsesDto;
import ru.practicum.shareit.request.interfaces.ItemRequestRepository;
import ru.practicum.shareit.request.interfaces.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;


    @Override
    @Transactional
    public ItemRequestDto create(long userId, ItemRequestDto itemRequestDto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("Нет такого пользователя"));

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestor, itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest itemRequestFromRep = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.toItemRequestDto(itemRequestFromRep);
    }

    @Override
    @Transactional
    public List<ItemRequestResponsesDto> getOwn(long userId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("Нет такого пользователя"));

        List<ItemRequest> itemsReqByUser =
                itemRequestRepository.findAllByRequestor_IdOrderByCreatedDesc(userId);
        List<ItemRequestResponsesDto> reqResponseDtos =
                ItemRequestMapper.toItemRequestResponseDtosWithoutResponses(itemsReqByUser);
        List<ItemRequestResponsesDto> reqResponseDtosFilled = fillWithResponses(reqResponseDtos);

        return reqResponseDtosFilled;
    }

    @Override
    @Transactional
    public ItemRequestResponsesDto getById(long userId, long requestId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("Нет такого пользователя"));

        ItemRequest requestById = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElemException("Нет такой заявки!"));

        ItemRequestResponsesDto iReqRepDto = ItemRequestMapper.toItemRequestResponseDtoWithoutResponses(requestById);
        ItemRequestResponsesDto iReqRepDtoWithResponses = fillWithResponse(iReqRepDto);


        return iReqRepDtoWithResponses;
    }

    @Override
    @Transactional
    public List<ItemRequestResponsesDto> getAll(long userId, PageRequest pageRequest) {
        Page<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_IdNot(userId, pageRequest);
        List<ItemRequest> itemRequestsFromPage = itemRequests.stream().collect(Collectors.toList());
        List<ItemRequestResponsesDto> reqResponseDtos =
                ItemRequestMapper.toItemRequestResponseDtosWithoutResponses(itemRequestsFromPage);
        List<ItemRequestResponsesDto> reqResponseDtosFilled = fillWithResponses(reqResponseDtos);


        return reqResponseDtosFilled;
    }

    private ItemRequestResponsesDto fillWithResponse(ItemRequestResponsesDto iReqRepDto) {
        List<Item> itemsOfResponse = itemRepository.findAllByRequest_Id(iReqRepDto.getId());
        iReqRepDto.setItems(ItemRequestMapper.toItemRequestResponse(itemsOfResponse));

        return iReqRepDto;
    }

    private List<ItemRequestResponsesDto> fillWithResponses(List<ItemRequestResponsesDto> reqResponseDtos) {
        for (ItemRequestResponsesDto itemReqRespDto : reqResponseDtos) {
            List<Item> itemsOfResponse = itemRepository.findAllByRequest_Id(itemReqRespDto.getId());
            itemReqRespDto.setItems(ItemRequestMapper.toItemRequestResponse(itemsOfResponse));

        }

        return reqResponseDtos;
    }
}
