package by.babanin.todo.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import by.babanin.todo.application.repository.PriorityRepository;
import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.controller.dto.PriorityInfo;
import by.babanin.todo.controller.dto.SwapParameter;
import by.babanin.todo.controller.exception.handler.ErrorResult;
import by.babanin.todo.controller.exception.handler.FieldValidationError;
import by.babanin.todo.controller.util.ResponseBodyMatchers;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Priority.Fields;

@WebMvcTest(PriorityController.class)
class PriorityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    @MockBean
    private PriorityService service;

    @MockBean
    private PriorityRepository repository;

    @TestConfiguration
    static class AdditionalConfiguration {

        @Bean
        ModelMapper modelMapper() {
            return new ModelMapper();
        }
    }

    @Test
    void create() throws Exception {
        String name = "name";
        Priority priority = Priority.builder()
                .id(0L)
                .name(name)
                .position(0)
                .build();
        PriorityInfo expectedPriorityInfo = modelMapper.map(priority, PriorityInfo.class);
        Mockito.when(service.create(name))
                .thenReturn(priority);
        PriorityInfo priorityInfo = new PriorityInfo();
        priorityInfo.setName(name);

        mockMvc.perform(MockMvcRequestBuilders.post("/priorities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(priorityInfo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(ResponseBodyMatchers.responseBody(objectMapper)
                        .containsObjectAsJson(expectedPriorityInfo, PriorityInfo.class));

        Mockito.verify(service, Mockito.times(1))
                .create(name);
    }

    @Test
    void createWithPosition() throws Exception {
        String name = "name";
        long position = 1;
        Priority priority = Priority.builder()
                .id(0L)
                .name(name)
                .position(1)
                .build();
        PriorityInfo expectedPriorityInfo = modelMapper.map(priority, PriorityInfo.class);
        Mockito.when(service.create(position, name))
                .thenReturn(priority);
        PriorityInfo priorityInfo = new PriorityInfo();
        priorityInfo.setName(name);
        priorityInfo.setPosition(position);

        mockMvc.perform(MockMvcRequestBuilders.post("/priorities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(priorityInfo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(ResponseBodyMatchers.responseBody(objectMapper)
                        .containsObjectAsJson(expectedPriorityInfo, PriorityInfo.class));

        Mockito.verify(service, Mockito.times(1))
                .create(position, name);
    }

    @Test
    void createWithEmptyName() throws Exception {
        String name = "";
        PriorityInfo priorityInfo = new PriorityInfo();
        priorityInfo.setName(name);
        ErrorResult errorResult = new ErrorResult();
        errorResult.getFieldErrors().add(new FieldValidationError("name", "Name can't be empty"));

        mockMvc.perform(MockMvcRequestBuilders.post("/priorities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(priorityInfo)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(ResponseBodyMatchers.responseBody(objectMapper)
                        .containsObjectAsJson(errorResult, ErrorResult.class));

        Mockito.verify(service, Mockito.never())
                .create(name);
    }

    @Test
    void createWithNegativeId() throws Exception {
        long id = -1;
        String name = "name";
        PriorityInfo priorityInfo = new PriorityInfo();
        priorityInfo.setId(id);
        priorityInfo.setName(name);
        ErrorResult errorResult = new ErrorResult();
        errorResult.getFieldErrors().add(new FieldValidationError("id", "Id can't be negative"));

        mockMvc.perform(MockMvcRequestBuilders.post("/priorities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(priorityInfo)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(ResponseBodyMatchers.responseBody(objectMapper)
                        .containsObjectAsJson(errorResult, ErrorResult.class));

        Mockito.verify(service, Mockito.never())
                .create(name);
    }

    @Test
    void createWithNegativePosition() throws Exception {
        long position = -1;
        String name = "name";
        PriorityInfo priorityInfo = new PriorityInfo();
        priorityInfo.setName(name);
        priorityInfo.setPosition(position);
        ErrorResult errorResult = new ErrorResult();
        errorResult.getFieldErrors().add(new FieldValidationError("position", "Position can't be negative"));

        mockMvc.perform(MockMvcRequestBuilders.post("/priorities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(priorityInfo)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(ResponseBodyMatchers.responseBody(objectMapper)
                        .containsObjectAsJson(errorResult, ErrorResult.class));

        Mockito.verify(service, Mockito.never())
                .create(position, name);
    }

    @Test
    void createWithEmptyContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/priorities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    void rename() throws Exception {
        long id = 1;
        String name = "name";
        Priority priority = Priority.builder()
                .id(id)
                .name(name)
                .position(0)
                .build();
        Mockito.when(service.rename(id, name))
                .thenReturn(priority);
        PriorityInfo expectedPriorityInfo = modelMapper.map(priority, PriorityInfo.class);
        PriorityInfo priorityInfo = new PriorityInfo();
        priorityInfo.setName(name);

        mockMvc.perform(MockMvcRequestBuilders.put("/priorities/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(priorityInfo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(ResponseBodyMatchers.responseBody(objectMapper)
                        .containsObjectAsJson(expectedPriorityInfo, PriorityInfo.class));

        Mockito.verify(service, Mockito.times(1))
                .rename(id, name);
    }

    @Test
    void renameWithEmptyName() throws Exception {
        long id = 10;
        String name = "";
        Priority priority = Priority.builder()
                .id(id)
                .name(name)
                .position(0)
                .build();
        PriorityInfo priorityInfo = modelMapper.map(priority, PriorityInfo.class);
        ErrorResult errorResult = new ErrorResult();
        errorResult.getFieldErrors().add(new FieldValidationError("name", "Name can't be empty"));

        mockMvc.perform(MockMvcRequestBuilders.put("/priorities/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(priorityInfo)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(ResponseBodyMatchers.responseBody(objectMapper)
                        .containsObjectAsJson(errorResult, ErrorResult.class));

        Mockito.verify(service, Mockito.never())
                .rename(id, name);
    }

    @Test
    void renameWithNegativeId() throws Exception {
        long id = -1;
        String name = "name";
        Priority priority = Priority.builder()
                .id(id)
                .name(name)
                .position(0)
                .build();
        PriorityInfo priorityInfo = modelMapper.map(priority, PriorityInfo.class);
        ErrorResult errorResult = new ErrorResult();
        errorResult.getFieldErrors().add(new FieldValidationError("id", "Id can't be negative"));

        mockMvc.perform(MockMvcRequestBuilders.put("/priorities/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(priorityInfo)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(ResponseBodyMatchers.responseBody(objectMapper)
                        .containsObjectAsJson(errorResult, ErrorResult.class));

        Mockito.verify(service, Mockito.never())
                .rename(id, name);
    }

    @Test
    void renameWithStringIdPathVariable() throws Exception {
        long id = 1;
        String name = "name";
        Priority priority = Priority.builder()
                .id(id)
                .name(name)
                .position(0)
                .build();
        PriorityInfo priorityInfo = modelMapper.map(priority, PriorityInfo.class);

        mockMvc.perform(MockMvcRequestBuilders.put("/priorities/{id}", "id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(priorityInfo)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(service, Mockito.never())
                .rename(id, name);
    }

    @Test
    void renameWithEmptyContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/priorities/{id}", 0)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void swap() throws Exception {
        SwapParameter swapParameter = new SwapParameter();
        swapParameter.setFrom(1);
        swapParameter.setTo(2);

        mockMvc.perform(MockMvcRequestBuilders.put("/priorities/swap")
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

        mockMvc.perform(MockMvcRequestBuilders.put("/priorities/swap")
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
        mockMvc.perform(MockMvcRequestBuilders.put("/priorities/swap")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void all() throws Exception {
        List<Priority> priorities = List.of(
                Priority.builder()
                        .id(1L)
                        .name("name1")
                        .position(0)
                        .build(),
                Priority.builder()
                        .id(2L)
                        .name("name2")
                        .position(1)
                        .build()
        );
        Mockito.when(service.getAll())
                .thenReturn(priorities);
        List<PriorityInfo> priorityInfos = priorities.stream()
                .map(priority -> modelMapper.map(priority, PriorityInfo.class))
                .toList();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/priorities/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Assertions.assertThat(result.getResponse().getContentAsString())
                .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(priorityInfos));
    }

    @Test
    void deleteById() throws Exception {
        Long id = 1L;
        Priority priority = Priority.builder()
                .id(id)
                .name("test")
                .position(id)
                .build();
        Mockito.when(service.deleteById(id))
                .thenReturn(priority);

        mockMvc.perform(MockMvcRequestBuilders.delete("/priorities/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(service, Mockito.times(1))
                .deleteById(id);
    }

    @Test
    void deleteByIdWithNegativeId() throws Exception {
        Long id = -1L;

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/priorities/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Assertions.assertThat(result.getResponse().getContentAsString())
                .isEqualTo("Not valid due to validation error: delete.id: must be greater than or equal to 0");

        Mockito.verify(service, Mockito.never()).deleteById(id);
    }

    @Test
    void deleteByIdWithStringIdPathVariable() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/priorities/{id}", "test"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deleteAllById() throws Exception {
        Set<Long> ids = Set.of(1L, 2L);
        List<Priority> priorities = List.of(
                Priority.builder()
                        .id(1L)
                        .name("name1")
                        .position(0)
                        .build(),
                Priority.builder()
                        .id(2L)
                        .name("name2")
                        .position(1)
                        .build()
        );
        Mockito.when(service.deleteAllById(ids))
                .thenReturn(priorities);

        mockMvc.perform(MockMvcRequestBuilders.delete("/priorities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", ids.stream().map(Object::toString).toArray(String[]::new)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(service, Mockito.times(1))
                .deleteAllById(ids);
    }

    @Test
    void deleteAllByIdWithSameIds() throws Exception {
        List<Long> ids = List.of(1L, 1L);
        List<Priority> priorities = List.of(
                Priority.builder()
                        .id(1L)
                        .name("name1")
                        .position(0)
                        .build()
        );
        Mockito.when(service.deleteAllById(new HashSet<>(ids)))
                .thenReturn(priorities);

        mockMvc.perform(MockMvcRequestBuilders.delete("/priorities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", ids.stream().map(Object::toString).toArray(String[]::new)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(service, Mockito.times(1))
                .deleteAllById(new HashSet<>(ids));
    }

    @Test
    void deleteAllByIdWithNegativeIds() throws Exception {
        Set<Long> ids = Set.of(1L, -2L);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/priorities")
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
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/priorities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", ""))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Assertions.assertThat(result.getResponse().getContentAsString())
                .isEqualTo("Not valid due to validation error: delete.ids: must not be empty");
    }

    @Test
    void deleteAllByIdWithStringIds() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/priorities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", Set.of("test1", "test2").toArray(String[]::new)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deleteAll() throws Exception {
        List<Priority> priorities = List.of(
                Priority.builder()
                        .id(1L)
                        .name("name1")
                        .position(0)
                        .build(),
                Priority.builder()
                        .id(2L)
                        .name("name2")
                        .position(1)
                        .build()
        );

        Mockito.when(service.deleteAll())
                .thenReturn(priorities);

        mockMvc.perform(MockMvcRequestBuilders.delete("/priorities/all"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(service, Mockito.times(1))
                .deleteAll();
    }

    @Test
    void getById() throws Exception {
        Long id = 1L;
        Priority priority = Priority.builder()
                .id(id)
                .name("test")
                .position(0)
                .build();
        PriorityInfo expectedPriorityInfo = modelMapper.map(priority, PriorityInfo.class);
        Mockito.when(service.getById(id))
                .thenReturn(priority);

        mockMvc.perform(MockMvcRequestBuilders.get("/priorities/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(ResponseBodyMatchers.responseBody(objectMapper)
                        .containsObjectAsJson(expectedPriorityInfo, PriorityInfo.class));

        Mockito.verify(service, Mockito.times(1))
                .getById(id);
    }

    @Test
    void getByIdWithNegativeId() throws Exception {
        Long id = -1L;

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/priorities/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Assertions.assertThat(result.getResponse().getContentAsString())
                .isEqualTo("Not valid due to validation error: getById.id: must be greater than or equal to 0");

        Mockito.verify(service, Mockito.never()).getById(id);
    }

    @Test
    void getByIdWithStringId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/priorities/{id}", "test"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getAllById() throws Exception {
        Set<Long> ids = Set.of(1L, 2L);
        List<Priority> priorities = List.of(
                Priority.builder()
                        .id(1L)
                        .name("name1")
                        .position(0)
                        .build(),
                Priority.builder()
                        .id(2L)
                        .name("name2")
                        .position(1)
                        .build()
        );
        List<PriorityInfo> expectedPriorityInfos = priorities.stream()
                .map(priority -> modelMapper.map(priority, PriorityInfo.class))
                .toList();
        Mockito.when(service.getAllById(ids))
                .thenReturn(priorities);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/priorities/search")
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

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/priorities/search")
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
        mockMvc.perform(MockMvcRequestBuilders.get("/priorities/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", "test1", "test2"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getAllByIdWithoutIds() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/priorities/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", ""))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Assertions.assertThat(result.getResponse().getContentAsString())
                .isEqualTo("Not valid due to validation error: getAllById.ids: must not be empty");
    }

    @Test
    void page() throws Exception {
        List<Priority> priorities = List.of(
                Priority.builder()
                        .id(1L)
                        .name("name1")
                        .position(0)
                        .build(),
                Priority.builder()
                        .id(2L)
                        .name("name2")
                        .position(1)
                        .build()
        );
        PageRequest pageRequest = PageRequest.of(0, 20, Direction.ASC, Fields.position);
        Page<Priority> page = new PageImpl<>(priorities, pageRequest, 2);
        Page<PriorityInfo> expectedPage = page.map(priority -> modelMapper.map(priority, PriorityInfo.class));
        Mockito.when(repository.findAll(pageRequest))
                .thenReturn(page);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/priorities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Assertions.assertThat(result.getResponse().getContentAsString())
                .isEqualTo(objectMapper.writeValueAsString(expectedPage));

        Mockito.verify(repository, Mockito.times(1))
                .findAll(pageRequest);
    }
}