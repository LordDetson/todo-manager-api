package by.babanin.todo.controller;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PositiveOrZero;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import by.babanin.todo.application.repository.TodoRepository;
import by.babanin.todo.application.service.TodoService;
import by.babanin.todo.controller.dto.SwapParameter;
import by.babanin.todo.controller.dto.TodoInfo;
import by.babanin.todo.controller.dto.TodoToCreate;
import by.babanin.todo.controller.dto.TodoToUpdate;
import by.babanin.todo.model.Priority.Fields;
import by.babanin.todo.model.Todo;

@RestController
@RequestMapping("/todo")
@Validated
public class TodoController {

    private final TodoService todoService;
    private final TodoRepository todoRepository;
    private final ModelMapper modelMapper;

    public TodoController(TodoService todoService, TodoRepository todoRepository, ModelMapper modelMapper) {
        this.todoService = todoService;
        this.todoRepository = todoRepository;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    TodoInfo create(@RequestBody @Valid TodoToCreate todoToCreate) {
        Todo todo;
        Long position = todoToCreate.getPosition();
        if(position == null) {
            todo = todoService.create(
                    todoToCreate.getTitle(),
                    todoToCreate.getDescription(),
                    todoToCreate.getPriority(),
                    todoToCreate.getPlannedDate()
            );
        }
        else {
            todo = todoService.create(
                    position,
                    todoToCreate.getTitle(),
                    todoToCreate.getDescription(),
                    todoToCreate.getPriority(),
                    todoToCreate.getPlannedDate()
            );
        }
        return modelMapper.map(todo, TodoInfo.class);
    }

    @PutMapping("{id}")
    TodoInfo update(
            @PathVariable("id") @PositiveOrZero Long id,
            @RequestBody @Valid TodoToUpdate todoToUpdate) {
        Todo todo = modelMapper.map(todoToUpdate, Todo.class);
        todo.setId(id);
        todo = todoService.save(todo);
        return modelMapper.map(todo, TodoInfo.class);
    }

    @PutMapping("/swap")
    void swap(@RequestBody @Valid SwapParameter swapParameter) {
        todoService.swap(swapParameter.getFrom(), swapParameter.getTo());
    }

    @DeleteMapping("{id}")
    void delete(@PathVariable("id") @PositiveOrZero Long id) {
        todoService.deleteById(id);
    }

    @DeleteMapping
    void delete(@RequestParam("ids") @NotEmpty Set<Long> ids) {
        CheckUtils.assertNegativeIds(ids);
        todoService.deleteAllById(ids);
    }

    @DeleteMapping("/all")
    void delete() {
        todoService.deleteAll();
    }

    @GetMapping
    Page<TodoInfo> page(@PageableDefault(size = 20, sort = Fields.position) Pageable pageable) {
        return todoRepository.findAll(pageable)
                .map(priority -> modelMapper.map(priority, TodoInfo.class));
    }

    @GetMapping("/all")
    List<TodoInfo> all() {
        return todoService.getAll().stream()
                .map(priority -> modelMapper.map(priority, TodoInfo.class))
                .toList();
    }

    @GetMapping("/search")
    List<TodoInfo> getAllById(@RequestParam("ids") @NotEmpty Set<Long> ids) {
        CheckUtils.assertNegativeIds(ids);
        return todoService.getAllById(ids).stream()
                .map(priority -> modelMapper.map(priority, TodoInfo.class))
                .toList();
    }

    @GetMapping("{id}")
    TodoInfo getById(@PathVariable("id") @PositiveOrZero Long id) {
        Todo todo = todoService.getById(id);
        return modelMapper.map(todo, TodoInfo.class);
    }
}
