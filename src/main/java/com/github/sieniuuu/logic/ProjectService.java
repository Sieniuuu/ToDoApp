package com.github.sieniuuu.logic;

import com.github.sieniuuu.TaskConfigurationProperties;
import com.github.sieniuuu.model.*;
import com.github.sieniuuu.model.projection.GroupReadModel;
import com.github.sieniuuu.model.projection.GroupTaskWriteModel;
import com.github.sieniuuu.model.projection.GroupWriteModel;
import com.github.sieniuuu.model.projection.ProjectWriteModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectService {
    private ProjectRepository repository;
    private TaskGroupRepository taskGroupRepository;
    private TaskConfigurationProperties config;
    private TaskGroupService taskGroupService;


    public ProjectService(final ProjectRepository repository, final TaskGroupRepository taskGroupRepository,
                          final TaskConfigurationProperties config, final TaskGroupService taskGroupService) {
        this.repository = repository;
        this.taskGroupRepository = taskGroupRepository;
        this.config = config;
        this.taskGroupService = taskGroupService;
    }

    public List<Project> readAll() {
        return repository.findAll();
    }

    public Project save(final ProjectWriteModel toSave) {
        return repository.save(toSave.toProject());
    }

    public GroupReadModel createGroup(LocalDateTime deadline, int projectId) {
        if (!config.getTemplate().isAllowMultipleTasks() && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId)) {
            throw new IllegalStateException("Only one undone group from project is allowed");
        }
        return repository.findById(projectId)
                .map(project -> {
                    var targetGroup = new GroupWriteModel();
                    targetGroup.setDescription(project.getDescription());
                    targetGroup.setTasks(
                            project.getSteps().stream()
                                    .map(projectStep -> {
                                                var task = new GroupTaskWriteModel();
                                                task.setDescription(projectStep.getDescription());
                                                task.setDeadline(deadline.plusDays(projectStep.getDaysToDeadline()));
                                                return task;
                                            }
                                    ).collect(Collectors.toSet())
                    );
                    return taskGroupService.createGroup(targetGroup, project);
                }).orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));
    }
}