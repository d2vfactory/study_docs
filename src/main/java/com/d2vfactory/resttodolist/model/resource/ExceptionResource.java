package com.d2vfactory.resttodolist.model.resource;

import com.d2vfactory.resttodolist.controller.IndexController;
import com.d2vfactory.resttodolist.model.dto.ExceptionDTO;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class ExceptionResource extends Resource<ExceptionDTO> {
    public ExceptionResource(ExceptionDTO exception, Link... links) {
        super(exception, links);
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
}
