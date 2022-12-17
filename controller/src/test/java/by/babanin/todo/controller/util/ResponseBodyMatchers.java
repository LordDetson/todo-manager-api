package by.babanin.todo.controller.util;

import org.assertj.core.api.Assertions;
import org.springframework.test.web.servlet.ResultMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseBodyMatchers {

    private final ObjectMapper objectMapper;

    public ResponseBodyMatchers(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> ResultMatcher containsObjectAsJson(Object expected, Class<T> targetClass) {
        return result -> {
            String json = result.getResponse().getContentAsString();
            T actual = objectMapper.readValue(json, targetClass);
            Assertions.assertThat(actual).isEqualTo(expected);
        };
    }

    public static ResponseBodyMatchers responseBody(ObjectMapper objectMapper) {
        return new ResponseBodyMatchers(objectMapper);
    }
}
