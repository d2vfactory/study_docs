package com.d2vfactory.resttodolist.controller;

import com.d2vfactory.resttodolist.exceptions.RuntimeTodoException;
import com.d2vfactory.resttodolist.model.dto.ExceptionDTO;
import com.d2vfactory.resttodolist.model.resource.ExceptionResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = RuntimeTodoException.class)
    public ExceptionResource handleTodoException(RuntimeTodoException e) {
        ExceptionResource exceptionResource = new ExceptionResource(createExceptionDTO(e));
        return exceptionResource;
    }

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
