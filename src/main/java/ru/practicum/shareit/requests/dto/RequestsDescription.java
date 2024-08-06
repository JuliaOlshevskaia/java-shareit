package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Generated
@Data
@Validated
@AllArgsConstructor
@NoArgsConstructor
public class RequestsDescription {
    @NotNull
    @NotBlank
    private String description;
}
