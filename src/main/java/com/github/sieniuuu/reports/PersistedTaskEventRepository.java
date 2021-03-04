package com.github.sieniuuu.reports;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface PersistedTaskEventRepository extends JpaRepository<PersistedTaskEvent, Integer> {

    @Query("SELECT e FROM PersistedTaskEvent e WHERE e.taskId = :id")
    List<PersistedTaskEvent> findByTaskId(@Param("id") int id);

}
