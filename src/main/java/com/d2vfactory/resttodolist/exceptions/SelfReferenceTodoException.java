package com.d2vfactory.resttodolist.exceptions;

public class SelfReferenceTodoException extends RuntimeTodoException {

    public SelfReferenceTodoException() {
        super("자기 자신을 참조할 수 없습니다.");
    }

}
