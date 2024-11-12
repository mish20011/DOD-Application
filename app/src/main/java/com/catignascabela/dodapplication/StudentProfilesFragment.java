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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.catignascabela.dodapplication.databinding.FragmentStudentProfilesBinding; // Import your generated binding class

import java.util.ArrayList;
import java.util.List;

public class StudentProfilesFragment extends Fragment implements StudentAdapter.OnStudentClickListener {

    private FragmentStudentProfilesBinding binding; // Declare the binding variable
    private StudentAdapter studentAdapter;
    private List<Student> studentList;
    private List<Student> filteredList;
    private String currentFilter = "All"; // Default filter
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout using ViewBinding
        binding = FragmentStudentProfilesBinding.inflate(inflater, container, false);
        View view = binding.getRoot(); // Get the root view from the binding

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
        // Setting up the spinner to filter by department
        Spinner departmentSpinner = binding.departmentSpinner; // Use binding to access the Spinner
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
        // Reference to the students collection in Firestore
        db.collection("students") // Ensure the collection name matches your Firestore setup
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null) {
                            studentList.clear();
                            for (QueryDocumentSnapshot document : snapshot) {
                                // Create Student object from Firestore document
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
                // Filter by course or department
                if (student.getCourse() != null && student.getCourse().equals(currentFilter)) {
                    filteredList.add(student);
                }
            }
        }

        studentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStudentClick(Student student) {
        // Handle click event, e.g., show student details
        Toast.makeText(getContext(), "Clicked: " + student.getFullName(), Toast.LENGTH_SHORT).show();
        // You can navigate to another fragment or activity to show more details about the student
        // Example: navigateToStudentDetailFragment(student);
    }

    // Uncomment and implement this method if you want to navigate to a detailed view
    /*
    private void navigateToStudentDetailFragment(Student student) {
        StudentDetailFragment detailFragment = StudentDetailFragment.newInstance(student);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, detailFragment) // Update with your fragment container ID
                .addToBackStack(null)
                .commit();
    }
    */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks by setting binding to null
    }
}