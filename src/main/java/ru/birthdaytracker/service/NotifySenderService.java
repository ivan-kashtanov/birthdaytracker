package ru.birthdaytracker.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.birthdaytracker.entity.PersonBirthday;
import ru.birthdaytracker.repository.PersonBirthdayImpl;

import java.time.LocalDate;
import java.time.Period;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class NotifySenderService {

    private final PersonBirthdayImpl personBirthday;
    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.enabled}")
    private boolean emailEnabled;

    @Scheduled(cron = "${interval-in-cron}")
    public void sendEmail() {
        if (!emailEnabled) {
            log.info("Email is disabled");
            return;
        }
        List<PersonBirthday> personBirthdays = personBirthday.findAllByBirthdayNotToday();
        List<PersonBirthday> personBirthdaysToday = personBirthday.findTodayBirthday();
        if (!personBirthdaysToday.isEmpty()) {
            try {
                if (!personBirthdays.isEmpty()) {
                    for (PersonBirthday email : personBirthdays) {
                        SimpleMailMessage message = new SimpleMailMessage();
                        message.setFrom(fromEmail);
                        message.setTo(email.getEmail());
                        message.setSubject("Уведомление от Birthday Tracker c напоминанием о дне рождения");
                        message.setText(generateText(personBirthdaysToday));
                        mailSender.send(message);
                    }
                }else{
                    log.info("Сегодня нет ни у кого дня рождения");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String generateText(List<PersonBirthday> personBirthday) {
        StringBuilder message = new StringBuilder();
        message.append("УРА!!!  Сегодня день рождения!\n\n");
        message.append("Сегодня свои дни рождения празднуют:\n\n");

        for (PersonBirthday person : personBirthday) {
            message.append("• ").append(person.getFullName());
            message.append(", сегодня исполняется ");
            message.append(Period.between(person.getBirthday(), LocalDate.now()).getYears());
            message.append(" лет ");

            message.append("\n");
        }

        message.append("\nНе забудьте поздравить!\n\n");
        message.append("С уважением,\nBirthday Tracker");
        return message.toString();
    }

}
