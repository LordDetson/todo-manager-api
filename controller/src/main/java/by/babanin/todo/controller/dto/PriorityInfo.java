package by.babanin.todo.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

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
