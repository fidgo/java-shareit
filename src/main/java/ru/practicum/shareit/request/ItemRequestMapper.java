package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponsesDto;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(User requestor, ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                requestor,
                itemRequestDto.getCreated()
        );
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated()
        );
    }


    public static ItemRequestResponsesDto toItemRequestResponseDtoWithoutResponses(ItemRequest itemReq) {
        ItemRequestResponsesDto itemReqRepDto = new ItemRequestResponsesDto();
        itemReqRepDto.setId(itemReq.getId());
        itemReqRepDto.setDescription(itemReq.getDescription());
        itemReqRepDto.setCreated(itemReq.getCreated());

        return itemReqRepDto;
    }


    public static List<ItemRequestResponsesDto> toItemRequestResponseDtosWithoutResponses(List<ItemRequest> itemsReqByUser) {
        List<ItemRequestResponsesDto> itemsReqRepDtos = new ArrayList<>();

        for (ItemRequest itemRequest : itemsReqByUser) {
            itemsReqRepDtos.add(toItemRequestResponseDtoWithoutResponses(itemRequest));
        }

        return itemsReqRepDtos;
    }

    public static ItemRequestResponsesDto.ItemReqResponses toItemReqResponses(Item item) {
        ItemRequestResponsesDto.ItemReqResponses response = new ItemRequestResponsesDto.ItemReqResponses();
        response.setId(item.getId());
        response.setName(item.getName());
        response.setDescription(item.getDescription());
        response.setAvailable(item.getAvailable());
        response.setRequestId(item.getRequest().getId());
        return response;
    }

    public static List<ItemRequestResponsesDto.ItemReqResponses> toItemRequestResponse(List<Item> itemsOfResponse) {
        List<ItemRequestResponsesDto.ItemReqResponses> responses = new ArrayList<>();

        for (Item item : itemsOfResponse) {
            responses.add(toItemReqResponses(item));
        }

        return responses;
    }

    public static List<ItemRequestDto> toItemRequests(List<ItemRequest> itemRequestsFromPage) {
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestsFromPage) {
            itemRequestDtos.add(toItemRequestDto(itemRequest));
        }
        return itemRequestDtos;
    }
}
