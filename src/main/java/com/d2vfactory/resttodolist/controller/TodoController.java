package com.d2vfactory.resttodolist.controller;

import com.d2vfactory.resttodolist.model.common.Status;
import com.d2vfactory.resttodolist.model.dto.TodoDTO;
import com.d2vfactory.resttodolist.model.form.StatusForm;
import com.d2vfactory.resttodolist.model.form.TodoForm;
import com.d2vfactory.resttodolist.model.resource.ErrorResource;
import com.d2vfactory.resttodolist.model.resource.TodoResource;
import com.d2vfactory.resttodolist.service.TodoCommandService;
import com.d2vfactory.resttodolist.service.TodoQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RequestMapping("/api/todo")
@RestController
public class TodoController {

    private final TodoQueryService queryService;
    private final TodoCommandService commandService;

    public TodoController(TodoQueryService queryService, TodoCommandService commandService) {
        this.queryService = queryService;
        this.commandService = commandService;
    }

    @GetMapping
    public ResponseEntity getTodoPages(Pageable pageable,
                                       PagedResourcesAssembler<TodoDTO> assembler) {

        PageRequest pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().isUnsorted() ? Sort.by("id").ascending() : pageable.getSort()
        );

        Page<TodoDTO> pages = queryService.getTodoPages(pageRequest);
        PagedResources<TodoResource> pageResources = assembler.toResource(pages, TodoResource::new);
        return ResponseEntity.ok(pageResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getTodo(@PathVariable Long id) {
        TodoResource todoResource = new TodoResource(queryService.getTodo(id));
        return ResponseEntity.ok(todoResource);
    }

    @PostMapping
    public ResponseEntity postTodo(@RequestBody @Valid TodoForm todoForm, Errors errors) {
        if (errors.hasErrors())
            return badRequest(errors);

        TodoDTO todo = commandService.createTodo(todoForm.getContent(), todoForm.getReferenceIds());

        URI createdUri = linkTo(TodoController.class).slash(todo.getId()).toUri();

        TodoResource todoResource = new TodoResource(todo);
        return ResponseEntity.created(createdUri).body(todoResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity putTodo(@PathVariable Long id,
                                  @RequestBody @Valid TodoForm todoForm, Errors errors) {

        if (errors.hasErrors())
            return badRequest(errors);

        TodoDTO todo = commandService.updateTodo(id, todoForm.getContent(), todoForm.getReferenceIds());

        TodoResource todoResource = new TodoResource(todo);
        return ResponseEntity.ok(todoResource);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity putTodoStatus(@PathVariable Long id,
                                        @RequestBody @Valid StatusForm statusForm, Errors errors) {

        if (errors.hasErrors())
            return badRequest(errors);


        Status status = Status.valueOf(statusForm.getStatus().toUpperCase());

        TodoDTO todo = commandService.updateTodoStatus(id, status);

        TodoResource todoResource = new TodoResource(todo);
        return ResponseEntity.ok(todoResource);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorResource(errors));
    }
}
