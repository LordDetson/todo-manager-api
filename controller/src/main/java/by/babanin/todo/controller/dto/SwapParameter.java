package by.babanin.todo.controller.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class SwapParameter {

    @Min(value = 0, message = "Position \"from\" can't be negative")
    long from;

    @Min(value = 0, message = "Position \"to\" can't be negative")
    long to;
}
