package by.babanin.todo.controller.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class PriorityInfo {

    @Min(value = 0, message = "Id can't be negative")
    Long id;

    @NotBlank(message = "Name can't be empty")
    String name;

    @Min(value = 0, message = "Position can't be negative")
    Long position;
}
