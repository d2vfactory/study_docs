package com.d2vfactory.resttodolist;


import com.d2vfactory.resttodolist.model.entity.Todo;
import com.d2vfactory.resttodolist.repository.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RunWith(SpringRunner.class)
public class AbstractRepositoryTest {

    @Autowired
    protected TodoRepository repository;

    /*
     * 할일 2, 3번은 1번에 참조가 걸린 상태이다.
     * 할일 4번은 할일 1, 3번에 참조가 걸린 상태이다.
     * 할일 1번은 할일 2번, 3번, 4번이 모두 완료되어야 완료처리가 가능하다.
     * 할일 3번은 할일 4번이 완료되어야 완료처리가 가능하다.
        => 할일 1번이 2,3번에 참조 걸었다.
            => 1: 2, 3
        => 할일 1번,3번은 할일 4번에 참조 걸었다.
            => 1: 2,3,4
            => 3: 4
     */
    protected List<Todo> createExampleTodo() {
        repository.deleteAll();
        
        Todo todo1 = createTodo("집안일");
        Todo todo2 = createTodo("빨래");
        Todo todo3 = createTodo("청소");
        Todo todo4 = createTodo("방청소");

        repository.save(todo1);
        repository.save(todo2);
        repository.save(todo3);
        repository.save(todo4);

        todo1.getReference().add(todo2);
        todo1.getReference().add(todo3);
        todo1.getReference().add(todo4);
        repository.save(todo1);

        todo3.getReference().add(todo4);
        repository.save(todo3);

        return Arrays.asList(todo1, todo2, todo3, todo4);
    }

    protected Todo createTodo(String content) {
        return Todo.builder()
                .content(content)
                .build();
    }

    protected void printTodoInfos(List<Todo> todoList) {
        for (Todo todo : todoList) {
            log.info("###############################################");
            log.info("# todo {} : {}({})", todo.getId(), todo.getContent(), todo.getStatus());
            log.info("# todo {} - reference : {}", todo.getId(),
                    todo.getReference().stream()
                            .map(x -> "@" + x.getId() + "(" + x.getStatus() + ")")
                            .collect(Collectors.joining(" ")));
            log.info("# todo {} - referenced : {}", todo.getId(),
                    todo.getReferenced().stream()
                            .map(x -> "@" + x.getId() + "(" + x.getStatus() + ")")
                            .collect(Collectors.joining(" ")));
            log.info("###############################################");
        }

    }
}
