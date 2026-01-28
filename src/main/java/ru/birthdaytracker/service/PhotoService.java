package ru.birthdaytracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.birthdaytracker.entity.PersonBirthday;
import ru.birthdaytracker.repository.PersonBirthdayImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PersonBirthdayImpl personBirthdayRepository;

    @Value("${app.photos.dir}")
    private String photosDir;

    @Value("${app.photos.url}")
    private String photosUrl;

    /**
     * Сохранить фотографию
     */
    public PersonBirthday savePhoto(Long personId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл пустой");
        }

        PersonBirthday person = personBirthdayRepository.findById(personId)
                .orElseThrow(() -> new IllegalArgumentException("Запись не найдена"));

        deletePhotoFile(person.getPhoto());

        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);
        String newFileName = UUID.randomUUID() + fileExtension;

        saveFile(file, newFileName);

        person.setPhoto(newFileName);
        return personBirthdayRepository.save(person);
    }

    /**
     * Удалить фотографию
     */
    public PersonBirthday deletePhoto(Long personId) throws IOException {
        PersonBirthday person = personBirthdayRepository.findById(personId)
                .orElseThrow(() -> new IllegalArgumentException("Запись не найдена"));

        deletePhotoFile(person.getPhoto());

        person.setPhoto(null);
        return personBirthdayRepository.save(person);
    }

    /**
     * Получить URL фотографии
     */
    public String getPhotoUrl(Long personId) {
        PersonBirthday person = personBirthdayRepository.findById(personId)
                .orElseThrow(() -> new IllegalArgumentException("Запись не найдена"));

        if (person.getPhoto() == null) {
            return null;
        }
        return photosUrl + "/" + person.getPhoto();
    }

    /**
     * Получить полный путь к файлу
     */
    public Path getPhotoPath(String photoName) {
        return Paths.get(photosDir).resolve(photoName);
    }

    /**
     * Проверить существование фото
     */
    public boolean photoExists(String photoName) {
        if (photoName == null) return false;
        return Files.exists(getPhotoPath(photoName));
    }


    private void saveFile(MultipartFile file, String fileName) throws IOException {
        Path uploadPath = Paths.get(photosDir);
        Files.createDirectories(uploadPath);
        Path targetLocation = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
    }

    private void deletePhotoFile(String photoName) throws IOException {
        if (photoName != null) {
            Path filePath = getPhotoPath(photoName);
            Files.deleteIfExists(filePath);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ".jpg";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}