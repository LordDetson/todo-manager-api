package by.babanin.todo.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Status;
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
