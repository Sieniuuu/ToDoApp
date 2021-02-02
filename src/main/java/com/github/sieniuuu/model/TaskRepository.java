package com.github.sieniuuu.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource
public interface TaskRepository extends JpaRepository <Task, Integer> {

    @Override
    @RestResource(exported = false)
    // dzięki przeciążeniu tych metod zapytania delete nie są już możliwe, błąd 405 Method Not Allowed
    void deleteById(Integer integer);

    @Override
    @RestResource(exported = false)
    void delete(Task task);

    @RestResource(path = "done", rel = "done")
    List<Task> findByDone(@Param("state") boolean done);

}
