package com.catignascabela.dodapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.catignascabela.dodapplication.databinding.ActivityLoginBinding;
import com.catignascabela.dodapplication.databinding.DialogRegistrationChoiceBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private DialogRegistrationChoiceBinding dialogBinding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

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
        String idOrUsername = binding.userid.getText().toString().trim();
        String password = binding.password.getText().toString().trim();

        binding.useridUnderline.setVisibility(View.GONE);
        binding.passwordUnderline.setVisibility(View.GONE);

        if (idOrUsername.isEmpty() || password.isEmpty()) {
            binding.useridUnderline.setVisibility(idOrUsername.isEmpty() ? View.VISIBLE : View.GONE);
            binding.passwordUnderline.setVisibility(password.isEmpty() ? View.VISIBLE : View.GONE);
            Toast.makeText(LoginActivity.this, "Empty Login", Toast.LENGTH_SHORT).show();
        } else {
            loginUser(idOrUsername, password);
        }
    }

    private void loginUser(String idOrUsername, String password) {
        // Retrieve stored user credentials from SharedPreferences
        String storedPassword = sharedPreferences.getString(idOrUsername + "_password", null);
        String storedRole = sharedPreferences.getString(idOrUsername + "_role", null);

        if (storedPassword != null && storedPassword.equals(password)) {
            if (storedRole != null) {
                navigateToHomepage("teacher".equals(storedRole));
            } else {
                Toast.makeText(LoginActivity.this, "Invalid user role", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, "Invalid ID or password", Toast.LENGTH_SHORT).show();
        }
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

        Button btnCancel = dialogBinding.btnCancel;
        Button btnConfirm = dialogBinding.btnConfirm;

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            int selectedId = dialogBinding.radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(LoginActivity.this, "Please select an option.", Toast.LENGTH_SHORT).show();
            } else {
                RadioButton selectedRadioButton = dialogBinding.getRoot().findViewById(selectedId);
                String selectedRole = selectedRadioButton.getText().toString();

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
        // Launch password reset activity
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }

    // Sample method to populate SharedPreferences with mock data for testing
    private void populateMockData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("201-1346_password", "12345678");
        editor.putString("201-1346_role", "student");
        editor.putString("teacherUsername_password", "password123");
        editor.putString("teacherUsername_role", "teacher");
        editor.apply();
    }
}
