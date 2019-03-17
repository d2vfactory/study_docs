package com.d2vfactory.resttodolist.service;

import com.d2vfactory.resttodolist.exceptions.HasReferenceTodoException;
import com.d2vfactory.resttodolist.exceptions.NotFoundTodoException;
import com.d2vfactory.resttodolist.model.common.Status;
import com.d2vfactory.resttodolist.model.dto.TodoDTO;
import com.d2vfactory.resttodolist.model.entity.Todo;
import com.d2vfactory.resttodolist.repository.TodoRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class TodoCommandService {

    private final TodoRepository repository;

    public TodoCommandService(TodoRepository repository) {
        this.repository = repository;
    }

    public TodoDTO createTodo(String content) {
        Todo todo = repository.save(Todo.builder().content(content).build());
        return new TodoDTO(todo);
    }

    public TodoDTO createTodo(String content, Long... referenceIds) {
        Todo todo = repository.save(Todo.builder()
                .content(content)
                .reference(repository.findAllByIdIn(referenceIds))
                .build()
        );
        return new TodoDTO(todo);
    }

    public void addReference(Long id, Long... referenceIds) {
        Todo todo = findById(id);
        todo.getReference().addAll(repository.findAllByIdIn(referenceIds));
        repository.save(todo);
    }

    public void removeReference(Long id, Long... referenceIds) {
        Todo todo = findById(id);
        todo.getReference().removeAll(repository.findAllByIdIn(referenceIds));
        repository.save(todo);
    }

    public void deleteTodo(Long id) {
        Todo todo = findById(id);
        todo.setStatus(Status.DELETED);
        repository.save(todo);
    }

    public void completeTodo(Long id) {
        Todo todo = findById(id);
        Hibernate.initialize(todo.getReference());

        // 참조된 할일 중에 상태가 "할일"인 건이 1개라도 있는 경우, 완료 안되게 처리.
        long cntReferenceTodoStatus = todo.getReference().stream()
                .filter(x -> x.getStatus() == Status.TODO)
                .count();

        if (cntReferenceTodoStatus > 0)
            throw new HasReferenceTodoException();

        todo.setStatus(Status.COMPLETED);
        todo.setCompleteDate(LocalDateTime.now());
        repository.save(todo);
    }

    private Todo findById(Long id) {
        return repository.findById(id).orElseThrow(NotFoundTodoException::new);
    }
}
