package com.github.sieniuuu.reports;

import com.github.sieniuuu.model.event.TaskDone;
import com.github.sieniuuu.model.event.TaskEvent;
import com.github.sieniuuu.model.event.TaskUndone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ChangedTaskEventListener {

    public static final Logger logger = LoggerFactory.getLogger(ChangedTaskEventListener.class);
    private final PersistedTaskEventRepository persistedTaskEventRepository;

    public ChangedTaskEventListener(PersistedTaskEventRepository persistedTaskEventRepository) {
        this.persistedTaskEventRepository = persistedTaskEventRepository;
    }

    @Async
    @EventListener
    public void on(TaskDone event) {
        onChanged(event);
    }

    @Async
    @EventListener
    public void on(TaskUndone event) {
        onChanged(event);
    }

    private void onChanged(TaskEvent event) {
        logger.info("Got " + event);
        persistedTaskEventRepository.save(new PersistedTaskEvent(event));
    }
}
