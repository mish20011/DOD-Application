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
    public Student(String studentId, String email, String fullName, String gender,
                   String yearBlock, String course, String profileImageUri) {
        this.studentId = studentId;
        this.email = email;
        this.fullName = fullName;
        this.gender = gender;
        this.yearBlock = yearBlock;
        this.course = course;
        this.profileImageUri = profileImageUri;
        this.violations = new ArrayList<>(); // Initialize the violations list
        this.role = "student"; // Set default role as student
    }

    // Getters and Setters...

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