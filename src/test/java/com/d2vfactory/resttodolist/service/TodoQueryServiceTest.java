package com.d2vfactory.resttodolist.service;

import com.d2vfactory.resttodolist.AbstractRepositoryTest;
import com.d2vfactory.resttodolist.TestDescription;
import com.d2vfactory.resttodolist.exceptions.NotFoundTodoException;
import com.d2vfactory.resttodolist.model.dto.TodoDTO;
import com.d2vfactory.resttodolist.model.entity.Todo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@SpringBootTest
public class TodoQueryServiceTest extends AbstractRepositoryTest {

    @Autowired
    private TodoQueryService queryService;

    @Test
    @TestDescription("TODO List 조회 - 0페이지 2개")
    public void getTodoList_page() {
        // given
        List<Todo> exampleTodoList = createExampleTodo();
        Todo exTodo1 = exampleTodoList.get(0);
        Todo exTodo2 = exampleTodoList.get(1);
        Todo exTodo3 = exampleTodoList.get(2);
        Todo exTodo4 = exampleTodoList.get(3);

        // when
        Pageable page = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<TodoDTO> todoList = queryService.getTodoPages(page);

        // then
        assertThat(todoList.getContent()).hasSize(2);

        TodoDTO todo1 = todoList.getContent().get(0);
        assertThat(todo1)
                .hasFieldOrPropertyWithValue("id", exTodo1.getId())
                .hasFieldOrPropertyWithValue("content", "집안일");
        assertThat(todo1.getReference())
                .extracting("id")
                .containsExactly(exTodo2.getId(), exTodo3.getId(), exTodo4.getId());
        assertThat(todo1.getReferenced())
                .hasSize(0);

        TodoDTO todo2 = todoList.getContent().get(1);
        assertThat(todo2)
                .hasFieldOrPropertyWithValue("id", exTodo2.getId())
                .hasFieldOrPropertyWithValue("content", "빨래");
        assertThat(todo2.getReference())
                .hasSize(0);
        assertThat(todo2.getReferenced())
                .extracting("id")
                .containsExactly(exTodo1.getId());

    }

    @Test
    @TestDescription("TODO List id가 3인 값 조회 - 4를 참조하고, 1에 참조됨")
    public void getTodo_id3() {
        // given
        List<Todo> exampleTodoList = createExampleTodo();
        Todo exTodo1 = exampleTodoList.get(0);
        Todo exTodo2 = exampleTodoList.get(1);
        Todo exTodo3 = exampleTodoList.get(2);
        Todo exTodo4 = exampleTodoList.get(3);

        // when
        TodoDTO todo = queryService.getTodo(exTodo3.getId());

        // then
        assertThat(todo)
                .hasFieldOrPropertyWithValue("id", exTodo3.getId())
                .hasFieldOrPropertyWithValue("content", "청소");

        assertThat(todo.getReference())
                .extracting("id")
                .containsExactly(exTodo4.getId());

        assertThat(todo.getReferenced())
                .extracting("id")
                .containsExactly(exTodo1.getId());

    }

    @Test(expected = NotFoundTodoException.class)
    @TestDescription("존재 하지 않는 ID 조회시, NotFoundTodoException 발생")
    public void getTodo_id_NotFoundTodoException(){
        // given
        Long notExistId = Long.MAX_VALUE;

        // when
        queryService.getTodo(notExistId);

        // then
        // => throw new NotFoundTodoException
    }


}