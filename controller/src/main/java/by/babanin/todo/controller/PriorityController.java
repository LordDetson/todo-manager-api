package by.babanin.todo.controller;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.ValidationException;
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

import by.babanin.todo.application.repository.PriorityRepository;
import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.controller.dto.PriorityInfo;
import by.babanin.todo.controller.dto.SwapParameter;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Priority.Fields;

@RestController
@RequestMapping("/priorities")
@Validated
public class PriorityController {

    private final PriorityService priorityService;
    private final PriorityRepository priorityRepository;
    private final ModelMapper modelMapper;

    public PriorityController(PriorityService priorityService, PriorityRepository priorityRepository, ModelMapper modelMapper) {
        this.priorityService = priorityService;
        this.priorityRepository = priorityRepository;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    PriorityInfo create(@RequestBody @Valid PriorityInfo priorityInfo) {
        Priority priority;
        Long position = priorityInfo.getPosition();
        if(position == null) {
            priority = priorityService.create(priorityInfo.getName());
        }
        else {
            priority = priorityService.create(position, priorityInfo.getName());
        }
        return modelMapper.map(priority, PriorityInfo.class);
    }

    @PutMapping("{id}")
    PriorityInfo rename(
            @PathVariable("id") @PositiveOrZero Long id,
            @RequestBody @Valid PriorityInfo priorityInfo) {
        Priority priority = priorityService.rename(id, priorityInfo.getName());
        return modelMapper.map(priority, PriorityInfo.class);
    }

    @PutMapping("/swap")
    void swap(@RequestBody @Valid SwapParameter swapParameter) {
        priorityService.swap(swapParameter.getFrom(), swapParameter.getTo());
    }

    @DeleteMapping("{id}")
    void delete(@PathVariable("id") @PositiveOrZero Long id) {
        priorityService.deleteById(id);
    }

    @DeleteMapping
    void delete(@RequestParam("ids") @NotEmpty Set<Long> ids) {
        assertNegativeIds(ids);
        priorityService.deleteAllById(ids);
    }

    @DeleteMapping("/all")
    void delete() {
        priorityService.deleteAll();
    }

    @GetMapping
    Page<PriorityInfo> page(@PageableDefault(size = 20, sort = Fields.position) Pageable pageable) {
        return priorityRepository.findAll(pageable)
                .map(priority -> modelMapper.map(priority, PriorityInfo.class));
    }

    @GetMapping("/all")
    List<PriorityInfo> all() {
        return priorityService.getAll().stream()
                .map(priority -> modelMapper.map(priority, PriorityInfo.class))
                .toList();
    }

    @GetMapping("/search")
    List<PriorityInfo> getAllById(@RequestParam("ids") @NotEmpty Set<Long> ids) {
        assertNegativeIds(ids);
        return priorityService.getAllById(ids).stream()
                .map(priority -> modelMapper.map(priority, PriorityInfo.class))
                .toList();
    }

    @GetMapping("{id}")
    PriorityInfo getById(@PathVariable("id") @PositiveOrZero Long id) {
        Priority priority = priorityService.getById(id);
        return modelMapper.map(priority, PriorityInfo.class);
    }

    private static void assertNegativeIds(Set<Long> ids) {
        for(Long id : ids){
            if(id < 0) {
                throw new ValidationException("ID set must not contain negative IDs.");
            }
        }
    }
}
