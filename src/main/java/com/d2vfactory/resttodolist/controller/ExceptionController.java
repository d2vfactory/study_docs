package com.d2vfactory.resttodolist.controller;

import com.d2vfactory.resttodolist.exceptions.HasReferenceTodoException;
import com.d2vfactory.resttodolist.exceptions.NotFoundTodoException;
import com.d2vfactory.resttodolist.model.dto.ExceptionDTO;
import com.d2vfactory.resttodolist.model.resource.ExceptionResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
public class ExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = HasReferenceTodoException.class)
    public ExceptionResource handleTodoException(HasReferenceTodoException e) {
        ExceptionResource exceptionResource = new ExceptionResource(createExceptionDTO(e));
        return exceptionResource;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = NotFoundTodoException.class)
    public ExceptionResource handleTodoException(NotFoundTodoException e) {
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
