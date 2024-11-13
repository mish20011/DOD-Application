package com.catignascabela.dodapplication;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private String uid; // Unique identifier
    private String email;
    private String fullName; // Full name
    private String first; // First name
    private String middle; // Middle initial
    private String last; // Last name
    private int born; // Birth year
    private String gender;
    private String yearBlock;
    private String course;
    private String profileImageUri;
    private List<Violation> violations; // List of Violation objects
    private String role; // User role
    private String studentId; // Student ID

    // Default constructor required for Firestore
    public Student() {
        this.violations = new ArrayList<>(); // Initialize violations list
        this.role = "student"; // Default role
    }

    // Constructor
    public Student(String uid, String email, String first, String middle, String last, int born,
                   String gender, String yearBlock, String course, String profileImageUri, String studentId) {
        this.uid = uid;
        this.email = email;
        this.first = first;
        this.middle = middle;
        this.last = last;
        this.born = born;
        this.gender = gender;
        this.yearBlock = yearBlock;
        this.course = course;
        this.profileImageUri = profileImageUri;
        this.violations = new ArrayList<>(); // Initialize violations list
        this.role = "student"; // Default role
        this.studentId = studentId; // Student ID
        this.fullName = first + " " + middle + " " + last; // Set full name
    }

    // Getters
    public String getUid() { return uid; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getFirst() { return first; }
    public String getMiddle() { return middle; }
    public String getLast() { return last; }
    public int getBorn() { return born; }
    public String getGender() { return gender; }
    public String getYearBlock() { return yearBlock; }
    public String getCourse() { return course; }
    public String getProfileImageUri() { return profileImageUri; }
    public List<Violation> getViolations() { return violations; }
    public String getRole() { return role; }
    public String getStudentId() { return studentId; }
}