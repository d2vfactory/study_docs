package com.d2vfactory.resttodolist.controller;

import com.d2vfactory.resttodolist.AbstractRepositoryTest;
import com.d2vfactory.resttodolist.TestDescription;
import com.d2vfactory.resttodolist.model.common.Status;
import com.d2vfactory.resttodolist.model.entity.Todo;
import com.d2vfactory.resttodolist.model.form.ReferenceForm;
import com.d2vfactory.resttodolist.model.form.StatusForm;
import com.d2vfactory.resttodolist.model.form.TodoForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class TodoControllerTest extends AbstractRepositoryTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    /*
    ### 목록조회 예시 및 설명
    | id | 할일         | 작성일시               | 최종수정일시          | 완료처리    |
    |----|-------------|---------------------|--------------------|-----------|
    | 1  | 집안일       | 2018-04-01 10:00:00 | 2018-04-01 13:00:00 |           |
    | 2  | 빨래 @1      | 2018-04-01 11:00:00 | 2018-04-01 11:00:00 |           |
    | 3  | 청소 @1      | 2018-04-01 12:00:00 | 2018-04-01 13:00:00 |           |
    | 4  | 방청소 @1 @3 | 2018-04-01 12:00:00 | 2018-04-01 13:00:00 |           |
     */
    @Test
    @TestDescription("예제 샘플 데이터 조회")
    public void getTodoList() throws Exception {
        // 8 * 4 => 32개 데이터 만들기.
        List<Todo> exampleTodoList = createExampleTodo();
        createExampleTodo();
        createExampleTodo();
        createExampleTodo();
        createExampleTodo();
        createExampleTodo();
        createExampleTodo();
        createExampleTodo();

        Todo todo1 = exampleTodoList.get(0);
        Todo todo3 = exampleTodoList.get(2);

        mockMvc.perform(get("/api/todo"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.todoList[0].content").value("집안일"))
                .andExpect(jsonPath("_embedded.todoList[0].status").value("ACTIVE"))
                .andExpect(jsonPath("_embedded.todoList[0]._links.self").exists())
                .andExpect(jsonPath("_embedded.todoList[1].content").value("빨래"))
                .andExpect(jsonPath("_embedded.todoList[1].contentAndReferenced")
                        .value("빨래 @" + todo1.getId()))
                .andExpect(jsonPath("_embedded.todoList[1].status").value("ACTIVE"))
                .andExpect(jsonPath("_embedded.todoList[1]._links.self").exists())
                .andExpect(jsonPath("_embedded.todoList[2].content").value("청소"))
                .andExpect(jsonPath("_embedded.todoList[2].contentAndReferenced")
                        .value("청소 @" + todo1.getId()))
                .andExpect(jsonPath("_embedded.todoList[2].status").value("ACTIVE"))
                .andExpect(jsonPath("_embedded.todoList[2]._links.self").exists())
                .andExpect(jsonPath("_embedded.todoList[3].content").value("방청소"))
                .andExpect(jsonPath("_embedded.todoList[3].contentAndReferenced")
                        .value("방청소 @" + todo1.getId() + " @" + todo3.getId()))
                .andExpect(jsonPath("_embedded.todoList[3].status").value("ACTIVE"))
                .andExpect(jsonPath("_embedded.todoList[3]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.next").exists())
                .andExpect(jsonPath("_links.last").exists())
                .andExpect(jsonPath("page").exists())
        ;
    }

    @Test
    @TestDescription("예제 할일 조회 - 할일1 => 빨래, 청소, 방청소 할일을 참조 한다.")
    public void getTodo_todo1() throws Exception {
        List<Todo> exampleTodoList = createExampleTodo();
        Todo todo1 = exampleTodoList.get(0);

        mockMvc.perform(get("/api/todo/{id}", todo1.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("content").value("집안일"))
                .andExpect(jsonPath("reference[0].content").value("빨래"))
                .andExpect(jsonPath("reference[1].content").value("청소"))
                .andExpect(jsonPath("reference[2].content").value("방청소"))
                .andExpect(jsonPath("_links.self").exists())
        ;

    }

    @Test
    @TestDescription("예제 할일 조회 - 할일3 => 방청소를 참조하고, 집안일에 참조걸려있다.")
    public void getTodo_todo3() throws Exception {
        List<Todo> exampleTodoList = createExampleTodo();
        Todo todo3 = exampleTodoList.get(2);

        mockMvc.perform(get("/api/todo/{id}", todo3.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("content").value("청소"))
                .andExpect(jsonPath("reference[0].content").value("방청소"))
                .andExpect(jsonPath("referenced[0].content").value("집안일"))
                .andExpect(jsonPath("_links.self").exists())
        ;
    }


    @Test
    @TestDescription("할일 생성 - 내용만 생성한다.")
    public void postTodo() throws Exception {
        TodoForm todoForm = TodoForm.builder()
                .content("할일 테스트")
                .build();

        mockMvc.perform(
                post("/api/todo")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(todoForm)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("content").value("할일 테스트"))
                .andExpect(jsonPath("status").value("ACTIVE"))
                .andExpect(jsonPath("reference").isEmpty())
                .andExpect(jsonPath("referenced").isEmpty())
                .andExpect(jsonPath("_links.self").exists())
        ;
    }

    @Test
    @TestDescription("할일 생성 - 내용과 참조 목록을 수정한다.")
    public void postTodo_withReference() throws Exception {
        List<Todo> exampleTodoList = createExampleTodo();
        Todo todo2 = exampleTodoList.get(1);
        Todo todo3 = exampleTodoList.get(2);

        TodoForm todoForm = TodoForm.builder()
                .content("할일 테스트")
                .referenceIds(new Long[]{todo2.getId(), todo3.getId()})
                .build();

        mockMvc.perform(
                post("/api/todo")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(todoForm)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("content").value("할일 테스트"))
                .andExpect(jsonPath("status").value("ACTIVE"))
                .andExpect(jsonPath("reference[0].content").value(todo2.getContent()))
                .andExpect(jsonPath("reference[1].content").value(todo3.getContent()))
                .andExpect(jsonPath("referenced").isEmpty())
                .andExpect(jsonPath("_links.self").exists())
        ;
    }

    @Test
    @TestDescription("할일 정보 수정 - 내용을 수정한다.")
    public void putTodo_updateContent() throws Exception {
        List<Todo> exampleTodoList = createExampleTodo();
        Todo todo1 = exampleTodoList.get(0);
        Todo todo2 = exampleTodoList.get(1);
        Todo todo4 = exampleTodoList.get(3);

        TodoForm todoForm = TodoForm.builder()
                .content("집안일 변경")
                .build();

        mockMvc.perform(
                put("/api/todo/{id}", todo1.getId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(todoForm)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("content").value("집안일 변경"))
                .andExpect(jsonPath("status").value("ACTIVE"))
                .andExpect(jsonPath("completeDate").isEmpty())
                .andExpect(jsonPath("reference[0].content").value("빨래"))
                .andExpect(jsonPath("reference[1].content").value("청소"))
                .andExpect(jsonPath("reference[2].content").value("방청소"))
                .andExpect(jsonPath("referenced").isEmpty())
                .andExpect(jsonPath("_links.self").exists());
    }

    @Test
    @TestDescription("할일2에서 할일1,3,4를 참조에 추가한것을 확인하고 할일 1,3,4에서 참조2에 의해 추가되었음을 확인.")
    public void todo2AddReference_otherTodoAddReferenced() throws Exception {
        List<Todo> exampleTodoList = createExampleTodo();
        Todo todo1 = exampleTodoList.get(0);
        Todo todo2 = exampleTodoList.get(1);
        Todo todo3 = exampleTodoList.get(2);
        Todo todo4 = exampleTodoList.get(3);

        ReferenceForm referenceForm = new ReferenceForm(todo1.getId(), todo3.getId(), todo4.getId());

        // todo2 참조 추가
        mockMvc.perform(
                put("/api/todo/{id}/reference", todo2.getId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(referenceForm)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("content").value("빨래"))
                .andExpect(jsonPath("status").value("ACTIVE"))
                .andExpect(jsonPath("completeDate").isEmpty())
                .andExpect(jsonPath("reference[0].id").value(todo1.getId()))
                .andExpect(jsonPath("reference[1].id").value(todo3.getId()))
                .andExpect(jsonPath("reference[2].id").value(todo4.getId()))
                .andExpect(jsonPath("referenced[0].id").value(todo1.getId()))
                .andExpect(jsonPath("_links.self").exists());

        // todo2에서 1,3,4를 참조 걸었기 때문에, 1,3,4의 referenced에는 todo2가 있어야 한다.
        mockMvc.perform(get("/api/todo"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.todoList[0].content").value("집안일"))
                .andExpect(jsonPath("_embedded.todoList[0].status").value("ACTIVE"))
                .andExpect(jsonPath("_embedded.todoList[0].contentAndReferenced")
                        .value("집안일 @" + todo2.getId()))
                .andExpect(jsonPath("_embedded.todoList[0]._links.self").exists())
                .andExpect(jsonPath("_embedded.todoList[1].content").value("빨래"))
                .andExpect(jsonPath("_embedded.todoList[1].contentAndReferenced")
                        .value("빨래 @" + todo1.getId()))
                .andExpect(jsonPath("_embedded.todoList[1].status").value("ACTIVE"))
                .andExpect(jsonPath("_embedded.todoList[1]._links.self").exists())
                .andExpect(jsonPath("_embedded.todoList[2].content").value("청소"))
                .andExpect(jsonPath("_embedded.todoList[2].contentAndReferenced")
                        .value("청소 @" + todo1.getId() + " @" + todo2.getId()))
                .andExpect(jsonPath("_embedded.todoList[2].status").value("ACTIVE"))
                .andExpect(jsonPath("_embedded.todoList[2]._links.self").exists())
                .andExpect(jsonPath("_embedded.todoList[3].content").value("방청소"))
                .andExpect(jsonPath("_embedded.todoList[3].contentAndReferenced")
                        .value("방청소 @" + todo1.getId() + " @" + todo2.getId() + " @" + todo3.getId()))
                .andExpect(jsonPath("_embedded.todoList[3].status").value("ACTIVE"))
                .andExpect(jsonPath("_embedded.todoList[3]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("page").exists())
        ;
    }

    @Test
    @TestDescription("자기 자신은 참조에서 제외 - 할일 2에서 할일 2를 참조하여도 저장되지 않는다.")
    public void addReference_todo2_AddSelfReference() throws Exception {
        List<Todo> exampleTodoList = createExampleTodo();
        Todo todo2 = exampleTodoList.get(1);

        ReferenceForm referenceForm = new ReferenceForm(todo2.getId());

        mockMvc.perform(
                put("/api/todo/{id}/reference", todo2.getId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(referenceForm)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("content").value("빨래"))
                .andExpect(jsonPath("status").value("ACTIVE"))
                .andExpect(jsonPath("completeDate").isEmpty())
                .andExpect(jsonPath("reference[0]").doesNotExist())
                .andExpect(jsonPath("_links.self").exists());
    }


    @Test
    @TestDescription("중복 참조 제외 - 자료형이 HashSet 이라서 중복 되지 않음")
    public void addReference_todo1_duplicatedAddReference() throws Exception {
        List<Todo> exampleTodoList = createExampleTodo();
        Todo todo1 = exampleTodoList.get(0);
        Todo todo2 = exampleTodoList.get(1);
        Todo todo3 = exampleTodoList.get(2);
        Todo todo4 = exampleTodoList.get(3);

        ReferenceForm referenceForm = new ReferenceForm(todo2.getId(), todo3.getId());

        mockMvc.perform(
                put("/api/todo/{id}/reference", todo1.getId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(referenceForm)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("content").value("집안일"))
                .andExpect(jsonPath("status").value("ACTIVE"))
                .andExpect(jsonPath("completeDate").isEmpty())
                .andExpect(jsonPath("reference[0].id").value(todo2.getId()))
                .andExpect(jsonPath("reference[1].id").value(todo3.getId()))
                .andExpect(jsonPath("reference[2].id").value(todo4.getId()))
                .andExpect(jsonPath("reference[3]").doesNotExist())
                .andExpect(jsonPath("_links.self").exists());

    }

    @Test
    @TestDescription("삭제 처리 - 삭제 상태로 변경한다.")
    public void putTodo_updateStatus_deleted() throws Exception {
        List<Todo> exampleTodoList = createExampleTodo();
        Todo todo1 = exampleTodoList.get(0);

        StatusForm statusForm = new StatusForm("DELETED");

        mockMvc.perform(
                put("/api/todo/{id}/status", todo1.getId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(statusForm)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value("DELETED"))
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("삭제 처리 실패 - 삭제 처리할 ID가 존재하지 않을때 NotFoundTodoException 발생")
    public void putTodo_updateStatus_deleted_notFoundTodoException() throws Exception {
        Long maxValue = Long.MAX_VALUE;

        StatusForm statusForm = new StatusForm(Status.DELETED.name());

        mockMvc.perform(
                put("/api/todo/{id}/status", maxValue)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(statusForm)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("할일 목록이 존재하지 않습니다."))
                .andExpect(jsonPath("cause").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("완료 처리 - completeDate에 날짜가 저장된다.")
    public void putTodo_updateStatus_completed() throws Exception {
        List<Todo> exampleTodoList = createExampleTodo();
        Todo todo2 = exampleTodoList.get(1);

        StatusForm statusForm = new StatusForm(Status.COMPLETED.name());

        mockMvc.perform(
                put("/api/todo/{id}/status", todo2.getId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(statusForm)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value("COMPLETED"))
                .andExpect(jsonPath("completeDate").isNotEmpty())
                .andExpect(jsonPath("_links.self").exists())
        ;
    }

    @Test
    @TestDescription("완료 처리 실패 - 참조한 할일이 있을 경우, 해당 할일이 정상상태이면 완료 처리할 수 없다. HasReferenceTodoException 발생")
    public void putTodo_updateStatus_completed_hasReferenceTodoException() throws Exception {
        List<Todo> exampleTodoList = createExampleTodo();
        Todo todo1 = exampleTodoList.get(0);
        Todo todo3 = exampleTodoList.get(2);

        StatusForm statusForm = new StatusForm(Status.COMPLETED.name());

        // 할일1 -> 할일 2,3,4를 참조하고 있어서 실패.
        mockMvc.perform(
                put("/api/todo/{id}/status", todo1.getId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(statusForm)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("참조된 할일 목록이 존재합니다."))
                .andExpect(jsonPath("cause").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;

        // 할일3 -> 할일4를 참조 하고 있어서 실패
        mockMvc.perform(
                put("/api/todo/{id}/status", todo3.getId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(statusForm)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("참조된 할일 목록이 존재합니다."))
                .andExpect(jsonPath("cause").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

}