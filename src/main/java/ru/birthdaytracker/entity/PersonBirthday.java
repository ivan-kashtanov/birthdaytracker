package ru.birthdaytracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "person_birthday")
@Getter
@Setter
public class PersonBirthday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "email")
    private String email;

    @Column(name = "photo")
    private String photo;
}
