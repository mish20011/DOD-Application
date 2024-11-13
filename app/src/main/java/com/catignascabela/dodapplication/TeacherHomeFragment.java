package com.catignascabela.dodapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.catignascabela.dodapplication.databinding.FragmentHomeTeacherBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TeacherHomeFragment extends Fragment {

    private FragmentHomeTeacherBinding binding;
    private List<Student> studentList;
    private StudentAdapter studentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeTeacherBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize Firebase Auth and get the current user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        // Set up the RecyclerView for displaying students
        binding.studentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        studentList = new ArrayList<>();
        studentAdapter = new StudentAdapter(studentList, student -> openManageViolationsFragment(student));
        binding.studentRecyclerView.setAdapter(studentAdapter);

        // Initialize Firestore reference for students
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference studentsRef = db.collection("students");

        // Load all students initially
        loadStudentsByDepartment(studentsRef, "All");

        // Set up department filter spinner
        String[] departmentOptions = {"All", "BS-Computer Science", "BS-Information Technology"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, departmentOptions);
        binding.departmentSpinner.setAdapter(adapter);

        // Set listener to reload list based on department selection
        binding.departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDepartment = departmentOptions[position];
                loadStudentsByDepartment(studentsRef, selectedDepartment);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadStudentsByDepartment(studentsRef, "All");
            }
        });

        // Load teacher's name if the user is logged in
        if (currentUser != null) {
            String teacherId = currentUser.getUid();
            loadTeacherName(teacherId);
        } else {
            binding.teacherName.setText("Teacher's Name");
            Toast.makeText(getActivity(), "User not logged in.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadStudentsByDepartment(CollectionReference studentsRef, String department) {
        studentsRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        studentList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Student student = document.toObject(Student.class);
                            if (student != null && (department.equals("All") || student.getCourse().equals(department))) {
                                studentList.add(student);
                            }
                        }
                        studentAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Failed to load students.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadTeacherName(String teacherId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("teachers").document(teacherId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String teacherName = task.getResult().getString("fullName");
                        if (teacherName != null) {
                            binding.teacherName.setText(teacherName);
                        } else {
                            binding.teacherName.setText("Teacher's Name");
                        }
                    } else {
                        binding.teacherName.setText("Teacher not found");
                    }
                });
    }

    private void openManageViolationsFragment(Student student) {
        ManageViolationsFragment manageViolationsFragment = new ManageViolationsFragment();

        // Pass the student data as arguments
        Bundle args = new Bundle();
        args.putString("studentId", student.getUid());
        args.putString("fullName", student.getFullName());
        manageViolationsFragment.setArguments(args);

        // Replace current fragment with ManageViolationsFragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, manageViolationsFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}
