package com.catignascabela.dodapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class StudentRegistrationActivity extends AppCompatActivity {

    private EditText studentId, surname, firstName, middleInitial, email, password, yearBlock;
    private RadioGroup genderGroup, courseGroup;
    private Button registerButton;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        initViews();

        // Set click listener for register button
        registerButton.setOnClickListener(v -> registerStudent());
    }

    private void initViews() {
        studentId = findViewById(R.id.register_student_id);
        surname = findViewById(R.id.register_surname);
        firstName = findViewById(R.id.register_first_name);
        middleInitial = findViewById(R.id.register_middle_initial);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        yearBlock = findViewById(R.id.register_year_block);
        genderGroup = findViewById(R.id.radio_group_gender);
        courseGroup = findViewById(R.id.radio_group_course);
        registerButton = findViewById(R.id.register_button);
    }

    private void registerStudent() {
        String studentIdValue = studentId.getText().toString().trim();
        String surnameValue = surname.getText().toString().trim();
        String firstNameValue = firstName.getText().toString().trim();
        String middleInitialValue = middleInitial.getText().toString().trim();
        String emailValue = email.getText().toString().trim();
        String passwordValue = password.getText().toString().trim();
        String yearBlockValue = yearBlock.getText().toString().trim();

        // Get selected gender
        String genderValue = getSelectedRadioButtonText(genderGroup);
        // Get selected course
        String courseValue = getSelectedRadioButtonText(courseGroup);

        // Validate input fields
        if (TextUtils.isEmpty(studentIdValue) || TextUtils.isEmpty(surnameValue) ||
                TextUtils.isEmpty(firstNameValue) || TextUtils.isEmpty(emailValue) ||
                TextUtils.isEmpty(passwordValue) || TextUtils.isEmpty(yearBlockValue) ||
                TextUtils.isEmpty(genderValue) || TextUtils.isEmpty(courseValue)) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register student in Firebase Authentication
        auth.createUserWithEmailAndPassword(emailValue, passwordValue)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        saveStudentData(studentIdValue, surnameValue, firstNameValue, middleInitialValue,
                                emailValue, genderValue, courseValue, yearBlockValue);
                    } else {
                        Toast.makeText(StudentRegistrationActivity.this,
                                "Registration failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getSelectedRadioButtonText(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedButton = findViewById(selectedId);
        return selectedButton != null ? selectedButton.getText().toString() : "";
    }

    private void saveStudentData(String studentId, String surname, String firstName, String middleInitial,
                                 String email, String gender, String course, String yearBlock) {
        HashMap<String, Object> studentData = new HashMap<>();
        studentData.put("studentId", studentId);
        studentData.put("surname", surname);
        studentData.put("firstName", firstName);
        studentData.put("middleInitial", middleInitial);
        studentData.put("email", email);
        studentData.put("gender", gender);
        studentData.put("course", course);
        studentData.put("yearBlock", yearBlock);

        DocumentReference studentRef = db.collection("students").document(studentId);
        studentRef.set(studentData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(StudentRegistrationActivity.this, "Student registered successfully", Toast.LENGTH_SHORT).show();
                    navigateToStudentHomeFragment();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(StudentRegistrationActivity.this, "Error creating user profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToStudentHomeFragment() {
        // Replace the current activity's content with the Student Home Fragment
        StudentHomeFragment studentHomeFragment = new StudentHomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, studentHomeFragment) // Ensure this ID matches your container ID
                .addToBackStack(null) // Optional: Add to back stack
                .commit();
    }
}