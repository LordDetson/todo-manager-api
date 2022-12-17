package by.babanin.todo.controller.exception.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ErrorResult {

    private final List<FieldValidationError> fieldErrors = new ArrayList<>();

    public List<FieldValidationError> getFieldErrors() {
        return fieldErrors;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        ErrorResult that = (ErrorResult) o;
        // order isn't important
        return fieldErrors.size() == that.fieldErrors.size()
                && fieldErrors.containsAll(that.fieldErrors)
                && that.fieldErrors.containsAll(fieldErrors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldErrors);
    }
}
