package ru.practicum.shareitserver.item.dto;

import lombok.Data;
import lombok.Generated;
import org.springframework.validation.annotation.Validated;

@Generated
@Data
@Validated
public class CommentDto {
    private String text;
}
