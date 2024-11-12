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
import com.google.firebase.firestore.ListenerRegistration;

public class StudentHomeFragment extends Fragment {

    private FragmentHomeStudentBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private ListenerRegistration listenerRegistration;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeStudentBinding.inflate(inflater, container, false);

        // Initialize Firebase Authentication and Firestore
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance(); // Initialize Firestore

        // Fetch user data from Firestore
        fetchStudentData();

        return binding.getRoot();
    }

    private void fetchStudentData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String studentId = currentUser.getUid(); // Get the current user's ID

            // Reference to the student's document in Firestore
            listenerRegistration = firestore.collection("students").document(studentId)
                    .addSnapshotListener((documentSnapshot, e) -> {
                        if (e != null) {
                            Log.e("StudentHomeFragment", "Listen failed.", e);
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            Student student = documentSnapshot.toObject(Student.class);

                            if (student != null) {
                                // Set the student details in the TextViews
                                binding.studentIdTextView.setText("ID: " + student.getStudentId());
                                binding.genderTextView.setText("Gender: " + student.getGender());
                                binding.collegeYearTextView.setText("Year: " + student.getYearBlock());
                                binding.courseTextView.setText("Course: " + student.getCourse());
                                binding.fullNameTextView.setText("Full Name: " + student.getFullName());

                                // Debugging log to verify student data retrieval
                                Log.d("StudentHomeFragment", "Student Data Retrieved: " + student);
                            } else {
                                Log.d("StudentHomeFragment", "Student data is null.");
                            }
                        } else {
                            Log.d("StudentHomeFragment", "No data found for user ID: " + studentId);
                        }
                    });
        } else {
            Log.d("StudentHomeFragment", "No current user found.");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Remove listener to avoid memory leaks
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}