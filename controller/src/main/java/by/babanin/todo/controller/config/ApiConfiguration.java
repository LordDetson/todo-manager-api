package by.babanin.todo.controller.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfiguration {

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
