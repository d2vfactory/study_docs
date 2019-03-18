package com.d2vfactory.resttodolist.exceptions;

public class NotFoundTodoException extends RuntimeTodoException {

    public NotFoundTodoException() {
        super("할일 목록이 존재하지 않습니다.");
    }


}
