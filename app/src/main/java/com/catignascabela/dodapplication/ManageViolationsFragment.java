package com.catignascabela.dodapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.catignascabela.dodapplication.databinding.FragmentManageViolationsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManageViolationsFragment extends Fragment {

    private FragmentManageViolationsBinding binding;
    private FirebaseFirestore firestore;
    private CollectionReference violationsRef;
    private List<String> violationTypes;
    private HashMap<String, String> violationMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentManageViolationsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        violationsRef = firestore.collection("violations");

        // Load violation types from the database
        loadViolationTypes();

        // Set up the add violation button
        binding.addViolationButton.setOnClickListener(v -> {
            String selectedType = binding.violationSpinner.getSelectedItem().toString();
            String comment = binding.commentEditText.getText().toString().trim();

            if (!selectedType.isEmpty() && !comment.isEmpty()) {
                addViolation(selectedType, comment);
            } else {
                Toast.makeText(getActivity(), "Please select a violation type and add a comment.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadViolationTypes() {
        violationsRef.document("types").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                violationTypes = new ArrayList<>();
                violationMap = new HashMap<>();

                task.getResult().getData().forEach((type, description) -> {
                    if (description != null) {
                        violationTypes.add(description.toString());
                        violationMap.put(description.toString(), type); // Store mapping for later use
                    }
                });

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_item, violationTypes);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.violationSpinner.setAdapter(adapter);
            } else {
                Toast.makeText(getActivity(), "Failed to load violation types.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addViolation(String violationType, String comment) {
        String violationId = violationsRef.document().getId(); // Unique ID for each violation record
        HashMap<String, String> violationData = new HashMap<>();
        violationData.put("type", violationMap.get(violationType)); // Get the corresponding violation type ID
        violationData.put("comment", comment);

        DocumentReference newViolationRef = violationsRef.document("records").collection("recordList").document(violationId);
        newViolationRef.set(violationData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getActivity(), "Violation added successfully.", Toast.LENGTH_SHORT).show();
                binding.commentEditText.setText(""); // Clear comment input
            } else {
                Toast.makeText(getActivity(), "Failed to add violation.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}