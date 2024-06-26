package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.dto.Item;
import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;


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
    Item toItem(ItemEntity itemEntity);

    List<Item> toListItem(List<ItemEntity> items);
}
