package ru.practicum.shareitserver.requests.dto;

import lombok.Data;
import lombok.Generated;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Generated
@Data
@Validated
public class RequestsParamByPage {
    @PositiveOrZero
    private Integer from;

    @Positive
    private Integer size;
}
