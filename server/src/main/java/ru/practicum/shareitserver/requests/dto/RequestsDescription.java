package ru.practicum.shareitserver.requests.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

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
