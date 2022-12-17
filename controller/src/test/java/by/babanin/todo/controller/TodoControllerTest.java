package by.babanin.todo.controller;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import by.babanin.todo.application.repository.TodoRepository;
import by.babanin.todo.application.service.TodoService;
import by.babanin.todo.controller.dto.SwapParameter;
import by.babanin.todo.controller.dto.TodoInfo;
import by.babanin.todo.controller.dto.TodoToCreate;
import by.babanin.todo.controller.dto.TodoToUpdate;
import by.babanin.todo.controller.exception.handler.ErrorResult;
import by.babanin.todo.controller.exception.handler.FieldValidationError;
import by.babanin.todo.controller.util.ResponseBodyMatchers;
import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;
import by.babanin.todo.model.Todo.Fields;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    @MockBean
    private TodoService service;

    @MockBean
    private TodoRepository repository;

    @TestConfiguration
    static class AdditionalConfiguration {

        @Bean
        ModelMapper modelMapper() {
            return new ModelMapper();
        }
    }

    @Test
    void create() throws Exception {
        Todo todo = Todo.builder()
                .id(0L)
                .title("title")
                .status(Status.OPEN)
                .creationDate(LocalDate.now())
                .plannedDate(LocalDate.now().plusDays(1))
                .build();
        TodoInfo expectedTodoInfo = modelMapper.map(todo, TodoInfo.class);
        Mockito.when(service.create(
                        todo.getTitle(),
                        todo.getDescription(),
                        todo.getPriority(),
                        todo.getPlannedDate()))
                .thenReturn(todo);
        TodoToCreate todoToCreate = new TodoToCreate();
        todoToCreate.setTitle(todo.getTitle());
        todoToCreate.setDescription(todo.getDescription());
        todoToCreate.setPriority(todo.getPriority());
        todoToCreate.setPlannedDate(todo.getPlannedDate());

        mockMvc.perform(MockMvcRequestBuilders.post("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoToCreate)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(ResponseBodyMatchers.responseBody(objectMapper)
                        .containsObjectAsJson(expectedTodoInfo, TodoInfo.class));

        Mockito.verify(service, Mockito.times(1))
                .create(todo.getTitle(),
                        todo.getDescription(),
                        todo.getPriority(),
                        todo.getPlannedDate());
    }

    @Test
    void createWithPosition() throws Exception {
        Todo todo = Todo.builder()
                .id(0L)
                .title("title")
                .status(Status.OPEN)
                .creationDate(LocalDate.now())
                .plannedDate(LocalDate.now().plusDays(1))
                .position(1)
                .build();
        TodoInfo expectedTodoInfo = modelMapper.map(todo, TodoInfo.class);
        Mockito.when(service.create(
                        todo.getPosition(),
                        todo.getTitle(),
                        todo.getDescription(),
                        todo.getPriority(),
                        todo.getPlannedDate()))
                .thenReturn(todo);
        TodoToCreate todoToCreate = new TodoToCreate();
        todoToCreate.setTitle(todo.getTitle());
        todoToCreate.setDescription(todo.getDescription());
        todoToCreate.setPriority(todo.getPriority());
        todoToCreate.setPlannedDate(todo.getPlannedDate());
        todoToCreate.setPosition(todo.getPosition());

        mockMvc.perform(MockMvcRequestBuilders.post("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoToCreate)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(ResponseBodyMatchers.responseBody(objectMapper)
                        .containsObjectAsJson(expectedTodoInfo, TodoInfo.class));

        Mockito.verify(service, Mockito.times(1))
                .create(todo.getPosition(),
                        todo.getTitle(),
                        todo.getDescription(),
                        todo.getPriority(),
                        todo.getPlannedDate());
    }

    @Test
    void createWithEmptyTitle() throws Exception {
        Todo todo = Todo.builder()
                .id(0L)
                .title("")
                .status(Status.OPEN)
                .creationDate(LocalDate.now())
                .plannedDate(LocalDate.now().plusDays(1))
                .build();
        Mockito.when(service.create(
                        todo.getTitle(),
                        todo.getDescription(),
                        todo.getPriority(),
                        todo.getPlannedDate()))
                .thenReturn(todo);
        TodoToCreate todoToCreate = new TodoToCreate();
        todoToCreate.setTitle(todo.getTitle());
        todoToCreate.setDescription(todo.getDescription());
        todoToCreate.setPriority(todo.getPriority());
        todoToCreate.setPlannedDate(todo.getPlannedDate());
        ErrorResult errorResult = new ErrorResult();
        List<FieldValidationError> fieldErrors = errorResult.getFieldErrors();
        fieldErrors.add(new FieldValidationError(Fields.title, "must not be blank"));
        fieldErrors.add(new FieldValidationError(Fields.title, "size must be between 1 and 32"));

        mockMvc.perform(MockMvcRequestBuilders.post("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoToCreate)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(ResponseBodyMatchers.responseBody(objectMapper)
                        .containsObjectAsJson(errorResult, ErrorResult.class));

        Mockito.verify(service, Mockito.never())
                .create(todo.getTitle(),
                        todo.getDescription(),
                        todo.getPriority(),
                        todo.getPlannedDate());
    }

    @Test
    void createWithLoneTitle() throws Exception {
        Todo todo = Todo.builder()
                .id(0L)
                .title(RandomStringUtils.random(33))
                .status(Status.OPEN)
                .creationDate(LocalDate.now())
                .plannedDate(LocalDate.now().plusDays(1))
                .build();
        Mockito.when(service.create(
                        todo.getTitle(),
                        todo.getDescription(),
                        todo.getPriority(),
                        todo.getPlannedDate()))
                .thenReturn(todo);
        TodoToCreate todoToCreate = new TodoToCreate();
        todoToCreate.setTitle(todo.getTitle());
        todoToCreate.setDescription(todo.getDescription());
        todoToCreate.setPriority(todo.getPriority());
        todoToCreate.setPlannedDate(todo.getPlannedDate());
        ErrorResult errorResult = new ErrorResult();
        List<FieldValidationError> fieldErrors = errorResult.getFieldErrors();
        fieldErrors.add(new FieldValidationError(Fields.title, "size must be between 1 and 32"));

        mockMvc.perform(MockMvcRequestBuilders.post("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoToCreate)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(ResponseBodyMatchers.responseBody(objectMapper)
                        .containsObjectAsJson(errorResult, ErrorResult.class));

        Mockito.verify(service, Mockito.never())
                .create(todo.getTitle(),
                        todo.getDescription(),
                        todo.getPriority(),
                        todo.getPlannedDate());
    }

    @Test
    void createWithNegativePosition() throws Exception {
        Todo todo = Todo.builder()
                .id(0L)
                .title("title")
                .status(Status.OPEN)
                .creationDate(LocalDate.now())
                .plannedDate(LocalDate.now().plusDays(1))
                .position(-1)
                .build();
        Mockito.when(service.create(
                        todo.getPosition(),
                        todo.getTitle(),
                        todo.getDescription(),
                        todo.getPriority(),
                        todo.getPlannedDate()))
                .thenReturn(todo);
        TodoToCreate todoToCreate = new TodoToCreate();
        todoToCreate.setTitle(todo.getTitle());
        todoToCreate.setDescription(todo.getDescription());
        todoToCreate.setPriority(todo.getPriority());
        todoToCreate.setPlannedDate(todo.getPlannedDate());
        todoToCreate.setPosition(todo.getPosition());
        ErrorResult errorResult = new ErrorResult();
        errorResult.getFieldErrors().add(new FieldValidationError(Fields.position, "must be greater than or equal to 0"));

        mockMvc.perform(MockMvcRequestBuilders.post("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoToCreate)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(ResponseBodyMatchers.responseBody(objectMapper)
                        .containsObjectAsJson(errorResult, ErrorResult.class));

        Mockito.verify(service, Mockito.never())
                .create(todo.getPosition(),
                        todo.getTitle(),
                        todo.getDescription(),
                        todo.getPriority(),
                        todo.getPlannedDate());
    }

    @Test
    void createWithLongDescription() throws Exception {
        Todo todo = Todo.builder()
                .id(0L)
                .title("title")
                .description(RandomStringUtils.random(1025))
                .status(Status.OPEN)
                .creationDate(LocalDate.now())
                .plannedDate(LocalDate.now().plusDays(1))
                .position(1)
                .build();
        Mockito.when(service.create(
                        todo.getPosition(),
                        todo.getTitle(),
                        todo.getDescription(),
                        todo.getPriority(),
                        todo.getPlannedDate()))
                .thenReturn(todo);
        TodoToCreate todoToCreate = new TodoToCreate();
        todoToCreate.setTitle(todo.getTitle());
        todoToCreate.setDescription(todo.getDescription());
        todoToCreate.setPriority(todo.getPriority());
        todoToCreate.setPlannedDate(todo.getPlannedDate());
        todoToCreate.setPosition(todo.getPosition());
        ErrorResult errorResult = new ErrorResult();
        errorResult.getFieldErrors().add(new FieldValidationError(Fields.description, "size must be between 0 and 1024"));

        mockMvc.perform(MockMvcRequestBuilders.post("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoToCreate)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(ResponseBodyMatchers.responseBody(objectMapper)
                        .containsObjectAsJson(errorResult, ErrorResult.class));

        Mockito.verify(service, Mockito.never())
                .create(todo.getPosition(),
                        todo.getTitle(),
                        todo.getDescription(),
                        todo.getPriority(),
                        todo.getPlannedDate());
    }

    @Test
    void createWithEmptyPlannedDate() throws Exception {
        Todo todo = Todo.builder()
                .id(0L)
                .title("title")
                .status(Status.OPEN)
                .creationDate(LocalDate.now())
                .plannedDate(LocalDate.now().plusDays(1))
                .position(1)
                .build();
        Mockito.when(service.create(
                        todo.getPosition(),
                        todo.getTitle(),
                        todo.getDescription(),
                        todo.getPriority(),
                        todo.getPlannedDate()))
                .thenReturn(todo);
        TodoToCreate todoToCreate = new TodoToCreate();
        todoToCreate.setTitle(todo.getTitle());
        todoToCreate.setDescription(todo.getDescription());
        todoToCreate.setPriority(todo.getPriority());
        todoToCreate.setPosition(todo.getPosition());
        ErrorResult errorResult = new ErrorResult();
        errorResult.getFieldErrors().add(new FieldValidationError(Fields.plannedDate, "must not be null"));

        mockMvc.perform(MockMvcRequestBuilders.post("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoToCreate)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(ResponseBodyMatchers.responseBody(objectMapper)
                        .containsObjectAsJson(errorResult, ErrorResult.class));

        Mockito.verify(service, Mockito.never())
                .create(todo.getPosition(),
                        todo.getTitle(),
                        todo.getDescription(),
                        todo.getPriority(),
                        todo.getPlannedDate());
    }

    @Test
    void createWithEmptyContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/todo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    void update() throws Exception {
        Todo todo = Todo.builder()
                .id(0L)
                .title("title")
                .status(Status.OPEN)
                .creationDate(LocalDate.now())
                .plannedDate(LocalDate.now().plusDays(1))
                .position(1L)
                .build();
        TodoInfo todoInfo = modelMapper.map(todo, TodoInfo.class);
        Mockito.when(service.save(todo))
                .thenReturn(todo);

        mockMvc.perform(MockMvcRequestBuilders.put("/todo/{id}", todo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoInfo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(ResponseBodyMatchers.responseBody(objectMapper)
                        .containsObjectAsJson(todoInfo, TodoInfo.class));

        Mockito.verify(service, Mockito.times(1))
                .save(todo);
    }

    @Test
    void updateWithEmptyTitle() throws Exception {
        Todo todo = Todo.builder()
                .id(0L)
                .title("")
                .status(Status.OPEN)
                .creationDate(LocalDate.now())
                .plannedDate(LocalDate.now().plusDays(1))
                .position(1L)
                .build();
        ErrorResult errorResult = new ErrorResult();
        List<FieldValidationError> fieldErrors = errorResult.getFieldErrors();
        fieldErrors.add(new FieldValidationError(Fields.title, "must not be blank"));
        fieldErrors.add(new FieldValidationError(Fields.title, "size must be between 1 and 32"));
        TodoToUpdate todoToUpdate = modelMapper.map(todo, TodoToUpdate.class);

        validateUpdateAndResult(todo, todoToUpdate, errorResult);
    }

    @Test
    void updateWithLongTitle() throws Exception {
        Todo todo = Todo.builder()
                .id(0L)
                .title(RandomStringUtils.random(33))
                .status(Status.OPEN)
                .creationDate(LocalDate.now())
                .plannedDate(LocalDate.now().plusDays(1))
                .position(1L)
                .build();
        ErrorResult errorResult = new ErrorResult();
        errorResult.getFieldErrors().add(new FieldValidationError(Fields.title, "size must be between 1 and 32"));
        TodoToUpdate todoToUpdate = modelMapper.map(todo, TodoToUpdate.class);

        validateUpdateAndResult(todo, todoToUpdate, errorResult);
    }

    @Test
    void updateWithNegativeId() throws Exception {
        Todo todo = Todo.builder()
                .id(-1L)
                .title("title")
                .status(Status.OPEN)
                .creationDate(LocalDate.now())
                .plannedDate(LocalDate.now().plusDays(1))
                .position(1L)
                .build();
        ErrorResult errorResult = new ErrorResult();
        errorResult.getFieldErrors().add(new FieldValidationError(Fields.id, "must be greater than or equal to 0"));
        TodoToUpdate todoToUpdate = modelMapper.map(todo, TodoToUpdate.class);

        MvcResult result = validateUpdate(todo, todoToUpdate);
        Assertions.assertThat(result.getResponse().getContentAsString())
                .isEqualTo("Not valid due to validation error: update.id: must be greater than or equal to 0");
    }

    @Test
    void updateWithLongDescription() throws Exception {
        Todo todo = Todo.builder()
                .id(0L)
                .title("title")
                .description(RandomStringUtils.random(1025))
                .status(Status.OPEN)
                .creationDate(LocalDate.now())
                .plannedDate(LocalDate.now().plusDays(1))
                .position(1L)
                .build();
        ErrorResult errorResult = new ErrorResult();
        errorResult.getFieldErrors().add(new FieldValidationError(Fields.description, "size must be between 0 and 1024"));
        TodoToUpdate todoToUpdate = modelMapper.map(todo, TodoToUpdate.class);

        validateUpdateAndResult(todo, todoToUpdate, errorResult);
    }

    @Test
    void updateWithEmptyStatus() throws Exception {
        Todo todo = Todo.builder()
                .id(0L)
                .title("title")
                .status(Status.OPEN)
                .creationDate(LocalDate.now())
                .plannedDate(LocalDate.now().plusDays(1))
                .position(1L)
                .build();
        TodoToUpdate todoToUpdate = new TodoToUpdate();
        todoToUpdate.setTitle(todo.getTitle());
        ErrorResult errorResult = new ErrorResult();
        errorResult.getFieldErrors().add(new FieldValidationError(Fields.status, "must not be null"));

        validateUpdateAndResult(todo, todoToUpdate, errorResult);
    }

    @Test
    void updateWithEmptyContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/todo/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    private void validateUpdateAndResult(Todo todo, TodoToUpdate todoToUpdate, ErrorResult errorResult) throws Exception {
        MvcResult result = validateUpdate(todo, todoToUpdate);
        ResponseBodyMatchers.responseBody(objectMapper)
                .containsObjectAsJson(errorResult, ErrorResult.class)
                .match(result);
    }

    private MvcResult validateUpdate(Todo todo, TodoToUpdate todoToUpdate) throws Exception {
        Mockito.when(service.save(todo))
                .thenReturn(todo);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/todo/{id}", todo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoToUpdate)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Mockito.verify(service, Mockito.never())
                .save(todo);
        return result;
    }

    @Test
    void swap() throws Exception {
        SwapParameter swapParameter = new SwapParameter();
        swapParameter.setFrom(1);
        swapParameter.setTo(2);

        mockMvc.perform(MockMvcRequestBuilders.put("/todo/swap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(swapParameter)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(service, Mockito.times(1))
                .swap(swapParameter.getFrom(), swapParameter.getTo());
    }

    @Test
    void swapWithNegativePositions() throws Exception {
        SwapParameter swapParameter = new SwapParameter();
        swapParameter.setFrom(-1);
        swapParameter.setTo(-2);
        ErrorResult errorResult = new ErrorResult();
        List<FieldValidationError> fieldErrors = errorResult.getFieldErrors();
        fieldErrors.add(new FieldValidationError("from", "Position \"from\" can't be negative"));
        fieldErrors.add(new FieldValidationError("to", "Position \"to\" can't be negative"));

        mockMvc.perform(MockMvcRequestBuilders.put("/todo/swap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(swapParameter)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(ResponseBodyMatchers.responseBody(objectMapper)
                        .containsObjectAsJson(errorResult, ErrorResult.class));

        Mockito.verify(service, Mockito.never())
                .swap(swapParameter.getFrom(), swapParameter.getTo());
    }

    @Test
    void swapWithEmptyContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/todo/swap")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void all() throws Exception {
        List<Todo> todos = List.of(
                Todo.builder()
                        .id(1L)
                        .title("title1")
                        .status(Status.OPEN)
                        .creationDate(LocalDate.now())
                        .plannedDate(LocalDate.now().plusDays(1))
                        .position(0)
                        .build(),
                Todo.builder()
                        .id(2L)
                        .title("title2")
                        .status(Status.OPEN)
                        .creationDate(LocalDate.now())
                        .plannedDate(LocalDate.now().plusDays(1))
                        .position(1)
                        .build()
        );
        Mockito.when(service.getAll())
                .thenReturn(todos);
        List<TodoInfo> todoInfos = todos.stream()
                .map(todo -> modelMapper.map(todo, TodoInfo.class))
                .toList();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/todo/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Assertions.assertThat(result.getResponse().getContentAsString())
                .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(todoInfos));
    }

    @Test
    void deleteById() throws Exception {
        Long id = 1L;
        Todo todo = Todo.builder()
                .id(id)
                .title("title")
                .status(Status.OPEN)
                .creationDate(LocalDate.now())
                .plannedDate(LocalDate.now().plusDays(1))
                .position(0)
                .build();
        Mockito.when(service.deleteById(id))
                .thenReturn(todo);

        mockMvc.perform(MockMvcRequestBuilders.delete("/todo/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(service, Mockito.times(1))
                .deleteById(id);
    }

    @Test
    void deleteByIdWithNegativeId() throws Exception {
        Long id = -1L;

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/todo/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Assertions.assertThat(result.getResponse().getContentAsString())
                .isEqualTo("Not valid due to validation error: delete.id: must be greater than or equal to 0");

        Mockito.verify(service, Mockito.never()).deleteById(id);
    }

    @Test
    void deleteByIdWithStringIdPathVariable() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/todo/{id}", "test"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deleteAllById() throws Exception {
        Set<Long> ids = Set.of(1L, 2L);
        List<Todo> todo = List.of(
                Todo.builder()
                        .id(1L)
                        .title("title1")
                        .status(Status.OPEN)
                        .creationDate(LocalDate.now())
                        .plannedDate(LocalDate.now().plusDays(1))
                        .position(0)
                        .build(),
                Todo.builder()
                        .id(2L)
                        .title("title2")
                        .status(Status.OPEN)
                        .creationDate(LocalDate.now())
                        .plannedDate(LocalDate.now().plusDays(1))
                        .position(1)
                        .build()
        );
        Mockito.when(service.deleteAllById(ids))
                .thenReturn(todo);

        mockMvc.perform(MockMvcRequestBuilders.delete("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", ids.stream().map(Object::toString).toArray(String[]::new)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(service, Mockito.times(1))
                .deleteAllById(ids);
    }

    @Test
    void deleteAllByIdWithSameIds() throws Exception {
        List<Long> ids = List.of(1L, 1L);
        List<Todo> todo = List.of(
                Todo.builder()
                        .id(1L)
                        .title("title")
                        .status(Status.OPEN)
                        .creationDate(LocalDate.now())
                        .plannedDate(LocalDate.now().plusDays(1))
                        .position(0)
                        .build()
        );
        Mockito.when(service.deleteAllById(new HashSet<>(ids)))
                .thenReturn(todo);

        mockMvc.perform(MockMvcRequestBuilders.delete("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", ids.stream().map(Object::toString).toArray(String[]::new)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(service, Mockito.times(1))
                .deleteAllById(new HashSet<>(ids));
    }

    @Test
    void deleteAllByIdWithNegativeIds() throws Exception {
        Set<Long> ids = Set.of(1L, -2L);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", ids.stream().map(Object::toString).toArray(String[]::new)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Assertions.assertThat(result.getResponse().getContentAsString())
                .isEqualTo("Not valid due to validation error: ID set must not contain negative IDs.");

        Mockito.verify(service, Mockito.never())
                .deleteAllById(ids);
    }

    @Test
    void deleteAllByIdWithoutIds() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", ""))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Assertions.assertThat(result.getResponse().getContentAsString())
                .isEqualTo("Not valid due to validation error: delete.ids: must not be empty");
    }

    @Test
    void deleteAllByIdWithStringIds() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", Set.of("test1", "test2").toArray(String[]::new)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deleteAll() throws Exception {
        List<Todo> todo = List.of(
                Todo.builder()
                        .id(1L)
                        .title("title1")
                        .status(Status.OPEN)
                        .creationDate(LocalDate.now())
                        .plannedDate(LocalDate.now().plusDays(1))
                        .position(0)
                        .build(),
                Todo.builder()
                        .id(2L)
                        .title("title2")
                        .status(Status.OPEN)
                        .creationDate(LocalDate.now())
                        .plannedDate(LocalDate.now().plusDays(1))
                        .position(1)
                        .build()
        );

        Mockito.when(service.deleteAll())
                .thenReturn(todo);

        mockMvc.perform(MockMvcRequestBuilders.delete("/todo/all"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(service, Mockito.times(1))
                .deleteAll();
    }

    @Test
    void getById() throws Exception {
        Long id = 1L;
        Todo todo = Todo.builder()
                .id(id)
                .title("title")
                .status(Status.OPEN)
                .creationDate(LocalDate.now())
                .plannedDate(LocalDate.now().plusDays(1))
                .position(0)
                .build();
        TodoInfo expectedPriorityInfo = modelMapper.map(todo, TodoInfo.class);
        Mockito.when(service.getById(id))
                .thenReturn(todo);

        mockMvc.perform(MockMvcRequestBuilders.get("/todo/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(ResponseBodyMatchers.responseBody(objectMapper)
                        .containsObjectAsJson(expectedPriorityInfo, TodoInfo.class));

        Mockito.verify(service, Mockito.times(1))
                .getById(id);
    }

    @Test
    void getByIdWithNegativeId() throws Exception {
        Long id = -1L;

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/todo/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Assertions.assertThat(result.getResponse().getContentAsString())
                .isEqualTo("Not valid due to validation error: getById.id: must be greater than or equal to 0");

        Mockito.verify(service, Mockito.never()).getById(id);
    }

    @Test
    void getByIdWithStringId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/todo/{id}", "test"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getAllById() throws Exception {
        Set<Long> ids = Set.of(1L, 2L);
        List<Todo> todos = List.of(
                Todo.builder()
                        .id(1L)
                        .title("title1")
                        .status(Status.OPEN)
                        .creationDate(LocalDate.now())
                        .plannedDate(LocalDate.now().plusDays(1))
                        .position(0)
                        .build(),
                Todo.builder()
                        .id(2L)
                        .title("title2")
                        .status(Status.OPEN)
                        .creationDate(LocalDate.now())
                        .plannedDate(LocalDate.now().plusDays(1))
                        .position(1)
                        .build()
        );
        List<TodoInfo> expectedPriorityInfos = todos.stream()
                .map(todo -> modelMapper.map(todo, TodoInfo.class))
                .toList();
        Mockito.when(service.getAllById(ids))
                .thenReturn(todos);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/todo/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", ids.stream().map(Object::toString).toArray(String[]::new)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Assertions.assertThat(result.getResponse().getContentAsString())
                .isEqualTo(objectMapper.writeValueAsString(expectedPriorityInfos));

        Mockito.verify(service, Mockito.times(1))
                .getAllById(ids);
    }

    @Test
    void getAllByIdWithNegativeIds() throws Exception {
        Set<Long> ids = Set.of(1L, -2L);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/todo/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", ids.stream().map(Object::toString).toArray(String[]::new)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Assertions.assertThat(result.getResponse().getContentAsString())
                .isEqualTo("Not valid due to validation error: ID set must not contain negative IDs.");

        Mockito.verify(service, Mockito.never())
                .getAllById(ids);
    }

    @Test
    void getAllByIdWithStringIds() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/todo/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", "test1", "test2"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getAllByIdWithoutIds() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/todo/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", ""))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Assertions.assertThat(result.getResponse().getContentAsString())
                .isEqualTo("Not valid due to validation error: getAllById.ids: must not be empty");
    }

    @Test
    void page() throws Exception {
        List<Todo> todos = List.of(
                Todo.builder()
                        .id(1L)
                        .title("title1")
                        .status(Status.OPEN)
                        .creationDate(LocalDate.now())
                        .plannedDate(LocalDate.now().plusDays(1))
                        .position(0)
                        .build(),
                Todo.builder()
                        .id(2L)
                        .title("title2")
                        .status(Status.OPEN)
                        .creationDate(LocalDate.now())
                        .plannedDate(LocalDate.now().plusDays(1))
                        .position(1)
                        .build()
        );
        PageRequest pageRequest = PageRequest.of(0, 20, Direction.ASC, Todo.Fields.position);
        Page<Todo> page = new PageImpl<>(todos, pageRequest, 2);
        Page<TodoInfo> expectedPage = page.map(todo -> modelMapper.map(todo, TodoInfo.class));
        Mockito.when(repository.findAll(pageRequest))
                .thenReturn(page);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/todo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Assertions.assertThat(result.getResponse().getContentAsString())
                .isEqualTo(objectMapper.writeValueAsString(expectedPage));

        Mockito.verify(repository, Mockito.times(1))
                .findAll(pageRequest);
    }
}