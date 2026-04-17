package ru.mipt.todo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mipt.todo.model.TaskAttachment;
import ru.mipt.todo.service.AttachmentService;

/**
 * REST-контроллер для операций с отдельными вложениями (скачивание, удаление).
 * Эти эндпоинты не привязаны к конкретной задаче в URL.
 */
@RestController
@RequestMapping("/api/attachments")
@Tag(name = "Attachments", description = "Скачивание и удаление вложений")
public class AttachmentResourceController {

    private final AttachmentService attachmentService;

    public AttachmentResourceController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @GetMapping("/{attachmentId}")
    @Operation(summary = "Скачать вложение")
    public ResponseEntity<Resource> download(@PathVariable Long attachmentId) {
        Resource resource = attachmentService.download(attachmentId);
        TaskAttachment entity = attachmentService.getAttachmentEntity(attachmentId);
        String contentType = entity.getContentType() != null
                ? entity.getContentType()
                : "application/octet-stream";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + entity.getFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{attachmentId}")
    @Operation(summary = "Удалить вложение")
    public ResponseEntity<Void> delete(@PathVariable Long attachmentId) {
        attachmentService.delete(attachmentId);
        return ResponseEntity.noContent().build();
    }
}
