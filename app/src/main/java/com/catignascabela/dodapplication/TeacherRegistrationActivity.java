package com.catignascabela.dodapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.catignascabela.dodapplication.databinding.ActivityTeacherRegistrationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

public class TeacherRegistrationActivity extends AppCompatActivity {

    private static final String TAG = "TeacherRegistration";
    private ActivityTeacherRegistrationBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTeacherRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Set the onClick listener for the register button
        binding.registerButton.setOnClickListener(v -> registerTeacher());
    }

    private void registerTeacher() {
        String email = binding.registerEmail.getText().toString().trim();
        String password = binding.registerPassword.getText().toString().trim();
        String fullName = binding.registerFullName.getText().toString().trim();
        String username = binding.registerUsername.getText().toString().trim();
        String department = getSelectedDepartment();

        // Validate input fields
        if (isAnyFieldEmpty(email, password, fullName, department, username)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Attempting to register user with email: " + email);

        // Firebase Authentication to create user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        handleUserRegistration(username, fullName, department, email);
                    } else {
                        handleRegistrationFailure(task.getException());
                    }
                });
    }

    private String getSelectedDepartment() {
        int selectedDepartmentId = binding.radioGroupDepartment.getCheckedRadioButtonId();
        RadioButton selectedDepartmentButton = findViewById(selectedDepartmentId);
        return selectedDepartmentButton != null ? selectedDepartmentButton.getText().toString() : "";
    }

    private boolean isAnyFieldEmpty(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void handleUserRegistration(String username, String fullName, String department, String email) {
        Teacher newTeacher = new Teacher(fullName, department, username, email, username);
        newTeacher.setRole("teacher");

        // Save user data to Firestore under "teachers" collection using username as the document ID
        firestore.collection("teachers").document(username)
                .set(newTeacher)
                .addOnCompleteListener(saveTask -> {
                    if (saveTask.isSuccessful()) {
                        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                        navigateToHomepage(true); // Navigate to HomepageActivity with isTeacher = true
                    } else {
                        Log.e(TAG, "Failed to save user data: ", saveTask.getException());
                        Toast.makeText(this, "Failed to save user data: " + saveTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToHomepage(boolean isTeacher) {
        Intent intent = new Intent(this, HomepageActivity.class);
        intent.putExtra("isTeacher", isTeacher); // Pass the isTeacher flag
        startActivity(intent);
        finish(); // Close this activity
    }

    private void handleRegistrationFailure(Exception exception) {
        Log.e(TAG, "Registration failed: ", exception);
        if (exception instanceof FirebaseAuthUserCollisionException) {
            Toast.makeText(this, "This email is already in use.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Registration failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}