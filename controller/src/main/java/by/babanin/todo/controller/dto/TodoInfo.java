package by.babanin.todo.controller.dto;

import java.time.LocalDate;

import by.babanin.todo.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TodoInfo {

    @PositiveOrZero
    Long id;

    @NotBlank
    @Size(min = 1, max = 32)
    String title;

    @Size(max = 1024)
    String description;

    PriorityInfo priority;

    @NotNull
    Status status;

    @NotNull
    @PastOrPresent
    LocalDate creationDate;

    @NotNull
    LocalDate plannedDate;

    @PastOrPresent
    LocalDate completionDate;

    @NotNull
    @PositiveOrZero
    Long position;
}
