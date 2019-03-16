package com.d2vfactory.resttodolist.model.common;

import lombok.Getter;

public enum Status {

    TODO("진행중"),
    COMPLETED("완료"),
    DELETED("삭제");

    @Getter
    private String name;

    private Status(String name) {
        this.name = name;
    }

}
