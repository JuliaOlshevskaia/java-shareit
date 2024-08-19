package ru.practicum.shareitserver.requests.mapper;

import lombok.Generated;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareitserver.requests.dto.Requests;
import ru.practicum.shareitserver.requests.dto.RequestsResponse;
import ru.practicum.shareitserver.requests.entity.RequestsEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Generated
@Mapper(componentModel = SPRING)
public interface RequestsMapper {
    @Mapping(target = "description", source = "description")
    @Mapping(target = "requestorId", source = "requestorId")
    @Mapping(target = "created", source = "created")
    Requests toRequests(String description, Long requestorId, LocalDateTime created);

    RequestsEntity toEntity(Requests requests);

    @Mapping(target = "requestorId", source = "requestor.id")
    Requests toRequests(RequestsEntity requestsEntity);

    RequestsResponse toResponse(Requests requests);

    @Mapping(target = "items.requestId", source = "items.requests.id")
    @Mapping(target = "requestorId", source = "requestor.id")
    List<Requests> toListRequests(List<RequestsEntity> requestsEntity);

    List<RequestsResponse> toListResponse(List<Requests> requests);
}
