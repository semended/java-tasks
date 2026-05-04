package ru.mipt.todo.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Сервис статистики через JdbcTemplate (HW3 requirement).
 */
@Service
public class TaskStatisticsJdbcService {

    private final JdbcTemplate jdbcTemplate;

    public TaskStatisticsJdbcService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long countTasks() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tasks", Long.class);
        return count != null ? count : 0;
    }

    public long countCompleted() {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tasks WHERE completed = true", Long.class);
        return count != null ? count : 0;
    }

    public Map<String, Long> getStatistics() {
        long total = countTasks();
        long completed = countCompleted();
        return Map.of(
                "total", total,
                "completed", completed,
                "pending", total - completed
        );
    }

    /**
     * Количество задач в разрезе приоритетов.
     */
    public java.util.List<Map<String, Object>> getTasksCountByPriority() {
        String sql = "SELECT priority, COUNT(*) as task_count FROM tasks GROUP BY priority ORDER BY priority";
        return jdbcTemplate.query(sql, (rs, rowNum) -> Map.of(
                "priority", rs.getString("priority"),
                "count", rs.getLong("task_count")
        ));
    }

    /**
     * Пример использования RowMapper — получение названий задач вместе с количеством вложений.
     */
    public java.util.List<Map<String, Object>> getTasksWithAttachmentCount() {
        String sql = """
                SELECT t.id, t.title, COUNT(a.id) as attachment_count
                FROM tasks t
                LEFT JOIN task_attachments a ON a.task_id = t.id
                GROUP BY t.id, t.title
                ORDER BY t.id
                """;

        return jdbcTemplate.query(sql, new TaskAttachmentCountRowMapper());
    }

    private static class TaskAttachmentCountRowMapper implements RowMapper<Map<String, Object>> {
        @Override
        public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Map.of(
                    "id", rs.getLong("id"),
                    "title", rs.getString("title"),
                    "attachmentCount", rs.getLong("attachment_count")
            );
        }
    }
}
