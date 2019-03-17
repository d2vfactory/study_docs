package com.d2vfactory.resttodolist.service;

import com.d2vfactory.resttodolist.exceptions.NotFoundTodoException;
import com.d2vfactory.resttodolist.model.dto.TodoDTO;
import com.d2vfactory.resttodolist.model.entity.Todo;
import com.d2vfactory.resttodolist.repository.TodoRepository;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TodoQueryService {

    private final TodoRepository repository;

    public TodoQueryService(TodoRepository repository) {
        this.repository = repository;
    }

    public Page<TodoDTO> getTodoPages(Pageable pageable) {
        Page<Todo> todoPages = repository.findAll(pageable);
        return new PageImpl<>(
                todoPages.getContent().stream().map(TodoDTO::new).collect(Collectors.toList()),
                pageable,
                todoPages.getTotalElements()
        );
    }

    public TodoDTO getTodo(Long id) {
        Todo todo = repository.findById(id).orElseThrow(NotFoundTodoException::new);
        Hibernate.initialize(todo.getReference());
        Hibernate.initialize(todo.getReferenced());
        return new TodoDTO(todo);
    }


}
