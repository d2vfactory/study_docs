package com.d2vfactory.resttodolist.controller;

import com.d2vfactory.resttodolist.AbstractRepositoryTest;
import com.d2vfactory.resttodolist.TestDescription;
import com.d2vfactory.resttodolist.config.RestDocsConfig;
import com.d2vfactory.resttodolist.model.entity.Todo;
import com.d2vfactory.resttodolist.model.form.TodoForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
@Import(RestDocsConfig.class)
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
                .andExpect(jsonPath("_embedded.todoList[0].status").value("TODO"))
                .andExpect(jsonPath("_embedded.todoList[0]._links.self").exists())
                .andExpect(jsonPath("_embedded.todoList[1].content").value("빨래"))
                .andExpect(jsonPath("_embedded.todoList[1].contentAndReferenced")
                        .value("빨래 @" + todo1.getId()))
                .andExpect(jsonPath("_embedded.todoList[1].status").value("TODO"))
                .andExpect(jsonPath("_embedded.todoList[1]._links.self").exists())
                .andExpect(jsonPath("_embedded.todoList[2].content").value("청소"))
                .andExpect(jsonPath("_embedded.todoList[2].contentAndReferenced")
                        .value("청소 @" + todo1.getId()))
                .andExpect(jsonPath("_embedded.todoList[2].status").value("TODO"))
                .andExpect(jsonPath("_embedded.todoList[2]._links.self").exists())
                .andExpect(jsonPath("_embedded.todoList[3].content").value("방청소"))
                .andExpect(jsonPath("_embedded.todoList[3].contentAndReferenced")
                        .value("방청소 @" + todo1.getId() + " @" + todo3.getId()))
                .andExpect(jsonPath("_embedded.todoList[3].status").value("TODO"))
                .andExpect(jsonPath("_embedded.todoList[3]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.next").exists())
                .andExpect(jsonPath("_links.last").exists())
                .andExpect(jsonPath("page").exists())
        ;
    }

    @Test
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
    public void postTodo() throws Exception {
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
                .andExpect(jsonPath("status").value("TODO"))
                .andExpect(jsonPath("reference[0].content").value(todo2.getContent()))
                .andExpect(jsonPath("reference[1].content").value(todo3.getContent()))
                .andExpect(jsonPath("referenced").isEmpty())
                .andExpect(jsonPath("_links.self").exists())
        ;
    }

    @Test
    public void postTodo_withReference() throws Exception {
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
                .andExpect(jsonPath("status").value("TODO"))
                .andExpect(jsonPath("reference").isEmpty())
                .andExpect(jsonPath("referenced").isEmpty())
                .andExpect(jsonPath("_links.self").exists())
        ;
    }
}