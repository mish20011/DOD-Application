package com.catignascabela.dodapplication;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private String studentId;
    private String email;
    private String fullName;
    private String gender;
    private String yearBlock;
    private String course;
    private String profileImageUri;
    private List<String> violations; // List to store violations
    private String role; // Added role attribute

    // Default constructor required for Firebase calls
    public Student() {
        this.violations = new ArrayList<>(); // Initialize the violations list
        this.role = "student"; // Set default role as student
    }

    // Constructor
    public Student(String studentId, String firstName, String middleInitial, String surname, String email,
                   String gender, String yearBlock, String course) {
        this.studentId = studentId;
        this.email = email;
        this.fullName = firstName + " " + middleInitial + " " + surname; // Combine names into full name
        this.gender = gender;
        this.yearBlock = yearBlock;
        this.course = course;
        this.violations = new ArrayList<>(); // Initialize the violations list
        this.role = "student"; // Set default role as student
    }


    // Getters
    public String getStudentId() {
        return studentId;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getGender() {
        return gender;
    }

    public String getYearBlock() {
        return yearBlock;
    }

    public String getCourse() {
        return course;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public List<String> getViolations() {
        return violations;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", gender='" + gender + '\'' +
                ", yearBlock='" + yearBlock + '\'' +
                ", course='" + course + '\'' +
                ", profileImageUri='" + profileImageUri + '\'' +
                ", violations=" + violations +
                ", role='" + role + '\'' +
                '}';
    }
}