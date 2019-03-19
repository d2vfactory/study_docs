package com.d2vfactory.resttodolist.repository;

import com.d2vfactory.resttodolist.model.entity.ActiveTodo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActiveTodoRepository extends JpaRepository<ActiveTodo, Long> {
}
