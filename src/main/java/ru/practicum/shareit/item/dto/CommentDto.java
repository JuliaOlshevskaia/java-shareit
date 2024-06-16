package ru.practicum.shareit.item.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Validated
public class CommentDto {
    @NotNull
    @NotBlank
    private String text;
}
