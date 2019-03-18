package com.d2vfactory.resttodolist.service;

import com.d2vfactory.resttodolist.AbstractRepositoryTest;
import com.d2vfactory.resttodolist.TestDescription;
import com.d2vfactory.resttodolist.exceptions.HasReferenceTodoException;
import com.d2vfactory.resttodolist.exceptions.NotFoundTodoException;
import com.d2vfactory.resttodolist.model.common.Status;
import com.d2vfactory.resttodolist.model.dto.TodoDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class TodoCommandServiceTest extends AbstractRepositoryTest {

    @Autowired
    private TodoCommandService commandService;

    @Autowired
    private TodoQueryService queryService;

    @Test
    @TestDescription("할일 생성 테스트")
    public void createTodo() {
        // given
        TodoDTO todo = commandService.createTodo("집안일");

        // when
        TodoDTO findTodo = queryService.getTodo(todo.getId());

        // then
        assertThat(findTodo)
                .hasFieldOrPropertyWithValue("id", todo.getId())
                .hasFieldOrPropertyWithValue("content", "집안일");

        assertThat(findTodo.getReference())
                .hasSize(0);

        assertThat(findTodo.getReferenced())
                .hasSize(0);

    }

    @Test
    @TestDescription("할일 생성시, 참조 추가 - 참조된 할일 목록에서도 참조된 목록이 추가된다.")
    public void createTodo_withReference() {
        // given
        TodoDTO todo1 = commandService.createTodo("집안일");
        TodoDTO todo2 = commandService.createTodo("할일", todo1.getId());

        // when
        Page<TodoDTO> todoList = queryService.getTodoPages(PageRequest.of(0, 2, Sort.by("id").ascending()));

        // then
        TodoDTO findTodo1 = todoList.getContent().get(0);
        assertThat(findTodo1)
                .hasFieldOrPropertyWithValue("id", todo1.getId())
                .hasFieldOrPropertyWithValue("content", "집안일");
        assertThat(findTodo1.getReference())
                .hasSize(0);
        assertThat(findTodo1.getReferenced())
                .extracting("id")
                .containsExactly(todo2.getId());

        TodoDTO findTodo2 = todoList.getContent().get(1);
        assertThat(findTodo2)
                .hasFieldOrPropertyWithValue("id", todo2.getId())
                .hasFieldOrPropertyWithValue("content", "할일");
        assertThat(findTodo2.getReference())
                .extracting("id")
                .containsExactly(todo1.getId());
        assertThat(findTodo2.getReferenced())
                .hasSize(0);

    }

    @Test
    @TestDescription("할일 목록 수정 - 내용 수정")
    public void updateTodo_content() {
        // given
        TodoDTO todo1 = commandService.createTodo("집안일");
        TodoDTO todo2 = commandService.createTodo("할일", todo1.getId());

        // when
        commandService.updateTodo(todo2.getId(), "할일 수정");
        TodoDTO todoDTO = queryService.getTodo(todo2.getId());

        // then
        assertThat(todoDTO.getContent()).isEqualTo("할일 수정");
        assertThat(todoDTO.getReference()).hasSize(1);
    }


    @Test
    @TestDescription("참조 목록 추가 - 참조를 추가하면 참조된 할일의 참조된 목록에서도 추가된다.")
    public void addReference() {
        // given
        TodoDTO todo1 = commandService.createTodo("집안일");
        TodoDTO todo2 = commandService.createTodo("할일");

        // when
        commandService.addReference(todo1.getId(), todo2.getId());
        Page<TodoDTO> todoList = queryService.getTodoPages(PageRequest.of(0, 2, Sort.by("id").ascending()));

        // then
        TodoDTO findTodo1 = todoList.getContent().get(0);
        assertThat(findTodo1)
                .hasFieldOrPropertyWithValue("id", todo1.getId())
                .hasFieldOrPropertyWithValue("content", "집안일");
        assertThat(findTodo1.getReference())
                .extracting("id")
                .containsExactly(todo2.getId());
        assertThat(findTodo1.getReferenced())
                .hasSize(0);

        TodoDTO findTodo2 = todoList.getContent().get(1);
        assertThat(findTodo2)
                .hasFieldOrPropertyWithValue("id", todo2.getId())
                .hasFieldOrPropertyWithValue("content", "할일");
        assertThat(findTodo2.getReference())
                .hasSize(0);
        assertThat(findTodo2.getReferenced())
                .extracting("id")
                .containsExactly(todo1.getId());

    }

    @Test
    @TestDescription("참조 목록 추가 - 참조가 있는 할일에 참조 추가")
    public void addedReference_addReference() {
        // given
        TodoDTO todo1 = commandService.createTodo("집안일");
        TodoDTO todo2 = commandService.createTodo("할일", todo1.getId());
        TodoDTO todo3 = commandService.createTodo("청소");

        // when
        commandService.addReference(todo2.getId(), todo3.getId());
        Page<TodoDTO> todoList = queryService.getTodoPages(PageRequest.of(0, 3, Sort.by("id").ascending()));

        // then
        TodoDTO findTodo1 = todoList.getContent().get(0);
        assertThat(findTodo1)
                .hasFieldOrPropertyWithValue("id", todo1.getId())
                .hasFieldOrPropertyWithValue("content", "집안일");
        assertThat(findTodo1.getReference())
                .hasSize(0);
        assertThat(findTodo1.getReferenced())
                .hasSize(1);

        TodoDTO findTodo2 = todoList.getContent().get(1);
        assertThat(findTodo2)
                .hasFieldOrPropertyWithValue("id", todo2.getId())
                .hasFieldOrPropertyWithValue("content", "할일");
        assertThat(findTodo2.getReference())
                .hasSize(2);
        assertThat(findTodo2.getReferenced())
                .hasSize(0);

        TodoDTO findTodo3 = todoList.getContent().get(2);
        assertThat(findTodo3)
                .hasFieldOrPropertyWithValue("id", todo3.getId())
                .hasFieldOrPropertyWithValue("content", "청소");
        assertThat(findTodo3.getReference())
                .hasSize(0);
        assertThat(findTodo3.getReferenced())
                .hasSize(1);

    }

    @Test
    @TestDescription("참조 목록 제외 - 참조 목록에서 제외하면 참조된 할일의 참조된 목록에서도 제외된다.")
    public void removeReference() {
        // given
        TodoDTO todo1 = commandService.createTodo("집안일");
        TodoDTO todo2 = commandService.createTodo("할일", todo1.getId());

        // when
        commandService.removeReference(todo2.getId(), todo1.getId());
        Page<TodoDTO> todoList = queryService.getTodoPages(PageRequest.of(0, 2, Sort.by("id").ascending()));

        // then
        TodoDTO findTodo1 = todoList.getContent().get(0);
        assertThat(findTodo1)
                .hasFieldOrPropertyWithValue("id", todo1.getId())
                .hasFieldOrPropertyWithValue("content", "집안일");
        assertThat(findTodo1.getReference())
                .hasSize(0);
        assertThat(findTodo1.getReferenced())
                .hasSize(0);

        TodoDTO findTodo2 = todoList.getContent().get(1);
        assertThat(findTodo2)
                .hasFieldOrPropertyWithValue("id", todo2.getId())
                .hasFieldOrPropertyWithValue("content", "할일");
        assertThat(findTodo2.getReference())
                .hasSize(0);
        assertThat(findTodo2.getReferenced())
                .hasSize(0);
    }

    @Test
    @TestDescription("TODO 삭제, 실제로는 STATUS가 DELETED로 변경되는것이지만, 참조된 목록도 조회에서 제외된다.")
    public void updateStatus_deleted() {
        // given
        TodoDTO todo1 = commandService.createTodo("집안일");
        TodoDTO todo2 = commandService.createTodo("할일", todo1.getId());

        // when
        commandService.updateTodoStatus(todo1.getId(), Status.DELETED);

        TodoDTO findTodo1 = null;
        try {
            findTodo1 = queryService.getTodo(todo1.getId());
        } catch (NotFoundTodoException nfe) {
            log.error("# NotFoundTodoException:findTodo1({}) - {}", todo1.getId(), nfe.getMessage());
        }

        TodoDTO findTodo2 = queryService.getTodo(todo2.getId());

        // then
        assertThat(findTodo1).isNull();
        assertThat(findTodo2.getReference()).hasSize(0);
    }

    @Test(expected = NotFoundTodoException.class)
    @TestDescription("없는 ID로 삭제시, deleteTodo_NotFoundTodoException 발생")
    public void updateStatus_deleted_NotFoundTodoException() {
        // given
        Long notExistId = Long.MAX_VALUE;

        // when
        commandService.updateTodoStatus(notExistId, Status.DELETED);

        // then
        // => NotFoundTodoException
    }

    @Test
    @TestDescription("할일 참조를 가지고 있는 할일을 완료 처리 할 경우, 참조된 할일이 완료 되어있어야한다.")
    public void updateStatus_completed() {
        // given
        TodoDTO todo1 = commandService.createTodo("집안일");
        TodoDTO todo2 = commandService.createTodo("할일", todo1.getId());

        // when
        commandService.updateTodoStatus(todo1.getId(), Status.COMPLETED);
        commandService.updateTodoStatus(todo2.getId(), Status.COMPLETED);

        // then
        TodoDTO findTodo1 = queryService.getTodo(todo1.getId());
        assertThat(findTodo1.getStatus()).isEqualTo(Status.COMPLETED);

        TodoDTO findTodo2 = queryService.getTodo(todo2.getId());
        assertThat(findTodo2.getStatus()).isEqualTo(Status.COMPLETED);
    }

    @Test(expected = HasReferenceTodoException.class)
    @TestDescription("할일 참조를 가지고 있는 할일을 완료 처리 할 경우, HasReferenceTodoException 발생")
    public void updateStatus_completed_HasReferenceTodoException() {
        // given
        TodoDTO todo1 = commandService.createTodo("집안일");
        TodoDTO todo2 = commandService.createTodo("할일", todo1.getId());

        // when
        commandService.updateTodoStatus(todo2.getId(), Status.COMPLETED);

        // then
        // => HasReferenceTodoException

    }

    @Test
    @TestDescription("할일을 completed 상태로 변경했다가 다시 todo 상태로 변경하기")
    public void updateStatus_completed_todo() {
        // given
        TodoDTO todo1 = commandService.createTodo("집안일");

        // when
        commandService.updateTodoStatus(todo1.getId(), Status.COMPLETED);
        TodoDTO findTodo1 = queryService.getTodo(todo1.getId());

        commandService.updateTodoStatus(todo1.getId(), Status.TODO);
        TodoDTO findTodo2 = queryService.getTodo(todo1.getId());

        // then
        assertThat(findTodo1.getStatus()).isEqualTo(Status.COMPLETED);
        assertThat(findTodo2.getStatus()).isEqualTo(Status.TODO);
    }
}