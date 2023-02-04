package by.babanin.todo.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PriorityInfo {

    @PositiveOrZero
    Long id;

    @NotBlank
    @Size(min = 1, max = 16)
    String name;

    @PositiveOrZero
    Long position;
}
