package com.github.sieniuuu.logic;

import com.github.sieniuuu.model.Task;
import com.github.sieniuuu.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskService {

    private Logger logger = LoggerFactory.getLogger(TaskService.class);
    private TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Async
    public CompletableFuture<List<Task>> findAllAsync() {
        logger.info("Supply async");
        return CompletableFuture.supplyAsync(taskRepository::findAll);
    }
}
