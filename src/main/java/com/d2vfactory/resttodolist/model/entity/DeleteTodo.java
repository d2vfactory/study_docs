package com.d2vfactory.resttodolist.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/*
 * repository.deleteAll 하기 위한 용도..
 * Entity TODO에서 @Where clause로 DELETED는 제외처리 했기 때문에,해당 repository에서는 deleteAll을 하지 못한다.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "TODO")
public class DeleteTodo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @OrderBy("id ASC")
    @JoinColumn(name = "reference_id")
    private Set<DeleteTodo> reference = new HashSet<>();

    @OrderBy("id ASC")
    @ManyToMany(mappedBy = "reference")
    private Set<DeleteTodo> referenced = new HashSet<>();

}
