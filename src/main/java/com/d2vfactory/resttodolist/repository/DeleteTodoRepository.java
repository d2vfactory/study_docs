package com.d2vfactory.resttodolist.repository;

import com.d2vfactory.resttodolist.model.entity.DeleteTodo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeleteTodoRepository extends JpaRepository<DeleteTodo, Long> {
}
