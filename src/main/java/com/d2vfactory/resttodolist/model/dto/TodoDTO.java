package com.d2vfactory.resttodolist.model.dto;

import com.d2vfactory.resttodolist.model.common.Status;
import com.d2vfactory.resttodolist.model.entity.Todo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.core.Relation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@ToString
@Relation(collectionRelation = "todoList")
public class TodoDTO {

    private Long id;

    private String content;

    private String contentAndReferenced;

    private Status status;

    private String statusName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completeDate;

    private List<ReferenceTodoDTO> reference;

    private List<ReferenceTodoDTO> referenced;

    public TodoDTO(Todo todo) {
        this.id = todo.getId();
        this.content = todo.getContent();
        this.status = todo.getStatus();
        this.createDate = todo.getCreateDate();
        this.updateDate = todo.getUpdateDate();
        this.completeDate = todo.getCompleteDate();

        this.reference = todo.getReference().stream().map(ReferenceTodoDTO::new).collect(Collectors.toList());
        this.referenced = todo.getReferenced().stream().map(ReferenceTodoDTO::new).collect(Collectors.toList());

        this.contentAndReferenced = content + " " +
                todo.getReferenced().stream()
                        .map(x -> "@" + x.getId())
                        .collect(Collectors.joining(" "))
                        .trim();

        this.statusName = todo.getStatus().getKorName();
    }

}
