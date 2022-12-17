package by.babanin.todo.controller.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import by.babanin.todo.model.Priority;
import lombok.Data;

@Data
public class TodoToCreate {

    @NotBlank
    @Size(min = 1, max = 32)
    String title;

    @Size(max = 1024)
    String description;

    Priority priority;

    @NotNull
    LocalDate plannedDate;

    @PositiveOrZero
    Long position;
}
