package com.catignascabela.dodapplication;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.catignascabela.dodapplication.databinding.FragmentHomeTeacherBinding;

import java.util.ArrayList;
import java.util.List;

public class TeacherHomeFragment extends Fragment implements StudentAdapter.OnStudentClickListener {

    private FragmentHomeTeacherBinding binding;
    private StudentAdapter studentAdapter;
    private List<Student> studentList;
    private List<Student> filteredList;
    private String currentFilter = "All"; // Default filter
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeTeacherBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Setup RecyclerView
        binding.studentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        studentList = new ArrayList<>();
        filteredList = new ArrayList<>();
        studentAdapter = new StudentAdapter(filteredList, this);
        binding.studentRecyclerView.setAdapter(studentAdapter);

        // Set up department filter
        setupDepartmentFilter();
        loadStudents();

        return view;
    }

    private void setupDepartmentFilter() {
        Spinner departmentSpinner = binding.departmentSpinner; // Access the Spinner
        List<String> departments = new ArrayList<>();
        departments.add("All");
        departments.add("BSCS");
        departments.add("BSIT");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, departments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(adapter);

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentFilter = departments.get(position);
                filterStudentsByDepartment();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadStudents() {
        db.collection("students") // Reference to the students collection in Firestore
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null) {
                            studentList.clear();
                            for (QueryDocumentSnapshot document : snapshot) {
                                Student student = document.toObject(Student.class);
                                if (student != null) {
                                    studentList.add(student);
                                }
                            }
                            filterStudentsByDepartment(); // Filter students after loading
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load students: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterStudentsByDepartment() {
        filteredList.clear();

        if ("All".equals(currentFilter)) {
            filteredList.addAll(studentList);
        } else {
            for (Student student : studentList) {
                if (student.getCourse() != null && student.getCourse().equals(currentFilter)) {
                    filteredList.add(student);
                }
            }
        }

        studentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStudentClick(Student student) {
        // Placeholder for student click handling
        Toast.makeText(getContext(), "Clicked: " + student.getFullName(), Toast.LENGTH_SHORT).show();
        // You can implement student detail navigation here later
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}