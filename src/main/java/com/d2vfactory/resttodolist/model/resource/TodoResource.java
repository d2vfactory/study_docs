package com.d2vfactory.resttodolist.model.resource;

import com.d2vfactory.resttodolist.controller.TodoController;
import com.d2vfactory.resttodolist.model.dto.TodoDTO;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class TodoResource extends Resource<TodoDTO> {
    public TodoResource(TodoDTO todo, Link... links) {
        super(todo, links);
        add(linkTo(TodoController.class).slash(todo.getId()).withSelfRel());
    }
}
