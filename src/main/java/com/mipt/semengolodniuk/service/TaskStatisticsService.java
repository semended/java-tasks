package com.mipt.semengolodniuk.service;

import com.mipt.semengolodniuk.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service for comparing two repositories.
 */
@Service
public class TaskStatisticsService {

    private final TaskRepository primaryTaskRepository;
    private final TaskRepository stubTaskRepository;

    public TaskStatisticsService(TaskRepository primaryTaskRepository,
                                 @Qualifier("stubTaskRepository") TaskRepository stubTaskRepository) {
        this.primaryTaskRepository = primaryTaskRepository;
        this.stubTaskRepository = stubTaskRepository;
    }

    public String compareRepositories() {
        return "primary=" + primaryTaskRepository.findAll().size()
                + ", stub=" + stubTaskRepository.findAll().size();
    }
}
