package com.milsondev.downloaduploadfiles.db.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name="first_name")
    private String firstName;
    @Column(name="second_name")
    private String secondName;
    @Column(name="place_of_birth")
    private String placeOfBirth;

    public Student() {

    }

    public Student(String firstName, String secondName, String placeOfBirth) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.placeOfBirth = placeOfBirth;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    @Override
    public String toString() {
        return "Student{" + "firstName=" + firstName + ", secondName=" + secondName + ", placeOfBirth=" + placeOfBirth + '}';
    }

}
