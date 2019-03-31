package com.d2vfactory.resttodolist;

import com.d2vfactory.resttodolist.model.entity.Todo;
import com.d2vfactory.resttodolist.repository.TodoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableJpaAuditing
@EnableWebMvc
@SpringBootApplication
public class RestTodoListApplication {


    public static void main(String[] args) {
        SpringApplication.run(RestTodoListApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(TodoRepository repository){
        return args -> {
            Todo todo1 = Todo.builder().content("집안일").build();
            Todo todo2 = Todo.builder().content("빨래").build();
            Todo todo3 = Todo.builder().content("청소").build();
            Todo todo4 = Todo.builder().content("방청소").build();

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
        };

    }


}
