package com.d2vfactory.resttodolist.model.dto;

import com.d2vfactory.resttodolist.model.common.Status;
import com.d2vfactory.resttodolist.model.entity.Todo;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
//@ToString(exclude = {"reference", "referenced"})
@ToString
public class TodoDTO {

    private Long id;

    private String content;

    private String contentAndReferenced;

    private Status status;

    private String statusName;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private LocalDateTime completeDate;

    private List<ReferenceTodoDTO> reference;

    private Set<ReferenceTodoDTO> referenced;

    public TodoDTO(Todo todo) {
        this.id = todo.getId();
        this.content = todo.getContent();
        this.status = todo.getStatus();
        this.createDate = todo.getCreateDate();
        this.updateDate = todo.getUpdateDate();
        this.completeDate = todo.getCompleteDate();

        this.reference = todo.getReference().stream().map(ReferenceTodoDTO::new).collect(Collectors.toList());
        this.referenced = todo.getReferenced().stream().map(ReferenceTodoDTO::new).collect(Collectors.toSet());

        this.contentAndReferenced = content + " " +
                todo.getReferenced().stream()
                        .map(x -> "@" + x.getId())
                        .collect(Collectors.joining(" "))
                        .trim();

        this.statusName = todo.getStatus().getName();
    }

}
