package com.d2vfactory.resttodolist.repository;

import com.d2vfactory.resttodolist.model.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface TodoRepository extends JpaRepository<Todo, Long> {


    @Query("select distinct t from Todo t  left join fetch t.reference  left join fetch t.referenced order by t.id")
    List<Todo> fetchFindAll();

    @Query(
            value = "select distinct t from Todo t left join fetch t.reference  left join fetch t.referenced",
            countQuery = "select count(t) from Todo t"
    )
    Page<Todo> fetchFindAll(Pageable pageable);

    Set<Todo> findAllByIdIn(Long... ids);

}
