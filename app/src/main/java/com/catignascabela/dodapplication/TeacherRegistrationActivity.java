package com.catignascabela.dodapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.catignascabela.dodapplication.databinding.ActivityTeacherRegistrationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class TeacherRegistrationActivity extends AppCompatActivity {

    private ActivityTeacherRegistrationBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTeacherRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Authentication and Firestore
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Set the onClick listener for the register button
        binding.registerButton.setOnClickListener(v -> registerTeacher());
    }

    private void registerTeacher() {
        String email = binding.registerEmail.getText().toString().trim();
        String password = binding.registerPassword.getText().toString().trim();
        String fullName = binding.registerFullName.getText().toString().trim();
        String username = binding.registerUsername.getText().toString().trim(); // Username field

        // Get selected department
        int selectedDepartmentId = binding.radioGroupDepartment.getCheckedRadioButtonId();
        RadioButton selectedDepartmentButton = findViewById(selectedDepartmentId);
        String department = selectedDepartmentButton != null ? selectedDepartmentButton.getText().toString() : "";

        // Input validation
        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || department.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return; // Exit if any field is empty
        }

        // Firebase Authentication to create user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Create a Teacher object with the role
                        Teacher newTeacher = new Teacher(fullName, department, user.getUid(), email, username);
                        newTeacher.setRole("teacher"); // Set role

                        // Save user data to Firestore
                        if (user != null) {
                            firestore.collection("teachers").document(username).set(newTeacher)
                                    .addOnCompleteListener(saveTask -> {
                                        if (saveTask.isSuccessful()) {
                                            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(this, LoginActivity.class));
                                            finish(); // Finish this activity to remove it from the back stack
                                        } else {
                                            Toast.makeText(this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // If registration fails, display a message
                        Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}