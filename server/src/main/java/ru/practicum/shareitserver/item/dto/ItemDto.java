package ru.practicum.shareitserver.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Generated
@Data
@AllArgsConstructor
@Validated
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDto {
    @NotNull
    @NotBlank
    private String name;

    @NotNull
    private String description;

    @NotNull
    private Boolean available;

    private Long requestId;
}
