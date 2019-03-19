package com.d2vfactory.resttodolist.model.entity;

import com.d2vfactory.resttodolist.model.common.Status;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Where(clause = "status = 'ACTIVE'")
@Table(name = "TODO")
public class ActiveTodo extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Enumerated(value = EnumType.STRING)
    private Status status = Status.ACTIVE;

    private LocalDateTime completeDate;

}
