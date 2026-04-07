package com.mipt.semengolodniuk.service;

import com.mipt.semengolodniuk.repository.TaskRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service that demonstrates simultaneous injection of primary and qualified repositories.
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

    public Map<String, Object> getRepositoryStatistics() {
        Map<String, Object> statistics = new LinkedHashMap<>();
        statistics.put("primaryRepository", primaryTaskRepository.getClass().getSimpleName());
        statistics.put("primaryRepositoryTaskCount", primaryTaskRepository.findAll().size());
        statistics.put("qualifiedRepository", stubTaskRepository.getClass().getSimpleName());
        statistics.put("qualifiedRepositoryTaskCount", stubTaskRepository.findAll().size());
        return statistics;
    }
}
