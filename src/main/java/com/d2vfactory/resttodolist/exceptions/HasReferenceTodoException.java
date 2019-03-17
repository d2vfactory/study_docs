package com.d2vfactory.resttodolist.exceptions;

public class HasReferenceTodoException extends RuntimeException {

    public HasReferenceTodoException(){
        super("참조된 할일 목록이 존재합니다.");
    }

    public HasReferenceTodoException(String message){
        super(message);
    }
}
