package by.babanin.todo.controller.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Status;
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

    Priority priority;

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
