package ru.birthdaytracker.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.birthdaytracker.service.PhotoService;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getPhoto(@PathVariable String filename) {
        try {
            Path filePath = photoService.getPhotoPath(filename);

            if (!Files.exists(filePath)) {
                log.warn("Файл не найден: {}", filename);
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(filePath);

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("Ошибка при чтении файла {}: {}", filename, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<Resource> getPersonPhoto(@PathVariable Long personId) {
        try {

            String photoUrl = photoService.getPhotoUrl(personId);

            if (photoUrl == null) {
                return ResponseEntity.notFound().build();
            }

            String filename = photoUrl.substring(photoUrl.lastIndexOf('/') + 1);
            return getPhoto(filename);

        } catch (Exception e) {
            log.error("Ошибка при получении фото для personId {}: {}", personId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}