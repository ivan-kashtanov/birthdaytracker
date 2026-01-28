package ru.birthdaytracker.utils;

import ru.birthdaytracker.entity.PersonBirthday;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Helpers {

    /**
     * Вспомогательная функция дял поиска по ФИО
     *
     * @param birthdays
     * @param search
     * @return
     */
    public static List<PersonBirthday> filterBySearchFullName(List<PersonBirthday> birthdays, String search) {
        return birthdays.stream().filter(b -> b.getFullName() != null &&
                        b.getFullName().toLowerCase().contains(search.toLowerCase())).
                collect(Collectors.toList());
    }

    /**
     * Вспомогательная функция для поиска по дню рождения
     *
     * @param birthdays
     * @param date
     * @return
     */
    public static List<PersonBirthday> filterBySearchBirthdate(List<PersonBirthday> birthdays, LocalDate date) {
        return birthdays.stream().filter(b -> b.getBirthday() != null &&
                        (b.getBirthday().getMonth() == date.getMonth() &&
                                b.getBirthday().getDayOfMonth() == date.getDayOfMonth() &&
                                b.getBirthday().getYear() == date.getYear())).
                collect(Collectors.toList());
    }

    /**
     * Вспомогательная функция для фильтрации по месяцу
     *
     * @param birthdays
     * @param month
     * @return
     */
    public static List<PersonBirthday> filterByMonth(List<PersonBirthday> birthdays, int month) {
        return birthdays.stream()
                .filter(b -> b.getBirthday() != null &&
                        b.getBirthday().getMonthValue() == month)
                .collect(Collectors.toList());
    }

    /**
     * Вспомогательная функция для фильтрации по году
     *
     * @param birthdays
     * @param year
     * @return
     */
    public static List<PersonBirthday> filterByYear(List<PersonBirthday> birthdays, int year) {
        return birthdays.stream()
                .filter(b -> b.getBirthday() != null &&
                        b.getBirthday().getYear() == year)
                .collect(Collectors.toList());
    }

    /**
     * Вспомогательная функция для сортировки по выбранному полю и тип сортировки(по убыванию либо возрастанию)
     *
     * @param birthdays
     * @param sortBy
     * @param sortOrder
     * @return
     */
    public static List<PersonBirthday> sortBirthdays(List<PersonBirthday> birthdays, String sortBy, String sortOrder) {
        Comparator<PersonBirthday> comparator;

        switch (sortBy.toLowerCase()) {
            case "date":
            case "birthday":
                comparator = Comparator.comparing(PersonBirthday::getBirthday);
                break;
            case "name":
            case "fullname":
            default:
                comparator = Comparator.comparing(PersonBirthday::getFullName,
                        Comparator.nullsLast(String::compareToIgnoreCase));
                break;
        }

        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }

        return birthdays.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}
