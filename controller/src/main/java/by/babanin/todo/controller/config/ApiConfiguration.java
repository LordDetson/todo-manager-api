package by.babanin.todo.controller.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.controller.dto.TodoToUpdate;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Todo;

@Configuration
public class ApiConfiguration {

    @Bean
    ModelMapper modelMapper(Converter<Long, Priority> priorityIdConverter) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(TodoToUpdate.class, Todo.class)
                .addMappings(mapping -> mapping.using(priorityIdConverter)
                        .map(TodoToUpdate::getPriorityId, Todo::setPriority));
        return modelMapper;
    }

    @Bean
    Converter<Long, Priority> priorityIdConverter(PriorityService service) {
        return context -> {
            Long priorityId = context.getSource();
            if(priorityId != null) {
                return service.getById(priorityId);
            }
            return null;
        };
    }
}
