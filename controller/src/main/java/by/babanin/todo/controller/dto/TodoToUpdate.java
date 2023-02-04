package by.babanin.todo.controller.dto;

import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TodoToUpdate {

    @NotBlank
    @Size(min = 1, max = 32)
    String title;

    @Size(max = 1024)
    String description;

    Priority priority;

    @NotNull
    Status status;
}
