package com.d2vfactory.resttodolist.model.dto;

import com.d2vfactory.resttodolist.model.common.Status;
import com.d2vfactory.resttodolist.model.entity.Todo;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class ReferenceTodoDTO {

    private Long id;

    private String content;

    private Status status;

    private String statusName;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private LocalDateTime completeDate;

    public ReferenceTodoDTO(Todo todo) {
        this.id = todo.getId();
        this.content = todo.getContent();
        this.status = todo.getStatus();
        this.statusName = todo.getStatus().getName();
        this.createDate = todo.getCreateDate();
        this.updateDate = todo.getUpdateDate();
        this.completeDate = todo.getCompleteDate();
    }


}
