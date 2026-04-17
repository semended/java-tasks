package ru.mipt.todo.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.mipt.todo.dto.TaskCreateDto;
import ru.mipt.todo.dto.TaskResponseDto;
import ru.mipt.todo.dto.TaskUpdateDto;
import ru.mipt.todo.model.Task;
import ru.mipt.todo.model.TaskAttachment;

import java.util.Collections;
import java.util.List;

/**
 * Маппер для преобразования Task <-> DTO.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "completed", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    Task toEntity(TaskCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    void updateEntity(TaskUpdateDto dto, @MappingTarget Task task);

    @Mapping(target = "attachmentIds", expression = "java(mapAttachmentIds(task.getAttachments()))")
    TaskResponseDto toResponseDto(Task task);

    default List<Long> mapAttachmentIds(List<TaskAttachment> attachments) {
        if (attachments == null) {
            return Collections.emptyList();
        }
        return attachments.stream()
                .map(TaskAttachment::getId)
                .toList();
    }
}
