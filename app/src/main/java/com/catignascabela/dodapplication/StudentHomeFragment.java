package com.catignascabela.dodapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.catignascabela.dodapplication.databinding.FragmentHomeStudentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class StudentHomeFragment extends Fragment {

    private FragmentHomeStudentBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeStudentBinding.inflate(inflater, container, false);

        // Initialize Firebase Authentication and Firestore
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Fetch student data based on their student ID
        fetchStudentData();

        return binding.getRoot();
    }

    private void fetchStudentData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            firestore.collection("students").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot studentSnapshot = task.getResult();
                            if (studentSnapshot != null && studentSnapshot.exists()) {
                                Student student = studentSnapshot.toObject(Student.class);

                                if (student != null) {
                                    // Populate the UI with student details
                                    binding.studentIdTextView.setText("ID: " + student.getUid());
                                    binding.genderTextView.setText("Gender: " + student.getGender());
                                    binding.collegeYearTextView.setText("Year/Block: " + student.getYearBlock());
                                    binding.courseTextView.setText("Course: " + student.getCourse());
                                    binding.fullNameTextView.setText("Full Name: " + student.getFullName());

                                    // Fetch the student’s violation data
                                    fetchStudentViolations(userId);
                                }
                            } else {
                                Log.e("StudentHomeFragment", "No student data found for user ID: " + userId);
                            }
                        } else {
                            Log.e("StudentHomeFragment", "Failed to fetch student data.", task.getException());
                        }
                    });
        } else {
            Log.e("StudentHomeFragment", "No authenticated user found.");
        }
    }

    private void fetchStudentViolations(String studentId) {
        Log.d("StudentHomeFragment", "Checking violations for student ID: " + studentId);

        firestore.collection("violations")
                .document(studentId)
                .collection("violationRecords")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot latestViolation = task.getResult().getDocuments().get(0);
                        String description = latestViolation.getString("description");
                        String punishment = latestViolation.getString("punishment");

                        if (description != null && punishment != null) {
                            // Format violation text
                            binding.violationStatusTextView.setText(
                                    "Violation type: " + description + " - " + punishment
                            );
                        } else {
                            Log.e("StudentHomeFragment", "Violation details are incomplete.");
                        }
                    } else {
                        Log.d("StudentHomeFragment", "No violations found for student.");
                        binding.violationStatusTextView.setText("You are clear of violations!");
                    }
                })
                .addOnFailureListener(e -> Log.e("StudentHomeFragment", "Error fetching violations.", e));
    }
}
