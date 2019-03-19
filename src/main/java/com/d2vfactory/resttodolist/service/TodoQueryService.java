package com.d2vfactory.resttodolist.service;

import com.d2vfactory.resttodolist.exceptions.NotFoundTodoException;
import com.d2vfactory.resttodolist.model.dto.ReferenceTodoDTO;
import com.d2vfactory.resttodolist.model.dto.TodoDTO;
import com.d2vfactory.resttodolist.model.entity.Todo;
import com.d2vfactory.resttodolist.repository.ActiveTodoRepository;
import com.d2vfactory.resttodolist.repository.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class TodoQueryService {

    private final TodoRepository todoRepository;

    private final ActiveTodoRepository activeTodoRepository;

    public TodoQueryService(TodoRepository todoRepository, ActiveTodoRepository activeTodoRepository) {
        this.todoRepository = todoRepository;
        this.activeTodoRepository = activeTodoRepository;
    }


    public Page<TodoDTO> getTodoPages(Pageable pageable) {
        Page<Todo> todoPages = todoRepository.fetchFindAll(pageable);
        return new PageImpl<>(
                todoPages.getContent().stream().map(TodoDTO::new).collect(Collectors.toList()),
                pageable,
                todoPages.getTotalElements()
        );
    }

    public List<ReferenceTodoDTO> getActiveTodoList() {
        return activeTodoRepository.findAll().stream()
                .map(ReferenceTodoDTO::new)
                .collect(Collectors.toList());
    }

    public TodoDTO getTodo(Long id) {
        Todo todo = todoRepository.findById(id).orElseThrow(NotFoundTodoException::new);
        Hibernate.initialize(todo.getReference());
        Hibernate.initialize(todo.getReferenced());
        return new TodoDTO(todo);
    }


}
