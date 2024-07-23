package ru.practicum.shareit.requests.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Data
@Validated
public class RequestsParamByPage {
    @PositiveOrZero
    private Integer from;

    @Positive
    private Integer size;
}
