package com.catignascabela.dodapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.catignascabela.dodapplication.databinding.ActivityLoginBinding;
import com.catignascabela.dodapplication.databinding.DialogRegistrationChoiceBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private DialogRegistrationChoiceBinding dialogBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        setupTextWatchers();
        binding.button.setOnClickListener(v -> handleLogin());
        binding.signupButton.setOnClickListener(v -> showRegistrationDialog());
        binding.cantRecover.setOnClickListener(v -> showForgotPasswordActivity());
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
        String email = binding.userid.getText().toString().trim();
        String password = binding.password.getText().toString().trim();

        binding.useridUnderline.setVisibility(View.GONE);
        binding.passwordUnderline.setVisibility(View.GONE);

        if (email.isEmpty() || password.isEmpty()) {
            binding.useridUnderline.setVisibility(email.isEmpty() ? View.VISIBLE : View.GONE);
            binding.passwordUnderline.setVisibility(password.isEmpty() ? View.VISIBLE : View.GONE);
            Toast.makeText(LoginActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
        } else {
            authenticateWithEmail(email, password);
        }
    }

    private void authenticateWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserRoleAndNavigate(user.getUid());
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRoleAndNavigate(String uid) {
        firestore.collection("users").document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String role = document.getString("role");
                            navigateToHomepage("teacher".equals(role));
                        } else {
                            Toast.makeText(LoginActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
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
        finish();
    }

    private void showRegistrationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialogBinding = DialogRegistrationChoiceBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());

        AlertDialog dialog = builder.create();
        dialogBinding.radioGroup.clearCheck();

        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnConfirm.setOnClickListener(v -> {
            int selectedId = dialogBinding.radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(LoginActivity.this, "Please select an option.", Toast.LENGTH_SHORT).show();
            } else {
                String selectedRole = ((RadioButton) dialogBinding.getRoot().findViewById(selectedId)).getText().toString();
                if ("Student".equals(selectedRole)) {
                    startActivity(new Intent(this, StudentRegistrationActivity.class));
                } else if ("Teacher".equals(selectedRole)) {
                    startActivity(new Intent(this, TeacherRegistrationActivity.class));
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showForgotPasswordActivity() {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }
}