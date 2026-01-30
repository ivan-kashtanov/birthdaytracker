package ru.birthdaytracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.birthdaytracker.entity.PersonBirthday;

import java.util.List;
import java.util.Optional;

public interface PersonBirthdayImpl extends JpaRepository<PersonBirthday, Long> {
    /**
    * Поиск всех активных записей дней рождения
    */
    @Query("select p from PersonBirthday p " +
            "ORDER BY p.birthday ASC")
    List<PersonBirthday> findAllPersonBirthdays();

    /**
     * Поиск всех ближайжих на N записей дней рождения
     */
    @Query(value = "select * from person_birthday pb where MAKE_DATE(EXTRACT(YEAR FROM CURRENT_DATE)::integer," +
            "    EXTRACT(MONTH FROM pb.birthday)::integer, " +
            "    EXTRACT(DAY FROM pb.birthday)::integer) " +
            "    BETWEEN " +
            "    CURRENT_DATE - (:period ||(' days'))::interval " +
            "    AND" +
            "    CURRENT_DATE + (:period ||(' days'))::interval", nativeQuery = true)
    List<PersonBirthday> findBirthdayAroundCurrent(@Param("period") int period);

    /**
     * Поиск сегодняшних дней рождений
     */
    @Query("select p from PersonBirthday p where  " +
            "month(p.birthday) = month(CURRENT_DATE) and " +
            "day(p.birthday) = day(CURRENT_DATE)")
    List<PersonBirthday> findTodayBirthday();

    /**
     *Поиск записи по имени
     */
    PersonBirthday findPersonBirthdayByFullName(String fullName);

    /**
     * Поиск записи по id
     */
    Optional<PersonBirthday> findById(long id);

    /**
     * Поиск всех дней рождений которые не сегодня
     * @return
     */
    @Query("select p from PersonBirthday p where  " +
            "month(p.birthday) != month(CURRENT_DATE) and " +
            "day(p.birthday) != day(CURRENT_DATE)")
    List<PersonBirthday> findAllByBirthdayNotToday();
}
