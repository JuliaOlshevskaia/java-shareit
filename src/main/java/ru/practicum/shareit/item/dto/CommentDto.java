package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.Generated;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Generated
@Data
@Validated
public class CommentDto {
    @NotNull
    @NotBlank
    private String text;
}
