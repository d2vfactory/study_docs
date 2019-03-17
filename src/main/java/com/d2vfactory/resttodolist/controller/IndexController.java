package com.d2vfactory.resttodolist.controller;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
public class IndexController {

    @GetMapping("/api")
    public ResourceSupport index() {
        ResourceSupport resource = new ResourceSupport();
        resource.add(linkTo(TodoController.class).withRel("todo-list"));
        return resource;
    }

}
