package ru.mipt.todo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.mipt.todo.dto.AttachmentResponseDto;
import ru.mipt.todo.model.TaskAttachment;
import ru.mipt.todo.service.AttachmentService;

import java.util.List;

/**
 * REST-контроллер для файловых вложений, привязанных к задачам.
 * Загрузка и список вложений привязаны к конкретной задаче.
 */
@RestController
@RequestMapping("/api/tasks/{taskId}/attachments")
@Tag(name = "Attachments", description = "Работа с вложениями задач")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузить файл к задаче")
    public ResponseEntity<AttachmentResponseDto> upload(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file) {
        AttachmentResponseDto dto = attachmentService.upload(taskId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    @Operation(summary = "Список вложений задачи")
    public ResponseEntity<List<AttachmentResponseDto>> listAttachments(@PathVariable Long taskId) {
        List<AttachmentResponseDto> list = attachmentService.listByTask(taskId);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(list.size()))
                .body(list);
    }
}
