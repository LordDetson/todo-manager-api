package by.babanin.todo.controller.dto;

import java.time.LocalDate;

import by.babanin.todo.model.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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
