package com.d2vfactory.resttodolist.controller;

import com.d2vfactory.resttodolist.exceptions.RuntimeTodoException;
import com.d2vfactory.resttodolist.model.dto.ExceptionDTO;
import com.d2vfactory.resttodolist.model.resource.ExceptionResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = RuntimeTodoException.class)
    public ExceptionResource handleTodoException(RuntimeTodoException e) {
        ExceptionResource exceptionResource = new ExceptionResource(createExceptionDTO(e));
        return exceptionResource;
    }

    @RequestMapping(produces = "application/json")
    @ExceptionHandler(value = NoHandlerFoundException.class)
    public ExceptionResource noHandleException(NoHandlerFoundException e) {
        ExceptionResource exceptionResource = new ExceptionResource(createExceptionDTO(e));
        return exceptionResource;
    }
    
    @ExceptionHandler(value = Exception.class)
    public ExceptionResource handleException(Exception e) {
        ExceptionResource exceptionResource = new ExceptionResource(createExceptionDTO(e));
        return exceptionResource;
    }



    private ExceptionDTO createExceptionDTO(Exception e) {
        return ExceptionDTO.builder()
                .message(e.getMessage())
                .cause(e.toString())
                .build();
    }

}
