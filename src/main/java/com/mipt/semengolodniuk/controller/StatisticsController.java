package com.mipt.semengolodniuk.controller;

import com.mipt.semengolodniuk.service.TaskStatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for repository statistics.
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final TaskStatisticsService taskStatisticsService;

    public StatisticsController(TaskStatisticsService taskStatisticsService) {
        this.taskStatisticsService = taskStatisticsService;
    }

    @GetMapping
    public String getStatistics() {
        return taskStatisticsService.compareRepositories();
    }
}
