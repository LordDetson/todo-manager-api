package by.babanin.todo.controller;

import java.util.Set;

import javax.validation.ValidationException;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CheckUtils {

    public static void assertNegativeIds(Set<Long> ids) {
        for(Long id : ids){
            if(id < 0) {
                throw new ValidationException("ID set must not contain negative IDs.");
            }
        }
    }
}
