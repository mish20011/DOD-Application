package com.catignascabela.dodapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.catignascabela.dodapplication.databinding.ActivityLoginBinding;
import com.catignascabela.dodapplication.databinding.DialogRegistrationChoiceBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private ProgressBar progressBar; // Loading indicator

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        progressBar = binding.progressBar; // Assuming you have a ProgressBar in your layout

        // Set up TextWatchers to handle text input
        setupTextWatchers();

        binding.button.setOnClickListener(v -> handleLogin());
        binding.signupButton.setOnClickListener(v -> showRegistrationDialog());
    }

    private void setupTextWatchers() {
        binding.userid.addTextChangedListener(createTextWatcher(binding.useridUnderline));
        binding.password.addTextChangedListener(createTextWatcher(binding.passwordUnderline));
    }

    private TextWatcher createTextWatcher(View underlineView) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                underlineView.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    private void handleLogin() {
        String idOrUsername = binding.userid.getText().toString().trim();
        String password = binding.password.getText().toString().trim();

        // Reset underline visibility
        binding.useridUnderline.setVisibility(View.GONE);
        binding.passwordUnderline.setVisibility(View.GONE);

        if (idOrUsername.isEmpty() || password.isEmpty()) {
            binding.useridUnderline.setVisibility(idOrUsername.isEmpty() ? View.VISIBLE : View.GONE);
            binding.passwordUnderline.setVisibility(password.isEmpty() ? View.VISIBLE : View.GONE);
            Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else if (!isValidEmail(idOrUsername) && !isValidStudentId(idOrUsername)) {
            Toast.makeText(LoginActivity.this, "Invalid ID format", Toast.LENGTH_SHORT).show();
        } else {
            // Show progress bar and disable the login button
            progressBar.setVisibility(View.VISIBLE);
            binding.button.setEnabled(false);
            loginUser(idOrUsername, password);
        }
    }

    private boolean isValidEmail(String email) {
        // Simple email validation regex
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return Pattern.matches(emailPattern, email);
    }

    private boolean isValidStudentId(String studentId) {
        // Implement your own student ID validation logic if necessary
        return !studentId.isEmpty(); // Example: just check if it's not empty
    }

    private void loginUser(String idOrUsername, String password) {
        if (isValidEmail(idOrUsername)) {
            // If input is an email, proceed with Firebase Auth directly
            authenticateWithEmail(idOrUsername, password);
        } else {
            // Assume input is a student ID or teacher username and look up the associated email
            lookupEmailFromIdOrUsername(idOrUsername, password);
        }
    }

    private void lookupEmailFromIdOrUsername(String idOrUsername, String password) {
        // Check in students collection
        firestore.collection("students")
                .whereEqualTo("studentId", idOrUsername)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot studentDoc = task.getResult().getDocuments().get(0);
                        String email = studentDoc.getString("email");
                        if (email != null) {
                            authenticateWithEmail(email, password);
                        } else {
                            // If not found in students, check teachers by username
                            lookupEmailFromTeacherUsername(idOrUsername, password);
                        }
                    } else {
                        // If not found in students, check teachers by username
                        lookupEmailFromTeacherUsername(idOrUsername, password);
                    }
                });
    }

    private void lookupEmailFromTeacherUsername(String username, String password) {
        firestore.collection("teachers")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot teacherDoc = task.getResult().getDocuments().get(0);
                        String email = teacherDoc.getString("email");
                        if (email != null) {
                            authenticateWithEmail(email, password);
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid ID or username", Toast.LENGTH_SHORT).show();
                            resetLoginUI();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid ID or username", Toast.LENGTH_SHORT).show();
                        resetLoginUI();
                    }
                });
    }

    private void authenticateWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    resetLoginUI(); // Reset UI regardless of result
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkUserRoleAndNavigate(user);
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void resetLoginUI() {
        progressBar.setVisibility(View.GONE); // Hide loading indicator
        binding.button.setEnabled(true); // Re-enable the login button
    }

    private void checkUserRoleAndNavigate(FirebaseUser user) {
        firestore.collection("teachers").document(user.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot teacherDoc = task.getResult();
                        if (teacherDoc.exists() && "teacher".equals(teacherDoc.getString("role"))) {
                            navigateToHomepage(true);
                        } else {
                            // Otherwise, check in the students collection
                            firestore.collection("students").document(user.getUid()).get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            DocumentSnapshot studentDoc = task2.getResult();
                                            if (studentDoc.exists() && "student".equals(studentDoc.getString("role"))) {
                                                navigateToHomepage(false);
                                            } else {
                                                Toast.makeText(LoginActivity.this, "User role not found", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Database error: " + task2.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Database error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToHomepage(boolean isTeacher) {
        Intent intent = new Intent(LoginActivity.this, HomepageActivity.class);
        intent.putExtra("isTeacher", isTeacher);
        startActivity(intent);
        finish(); // Close LoginActivity
    }

    private void showRegistrationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogRegistrationChoiceBinding dialogBinding = DialogRegistrationChoiceBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());

        AlertDialog dialog = builder.create();

        dialogBinding.radioStudent.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, StudentRegistrationActivity.class));
            dialog.dismiss(); // Close dialog after selection
        });

        dialogBinding.radioTeacher.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, TeacherRegistrationActivity.class));
            dialog.dismiss(); // Close dialog after selection
        });

        dialogBinding.buttonCancel.setOnClickListener(v -> dialog.dismiss()); // Close dialog on cancel

        dialog.show();
    }
}