package ru.birthdaytracker.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.birthdaytracker.entity.PersonBirthday;
import ru.birthdaytracker.repository.PersonBirthdayImpl;
import ru.birthdaytracker.utils.Helpers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class PersonBirthdayService {
    private final PersonBirthdayImpl personBirthdayImpl;
    private final PhotoService photoService;

    /**
     * Получение всех активных дней рождения c фильтрами и сортировкой
     */
    public List<PersonBirthday> getPersonBirthdays(String search,
                                                   LocalDate searchDate,
                                                   Integer month,
                                                   Integer year,
                                                   String sortBy,
                                                   String sortOrder) {
        log.info("Get all active person birthdays");

        List<PersonBirthday> birthdayList = personBirthdayImpl.findAllPersonBirthdays();

        if(search!= null && !search.trim().isEmpty()) {
            birthdayList = Helpers.filterBySearchFullName(birthdayList, search);
        }

        if(searchDate != null){
            birthdayList = Helpers.filterBySearchBirthdate(birthdayList, searchDate);
        }

        if (month != null && month >= 1 && month <= 12) {
            birthdayList = Helpers.filterByMonth(birthdayList, month);
        }

        if (year != null && year >= 1900 && year <= 2100) {
            birthdayList = Helpers.filterByYear(birthdayList, year);
        }

        birthdayList = Helpers.sortBirthdays(birthdayList, sortBy, sortOrder);

        return birthdayList;
    }

    /**
     * Поулчение дней рождения на сегодня
     */
    public List<PersonBirthday> getTodayBirtday() {
        log.info("Get today birthdays");
        return personBirthdayImpl.findTodayBirthday();
    }

    /**
     * Поиск записи по имени пользователя
     */
    public PersonBirthday getPersonBirthdayByFullName(String fullName) {
        log.info("Get person birthday by Full name");
        return personBirthdayImpl.findPersonBirthdayByFullName(fullName);
    }

    /**
     * Поулчение дней рождения на сегодня
     */
    public List<PersonBirthday> getBirtdayAround(int days) {
        log.info("Get birthdays around current");
        return personBirthdayImpl.findBirthdayAroundCurrent(days);
    }

    /**
     * Получение записи по id
     * @param id
     * @return
     */
    public PersonBirthday getBirthdayById(long id) {
        log.info("Get birthday by ID");
        PersonBirthday birth = personBirthdayImpl.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        return birth;
    }

    /**
     * Создание новой записи
     */
    @Transactional
    public PersonBirthday createPersonBirthday(PersonBirthday personBirthday, MultipartFile photo) throws IOException {
        log.info("Create person birthday");
        PersonBirthday savedPersonBirthday = personBirthdayImpl.save(personBirthday);

        if (photo != null && !photo.isEmpty()) {
            savedPersonBirthday = photoService.savePhoto(savedPersonBirthday.getId(), photo);
        }
        return savedPersonBirthday;
    }

    /**
     * Редактирование записи
     */
    @Transactional
    public PersonBirthday updatePersonBirthday(long id, PersonBirthday personBirthday, MultipartFile photo) throws IOException{
        log.info("Update person birthday");
        PersonBirthday birth = personBirthdayImpl.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person birthday with ID " + id + " not found"));
        if (personBirthday.getBirthday() != null) {
            birth.setBirthday(personBirthday.getBirthday());
        }
        if (birth.getFullName() != null && !birth.getFullName().trim().isEmpty()) {
            birth.setFullName(personBirthday.getFullName().trim());
        }
        if (birth.getEmail() != null && !birth.getEmail().trim().isEmpty()) {
            birth.setEmail(personBirthday.getEmail().trim());
        }

        PersonBirthday updated = personBirthdayImpl.save(birth);

        if (photo != null && !photo.isEmpty()) {
            updated = photoService.savePhoto(id, photo);
        }

        return updated;
    }

    /**
     * Удалить фото у записи
     */
    @Transactional
    public PersonBirthday deletePhoto(Long id) throws IOException {
        log.info("Delete photo for person birthday ID: {}", id);
        return photoService.deletePhoto(id);
    }

    /**
     * Получить URL фото для записи
     */
    public String getPhotoUrl(Long id) {
        PersonBirthday person = personBirthdayImpl.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person birthday with ID " + id + " not found"));
        return photoService.getPhotoUrl(id);
    }

    /**
     * Удаление записи
     */
    public void deletePersonBirthday(Long id) {
        log.info("Delete person birthday");
        PersonBirthday birth = personBirthdayImpl.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person birthday with ID " + id + " not found"));
        personBirthdayImpl.delete(birth);
    }

}
