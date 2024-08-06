package ru.practicum.shareit.item.mapper;

import lombok.Generated;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.Comment;
import ru.practicum.shareit.item.entity.CommentEntity;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Generated
@Mapper(componentModel = SPRING)
public interface CommentMapper {

    @Mapping(target = "authorName", source = "commentEntity.author.name")
    Comment toComment(CommentEntity commentEntity);

    @Mapping(target = "authorName", source = "commentEntity.author.name")
    List<Comment> toListComment(List<CommentEntity> commentEntity);
}
