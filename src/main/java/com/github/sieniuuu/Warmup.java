package com.github.sieniuuu;

import com.github.sieniuuu.model.Task;
import com.github.sieniuuu.model.TaskGroup;
import com.github.sieniuuu.model.TaskGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Warmup implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(Warmup.class);

    private final TaskGroupRepository taskGroupRepository;

    public Warmup(TaskGroupRepository taskGroupRepository) {
        this.taskGroupRepository = taskGroupRepository;
    }

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent contextRefreshedEvent) {
        logger.info("Application warmup after context refreshed");
        final String description = "ApplicationContextEvent";
        if (!taskGroupRepository.existsByDescription(description)) {
            logger.info("No required group found! Adding it");
            var group = new TaskGroup();
            group.setDescription(description);
            group.setTasks(Set.of(
                    new Task("contextClosedEvent", null, group),
                    new Task("contextRefreshedEvent", null, group),
                    new Task("contextStoppedEvent", null, group),
                    new Task("contextStartedEvent", null, group)
            ));
            taskGroupRepository.save(group);
        }
    }
}
