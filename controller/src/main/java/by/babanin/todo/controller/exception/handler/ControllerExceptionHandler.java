package by.babanin.todo.controller.exception.handler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ValidationException;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<String> handleConstraintViolationException(ValidationException exception) {
        return new ResponseEntity<>("Not valid due to validation error: " + exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResult handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        ErrorResult errorResult = new ErrorResult();
        List<FieldValidationError> fieldErrors = errorResult.getFieldErrors();
        for(FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.add(new FieldValidationError(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return errorResult;
    }
}
