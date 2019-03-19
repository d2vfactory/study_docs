package com.d2vfactory.resttodolist.model.common;

import lombok.Getter;

public enum Status {

    ACTIVE("진행중"),
    COMPLETED("완료"),
    DELETED("삭제");

    @Getter
    private String korName;

    Status(String korName) {
        this.korName = korName;
    }

}
