package com.github.sieniuuu.logic;

import com.github.sieniuuu.TaskConfigurationProperties;
import com.github.sieniuuu.model.ProjectRepository;
import com.github.sieniuuu.model.TaskGroupRepository;
import com.github.sieniuuu.model.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogicConfiguration {

    @Bean
    ProjectService projectService(ProjectRepository repository,
                                  TaskGroupRepository taskGroupRepository,
                                  TaskGroupService taskGroupService,
                                  TaskConfigurationProperties config) {
        return new ProjectService(repository, taskGroupRepository, config, taskGroupService);
    }

    @Bean
    TaskGroupService taskGroupService(TaskGroupRepository taskGroupRepository,
                                      TaskRepository taskRepository) {
        return new TaskGroupService(taskGroupRepository, taskRepository);
    }

}
