package com.d2vfactory.resttodolist.repository;

import com.d2vfactory.resttodolist.AbstractRepositoryTest;
import com.d2vfactory.resttodolist.TestDescription;
import com.d2vfactory.resttodolist.model.common.Status;
import com.d2vfactory.resttodolist.model.entity.Todo;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class TodoRepositoryTest extends AbstractRepositoryTest {

    @Test
    @TestDescription("예제 데이터 전체 구성 테스트")
    public void exampleData() {
        // given
        List<Todo> exampleTodoList = createExampleTodo();
        Todo todo1 = exampleTodoList.get(0);
        Todo todo2 = exampleTodoList.get(1);
        Todo todo3 = exampleTodoList.get(2);
        Todo todo4 = exampleTodoList.get(3);

        // when
        List<Todo> todoList = repository.fetchFindAll();
        printTodoInfos(todoList);

        // then
        // todo1
        Todo findTodo1 = todoList.get(0);
        assertThat(findTodo1.getContent()).isEqualTo("집안일");
        assertThat(findTodo1.getReference()).containsExactly(todo2, todo3, todo4);
        assertThat(findTodo1.getReferenced()).hasSize(0);
        // todo2
        Todo findTodo2 = todoList.get(1);
        assertThat(findTodo2.getContent()).isEqualTo("빨래");
        assertThat(findTodo2.getReference()).hasSize(0);
        assertThat(findTodo2.getReferenced()).containsExactly(todo1);
        // todo3
        Todo findTodo3 = todoList.get(2);
        assertThat(findTodo3.getContent()).isEqualTo("청소");
        assertThat(findTodo3.getReference()).containsExactly(todo4);
        assertThat(findTodo3.getReferenced()).containsExactly(todo1);
        // todo4
        Todo findTodo4 = todoList.get(3);
        assertThat(findTodo4.getContent()).isEqualTo("방청소");
        assertThat(findTodo4.getReference()).hasSize(0);
        assertThat(findTodo4.getReferenced()).containsExactly(todo1, todo3);
    }

    @Test
    @TestDescription("완료 처리 테스트")
    public void exampleData_updateComplete() {
        // given
        List<Todo> exampleTodoList = createExampleTodo();
        Todo todo1 = exampleTodoList.get(0);

        // when
        Todo findTodo = repository.findById(todo1.getId()).get();
        findTodo.setStatus(Status.COMPLETED);
        findTodo.setCompleteDate(LocalDateTime.now());
        repository.save(findTodo);

        // then
        findTodo = repository.findById(todo1.getId()).get();
        assertThat(findTodo.getStatus()).isEqualTo(Status.COMPLETED);
    }

    @Test
    @TestDescription("참조 변경 테스트 - 참조가 변경되면 참조된 할일도 제거되어야 한다.")
    @Transactional
    public void exampleData_updateReferenceAndRemoveReferenced() {
        // given
        List<Todo> exampleTodoList = createExampleTodo();
        Todo todo1 = exampleTodoList.get(0);
        Todo todo2 = exampleTodoList.get(1);
        Todo todo3 = exampleTodoList.get(2);
        Todo todo4 = exampleTodoList.get(3);

        // when
        Todo findTodo1 = repository.findById(todo1.getId()).get();
        Hibernate.initialize(findTodo1.getReference());
        Set<Todo> newReference = findTodo1.getReference();
        newReference.remove(todo2);
        findTodo1.setReference(newReference);
        repository.save(findTodo1);

        // then
        Todo newTodo1 = repository.findById(todo1.getId()).get();
        Hibernate.initialize(newTodo1.getReference());
        Hibernate.initialize(newTodo1.getReferenced());
        assertThat(newTodo1.getContent()).isEqualTo("집안일");
        assertThat(newTodo1.getReference()).containsExactly(todo3, todo4);
        assertThat(newTodo1.getReferenced()).hasSize(0);

        // todo1에서 참조를 뺏기 때문에, 참조된 목록에서 todo1이 없어야 한다.
        Todo newTodo2 = repository.findById(todo2.getId()).get();
        Hibernate.initialize(newTodo2.getReference());
        Hibernate.initialize(newTodo2.getReferenced());
        assertThat(newTodo2.getContent()).isEqualTo("빨래");
        assertThat(newTodo2.getReference()).hasSize(0);
        assertThat(newTodo2.getReferenced()).hasSize(0);
    }

    @Test
    @TestDescription("Page 테스트 - ID 내림차순으로 2개 조회하기.")
    public void pageable_descendingId_2element() {
        // given
        List<Todo> exampleTodoList = createExampleTodo();
        Todo todo1 = exampleTodoList.get(0);
        Todo todo3 = exampleTodoList.get(2);
        Todo todo4 = exampleTodoList.get(3);

        // when
        Pageable page = PageRequest.of(0, 2, Sort.by("id").descending());
        Page<Todo> pageTodo = repository.fetchFindAll(page);

        // then
        // page
        assertThat(pageTodo.getTotalElements()).isEqualTo(4);
        assertThat(pageTodo.getSize()).isEqualTo(2);
        assertThat(pageTodo.getContent()).containsExactly(todo4, todo3);
        // todo3
        Todo findTodo3 = pageTodo.getContent().get(1);
        assertThat(findTodo3.getContent()).isEqualTo("청소");
        assertThat(findTodo3.getReference()).containsExactly(todo4);
        assertThat(findTodo3.getReferenced()).containsExactly(todo1);
        // todo4
        Todo findTodo4 = pageTodo.getContent().get(0);
        assertThat(findTodo4.getContent()).isEqualTo("방청소");
        assertThat(findTodo4.getReference()).hasSize(0);
        assertThat(findTodo4.getReferenced()).containsExactly(todo1, todo3);
    }

    @Test
    @TestDescription("ID In 테스트 -  Id가 2,3인 todo 조회")
    public void findByIdIn_2And3() {
        // given
        List<Todo> exampleTodoList = createExampleTodo();
        Todo todo2 = exampleTodoList.get(1);
        Todo todo3 = exampleTodoList.get(2);

        // when
        Set<Todo> todoList = repository.findAllByIdIn(todo2.getId(), todo3.getId());

        // then
        assertThat(todoList)
                .extracting("id")
                .containsExactly(todo2.getId(), todo3.getId());

    }

    @Test
    @TestDescription("상태를 삭제로 변경하기 - Entity에서 DELETED된 상태는 가져오지 못하도록 처리하였기 때문에 조회되지 않아야 한다.")
    public void status_deleted_todo2() {
        // given
        createExampleTodo();
        List<Todo> todoList = repository.fetchFindAll();
        Todo todo2 = todoList.get(1);

        // when
        todo2.setStatus(Status.DELETED);
        repository.save(todo2);

        // then
        assertThat(repository.fetchFindAll()).doesNotContain(todo2);
    }

    @Test
    @TestDescription("DB에서 삭제 하기 - 참조를 건 Todo에서 참조를 제외해야 삭제 된다.")
    public void db_deleted_todo2() {
        // given
        createExampleTodo();
        List<Todo> todoList = repository.fetchFindAll();
        Todo todo2 = todoList.get(1);

        // when
        // remove todo2
        for (Todo referedTodo : todo2.getReferenced()) {
            referedTodo.getReference().remove(todo2);
            repository.save(referedTodo);
        }
        repository.delete(todo2);

        // then
        assertThat(repository.fetchFindAll()).doesNotContain(todo2);
    }

}