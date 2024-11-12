package com.catignascabela.dodapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.catignascabela.dodapplication.databinding.ActivityStudentRegistrationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentRegistrationActivity extends AppCompatActivity {

    private ActivityStudentRegistrationBinding binding;
    private Bitmap profileImageBitmap;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Authentication and Firestore
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        progressBar = binding.progressBar; // Assuming you have a ProgressBar in your layout

        // Set the onClick listener for the register button
        binding.registerButton.setOnClickListener(v -> registerStudent());

        // Set the onClick listener for the image selection button
        binding.selectImageButton.setOnClickListener(v -> openGallery());
    }

    private void openGallery() {
        // Launch the gallery to pick an image
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> photoResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri imageUri = result.getData() != null ? result.getData().getData() : null;
                    if (imageUri != null) {
                        try {
                            profileImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            binding.imageViewProfile.setImageBitmap(profileImageBitmap); // Display the selected image
                        } catch (Exception e) {
                            Log.e("ImageError", "Error loading image", e);
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    private void registerStudent() {
        String studentId = binding.registerStudentId.getText().toString().trim();
        String firstName = binding.registerFirstName.getText().toString().trim();
        String surname = binding.registerSurname.getText().toString().trim();
        String middleInitial = binding.registerMiddleInitial.getText().toString().trim();
        String email = binding.registerEmail.getText().toString().trim();
        String password = binding.registerPassword.getText().toString().trim();
        String gender = binding.radioGroupGender.getCheckedRadioButtonId() == R.id.radio_male ? "Male" : "Female";
        String yearBlock = binding.registerYearBlock.getText().toString().trim();
        String course = binding.radioGroupCourse.getCheckedRadioButtonId() == R.id.radio_bs_computer_science ? "BS-Computer Science" : "BS-Information Technology";

        // Input validation
        if (studentId.isEmpty() || firstName.isEmpty() || surname.isEmpty() || middleInitial.isEmpty() || email.isEmpty() || password.isEmpty() || yearBlock.isEmpty() || course.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase Authentication
        progressBar.setVisibility(View.VISIBLE); // Show progress
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Create a new Student object
                        Student newStudent = new Student(studentId, firstName, surname, middleInitial, email, gender, yearBlock, course);

                        // Save user data to Firestore with studentId as document ID
                        firestore.collection("students").document(studentId).set(newStudent)
                                .addOnCompleteListener(saveTask -> {
                                    progressBar.setVisibility(View.GONE); // Hide progress
                                    if (saveTask.isSuccessful()) {
                                        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(this, LoginActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(this, "Failed to save user data: " + (saveTask.getException() != null ? saveTask.getException().getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        progressBar.setVisibility(View.GONE); // Hide progress
                        Toast.makeText(this, "Registration failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}