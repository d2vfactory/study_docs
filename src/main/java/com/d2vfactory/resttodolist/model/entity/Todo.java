package com.d2vfactory.resttodolist.model.entity;

import com.d2vfactory.resttodolist.model.common.Status;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Where(clause = "status <> 'DELETED'")
public class Todo extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Enumerated(value = EnumType.STRING)
    private Status status = Status.TODO;

    private LocalDateTime completeDate;

    @ManyToMany
    @OrderBy("id ASC")
    @JoinColumn(name = "reference_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<Todo> reference = new ArrayList<>();

    @OrderBy("id ASC")
    @ManyToMany(mappedBy = "reference")
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<Todo> referenced = new HashSet<>();


    @Builder
    public Todo(String content, List<Todo> reference) {
        this.content = content;
        if (reference == null)
            this.reference = new ArrayList<>();
        else
            this.reference = reference;
    }

}
