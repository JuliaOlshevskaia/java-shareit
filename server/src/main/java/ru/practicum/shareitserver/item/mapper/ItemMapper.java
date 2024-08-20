package ru.practicum.shareitserver.item.mapper;

import lombok.Generated;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareitserver.item.entity.ItemEntity;
import ru.practicum.shareitserver.item.dto.*;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Generated
@Mapper(componentModel = SPRING)
public interface ItemMapper {

    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "available", source = "request.available")
    @Mapping(target = "userId", source = "userId")
    Item toItem(ItemDto request, Long userId);

    Item toItem(ItemUpdateDto request);

    ItemResponse toResponse(Item item);

    List<ItemResponse> toListResponse(List<Item> items);

    ItemEntity toEntity(Item item);

    @Mapping(target = "userId", source = "itemEntity.owner.id")
    @Mapping(target = "requestId", source = "itemEntity.requests.id")
    Item toItem(ItemEntity itemEntity);

    List<Item> toListItem(List<ItemEntity> items);

    @Mapping(target = "requestId", source = "itemEntity.requests.id")
    ItemForRequestsResponse toItemForRequestsResponse(ItemEntity itemEntity);

    @Mapping(target = "requestId", source = "itemEntity.requests.id")
    List<ItemForRequestsResponse> toListItemForRequestsResponse(List<ItemEntity> itemEntity);
}
