package ru.mipt.todo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.mipt.todo.dto.AttachmentResponseDto;
import ru.mipt.todo.exception.AttachmentNotFoundException;
import ru.mipt.todo.exception.TaskNotFoundException;
import ru.mipt.todo.model.Task;
import ru.mipt.todo.model.TaskAttachment;
import ru.mipt.todo.repository.InMemoryAttachmentRepository;
import ru.mipt.todo.repository.InMemoryTaskRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Сервис для работы с файловыми вложениями.
 */
@Service
public class AttachmentService {

    private final Path storageDir;
    private final InMemoryTaskRepository taskRepository;
    private final InMemoryAttachmentRepository attachmentRepository;

    public AttachmentService(@Value("${app.attachments.dir:./attachments}") String dir,
                             InMemoryTaskRepository taskRepository,
                             InMemoryAttachmentRepository attachmentRepository) {
        this.storageDir = Paths.get(dir).toAbsolutePath().normalize();
        this.taskRepository = taskRepository;
        this.attachmentRepository = attachmentRepository;
        try {
            Files.createDirectories(this.storageDir);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create attachments directory", e);
        }
    }

    public AttachmentResponseDto upload(Long taskId, MultipartFile file) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        String storedName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path targetPath = storageDir.resolve(storedName);

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }

        TaskAttachment attachment = new TaskAttachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFilePath(targetPath.toString());
        attachment.setContentType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setCreatedAt(LocalDateTime.now());
        attachment.setTask(task);

        TaskAttachment saved = attachmentRepository.save(attachment);
        return toDto(saved);
    }

    public Resource download(Long attachmentId) {
        TaskAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException(attachmentId));
        try {
            Path filePath = Paths.get(attachment.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new AttachmentNotFoundException(attachmentId);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new AttachmentNotFoundException(attachmentId);
        }
    }

    public TaskAttachment getAttachmentEntity(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException(attachmentId));
    }

    public List<AttachmentResponseDto> listByTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException(taskId);
        }
        return attachmentRepository.findByTaskId(taskId).stream()
                .map(this::toDto)
                .toList();
    }

    public void delete(Long attachmentId) {
        TaskAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException(attachmentId));
        try {
            Files.deleteIfExists(Paths.get(attachment.getFilePath()));
        } catch (IOException e) {
            // файл мог быть уже удалён, не критично
        }
        attachmentRepository.delete(attachment);
    }

    private AttachmentResponseDto toDto(TaskAttachment a) {
        AttachmentResponseDto dto = new AttachmentResponseDto();
        dto.setId(a.getId());
        dto.setFileName(a.getFileName());
        dto.setContentType(a.getContentType());
        dto.setFileSize(a.getFileSize());
        dto.setCreatedAt(a.getCreatedAt());
        return dto;
    }
}
