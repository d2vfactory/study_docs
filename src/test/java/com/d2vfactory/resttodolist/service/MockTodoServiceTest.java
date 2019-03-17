package com.d2vfactory.resttodolist.service;


import com.d2vfactory.resttodolist.TestDescription;
import com.d2vfactory.resttodolist.exceptions.HasReferenceTodoException;
import com.d2vfactory.resttodolist.exceptions.NotFoundTodoException;
import com.d2vfactory.resttodolist.model.dto.TodoDTO;
import com.d2vfactory.resttodolist.model.entity.Todo;
import com.d2vfactory.resttodolist.repository.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MockTodoServiceTest {

    @MockBean
    private TodoRepository mockRepository;

    @Autowired
    private TodoQueryService queryService;

    @Autowired
    private TodoCommandService commandService;

    @Test
    @TestDescription("[MOCK] todo1 조회 - todo1은 todo2를 참조한다.")
    public void getTodo1_referenceTodo2() {
        // given
        Todo mockTodo1 = createTodo(1L, "mock 할일");
        Todo mockTodo2 = createTodo(2L, "mock 할일 참조");
        addReference(mockTodo1, mockTodo2);

        // when
        when(mockRepository.findById(1L)).thenReturn(Optional.of(mockTodo1));
        TodoDTO todoDTO = queryService.getTodo(1L);

        // then
        assertThat(todoDTO)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("content", "mock 할일");

        assertThat(todoDTO.getReference())
                .first()
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("content", "mock 할일 참조");
    }

    @Test(expected = NotFoundTodoException.class)
    @TestDescription("[MOCK] 없는 ID 조회 상황.")
    public void getTodo_NotFoundTodoException(){
        // given & when
        when(mockRepository.findById(1L)).thenThrow(NotFoundTodoException.class);
        queryService.getTodo(1L);

        // then
        // NotFoundTodoException
    }

    @Test
    @TestDescription("[MOCK] todo list page 조회 - todo1은 todo2를 참조하고, todo2는 todo1에 참조걸려야한다.")
    public void getTodoList_todo1ReferenceTodo2_todo2ReferencedTodo1() {
        // given
        Todo mockTodo1 = createTodo(1L, "mock 할일");
        Todo mockTodo2 = createTodo(2L, "mock 할일 참조");
        addReference(mockTodo1, mockTodo2);
        Pageable page = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Todo> mockTodoList = new PageImpl<>(Arrays.asList(mockTodo1, mockTodo2), page, 2);

        // when
        when(mockRepository.findAll(page)).thenReturn(mockTodoList);
        Page<TodoDTO> todoDTOList = queryService.getTodoList(page);

        // then
        TodoDTO todo1 = todoDTOList.getContent().get(0);
        assertThat(todo1)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("content", "mock 할일");
        assertThat(todo1.getReference())
                .first()
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("content", "mock 할일 참조");

        TodoDTO todo2 = todoDTOList.getContent().get(1);
        assertThat(todo2)
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("content", "mock 할일 참조")
                .hasFieldOrPropertyWithValue("contentAndReferenced", "mock 할일 참조 @1");
        assertThat(todo2.getReferenced())
                .first()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("content", "mock 할일");

    }

    @Test(expected = HasReferenceTodoException.class)
    @TestDescription("[MOCK] todo 완료 예외 - 완료되지 않은 참조된 할일이 있는 경우, HasReferenceTodoException 발생")
    public void completeTodo_hasReference_exception(){
        // given
        Todo mockTodo1 = createTodo(1L, "mock 할일");
        Todo mockTodo2 = createTodo(2L, "mock 할일 참조");
        addReference(mockTodo1, mockTodo2);

        // when
        when(mockRepository.findById(1L)).thenReturn(Optional.of(mockTodo1));
        commandService.completeTodo(1L);

        // then
        // throw new HasReferenceTodoException
    }

    private Todo createTodo(Long id, String content) {
        Todo todo = new Todo();
        todo.setId(id);
        todo.setContent(content);
        return todo;
    }

    private void addReference(Todo todo, Todo... reference) {
        todo.getReference().addAll(Arrays.asList(reference));
        for (Todo referTodo : reference) {
            referTodo.getReferenced().add(todo);
        }
    }

    private void removeReference(Todo todo, Todo... reference) {
        todo.getReference().removeAll(Arrays.asList(reference));
        for (Todo referTodo : reference) {
            referTodo.getReferenced().remove(todo);
        }
    }

}
