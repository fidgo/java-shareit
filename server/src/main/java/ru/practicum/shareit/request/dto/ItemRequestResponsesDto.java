package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemRequestResponsesDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemReqResponses> items;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class ItemReqResponses {
        private long id;
        private String name;
        private String description;
        private boolean available;
        private long requestId;
    }
}
