package com.github.sieniuuu.logic;

import com.github.sieniuuu.TaskConfigurationProperties;
import com.github.sieniuuu.model.Project;
import com.github.sieniuuu.model.ProjectRepository;
import com.github.sieniuuu.model.TaskGroup;
import com.github.sieniuuu.model.TaskGroupRepository;
import com.github.sieniuuu.model.projection.GroupReadModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class ProjectServiceTest {

    @Test
    @DisplayName("should throw IllegalStateException when confgured to all just" +
            "one group and the other undone group exsists")
    void createGroup_noMultipleGroupsConfig_And_undoneOpenGroupsExists_throwsIllegalStateException() {
        // given
        var mockGroupRepository = groupRepositoryReturning(true);

        TaskConfigurationProperties mocConfig = configurationReturning(false);

        var toTest = new ProjectService(null, mockGroupRepository, mocConfig);

        // when

        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 1));

        // then

        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("one undone group");

    }

    @Test
    @DisplayName("should throw IllegalArgumentException when confguration ok and no projects for a given id")
    void createGroup_configurationOk_And_noProjects_throwsIllegalArgumentException() {
        // given
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        //and

        var mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(true);

        //and
        TaskConfigurationProperties mocConfig = configurationReturning(true);

        var toTest = new ProjectService(mockRepository, null, mocConfig);

        // when

        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));

        // then

        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");

    }

    @Test
    @DisplayName("should throw IllegalArgumentException when confgured to allowe just one group no grups and no projects")
    void createGroup_noMultipleGroupsConfig_And_noUndoneGroupeExists_noProjects_throwsIllegalArgumentException() {
        // given
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        //and
        TaskGroupRepository mockGroupRepository = groupRepositoryReturning(false);

        TaskConfigurationProperties mocConfig = configurationReturning(true);

        var toTest = new ProjectService(mockRepository, mockGroupRepository, mocConfig);

        // when

        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));

        // then

        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");

    }

    @Test
    @DisplayName("should creat a new group form project")
    void createGroup_configurationOk_existingProject_createsAndSavesGroup() {
        //given
        var today = LocalDate.now().atStartOfDay();
        //and
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        //and
        InMemoryGroupRepository inMemoryGroupRepo = inMemoryGroupRepository();
        int countBeforeCall = inMemoryGroupRepo.count();
        //and
        TaskConfigurationProperties mockConfig = configurationReturning(true);

        //system under test
        var toTest = new ProjectService(mockRepository, inMemoryGroupRepo, mockConfig);

        //whent
        GroupReadModel result = toTest.createGroup(today, 1);

        //then

//        assertThat(result)

        assertThat(countBeforeCall + 1)
                .isNotEqualTo(inMemoryGroupRepo.count());
    }

    private Project projectWith(String description) {
        var result = mock(Project.class);
        when(result.getDescription()).thenReturn(description);
        
    }

    private InMemoryGroupRepository inMemoryGroupRepository() {
        return new InMemoryGroupRepository();
    }

    private static class InMemoryGroupRepository implements TaskGroupRepository {

        private int index = 0;
        private Map<Integer, TaskGroup> map = new HashMap<>();

        public int count() {
            return map.values().size();
        }

        @Override
        public List<TaskGroup> findAll() {
            return new ArrayList<>(map.values());
        }

        @Override
        public Optional<TaskGroup> findById(Integer id) {
            return Optional.ofNullable(map.get(id));
        }

        @Override
        public TaskGroup save(TaskGroup entity) {
            if (entity.getId() == 0) {
                try {
                    TaskGroup.class.getDeclaredField("id").set(entity, ++index);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            map.put(entity.getId(), entity);
            return null;
        }

        @Override
        public boolean existsByDoneIsFalseAndProject_Id(Integer projectId) {
            return map.values().stream()
                    .filter(group -> !group.isDone())
                    .anyMatch(group -> group.getProject() != null && group.getProject().getId() == projectId);
        }
    };


    private TaskGroupRepository groupRepositoryReturning(boolean result) {
        var mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(result);
        return mockGroupRepository;
    }

    private TaskConfigurationProperties configurationReturning(boolean b) {
        var mockTemplate = mock(TaskConfigurationProperties.Template.class);
        when(mockTemplate.isAllowMultipleTasks()).thenReturn(b);

        var mocConfig = mock(TaskConfigurationProperties.class);
        when(mocConfig.getTemplate()).thenReturn(mockTemplate);
        return mocConfig;
    }
}