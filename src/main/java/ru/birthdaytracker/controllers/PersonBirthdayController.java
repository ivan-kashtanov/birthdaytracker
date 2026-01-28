package ru.birthdaytracker.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.birthdaytracker.entity.PersonBirthday;
import ru.birthdaytracker.service.PersonBirthdayService;


import java.time.LocalDate;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/birthdays")
@RequiredArgsConstructor
@Tag(name = "", description = "API для работы с записями о дне рождении")
public class PersonBirthdayController {
    private final PersonBirthdayService personBirthdayService;

    /**
     * Получить все дни рождения
     * GET /api/birthdays
     */
    @Operation(description = "Получение списка всех записей со днями рождения c возможностью фильтрации")
    @GetMapping
    public ResponseEntity<List<PersonBirthday>> getAllBirthdays( @RequestParam(required = false) String search,
                                                                 @RequestParam(required = false) LocalDate searchDate,
                                                                 @RequestParam(required = false) Integer month,
                                                                 @RequestParam(required = false) Integer year,
                                                                 @RequestParam(defaultValue = "name") String sortBy,
                                                                 @RequestParam(defaultValue = "asc") String sortOrder) {
        try {
            return ResponseEntity.ok(personBirthdayService.getPersonBirthdays(search,searchDate,month,year,sortBy,sortOrder));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Получить список дней рождения относительно сегодняшнего дня на определенное количество дней вперед и назад
     *
     * @param days
     * @return
     */
    @Operation(description = "Получение списка всех записей со днями рождения относительно " +
            "сегодняшнего дня на определенное количество дней вперед и назад")
    @GetMapping("/around/{days}")
    public ResponseEntity<List<PersonBirthday>> getBirthdaysByDays(@PathVariable final int days) {
        try {
            return ResponseEntity.ok(personBirthdayService.getBirtdayAround(days));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Получение записи по id
     * @param id
     * @return
     */
    @Operation(description = "Получение конкретной записи дня рождения по id")
    @GetMapping("/{id}")
    public ResponseEntity<PersonBirthday> getBirthdaysById(@PathVariable final long id) {
        try {
            return ResponseEntity.ok(personBirthdayService.getBirthdayById(id));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Создание новой записи о дне рождении
     *
     * @param personBirthday
     * @return
     */
    @Operation(description = "Создание новой записи о дне рождении")
    @PostMapping("/create")
    public ResponseEntity<PersonBirthday> createBirthday( @RequestPart("person") PersonBirthday personBirthday,
                                                          @RequestPart(value = "photo", required = false) MultipartFile photo) {
        try {
            personBirthdayService.createPersonBirthday(personBirthday, photo);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Удаление записи по id
     * @param id
     * @return
     */
    @Operation(description = "Удаление записи дня рождения по id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBirthday(@PathVariable final long id) {
        try {
            personBirthdayService.deletePersonBirthday(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    /**
     * Получение списка дней рождения на сегодня
     * @return
     */
    @Operation(description = "Получение списка всех записей со днями рождения")
    @GetMapping("/today")
    public ResponseEntity<?> getBirthdayToday(){
        try{
            return ResponseEntity.ok(personBirthdayService.getTodayBirtday());
        }
        catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Редактирование записи
     * @param id
     * @param personBirthday
     * @return
     */
    @Operation(description = "Редактирование одной записи дня рождения")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateBirthday(  @PathVariable final long id,
                                              @RequestPart("person") PersonBirthday personBirthday,
                                              @RequestPart(value = "photo", required = false) MultipartFile photo) {
        try{
           return ResponseEntity.ok(personBirthdayService.updatePersonBirthday(id, personBirthday, photo));
        }catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Удаление фото у записи
     */
    @Operation(description = "Удаление фото у записи")
    @DeleteMapping("/{id}/photo")
    public ResponseEntity<?> deletePhoto(@PathVariable final long id) {
        try {
            personBirthdayService.deletePhoto(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }
}
