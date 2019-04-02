package com.d2vfactory.resttodolist.model.resource;

import com.d2vfactory.resttodolist.controller.IndexController;
import com.d2vfactory.resttodolist.controller.TodoController;
import com.d2vfactory.resttodolist.model.common.Status;
import com.d2vfactory.resttodolist.model.dto.TodoDTO;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class TodoResource extends Resource<TodoDTO> {
    public TodoResource(TodoDTO todo, Link... links) {
        super(todo, links);

        if (todo.getStatus() == Status.DELETED) {
            add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
        } else {
            add(linkTo(methodOn(TodoController.class).getTodo(todo.getId())).withSelfRel());
            add(new Link("/docs/index.html#todo-id").withRel("profile"));
        }

    }
}
