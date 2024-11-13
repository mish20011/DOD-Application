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

import com.catignascabela.dodapplication.databinding.FragmentManageViolationsBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ManageViolationsFragment extends Fragment {

    private FragmentManageViolationsBinding binding;
    private List<Violation> violationList;
    private ViolationAdapter violationAdapter;
    private String studentId;
    private String studentName;

    private FirebaseFirestore firestore;

    public ManageViolationsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentManageViolationsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        firestore = FirebaseFirestore.getInstance();

        violationList = new ArrayList<>();
        violationAdapter = new ViolationAdapter(violationList, violation -> {
            Toast.makeText(getContext(), "Clicked: " + violation.getDescription(), Toast.LENGTH_SHORT).show();
        });

        // Set LayoutManager and Adapter
        binding.violationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.violationRecyclerView.setAdapter(violationAdapter);

        // Retrieve the student details from arguments
        if (getArguments() != null) {
            studentId = getArguments().getString("studentId");
            studentName = getArguments().getString("fullName");
        }

        // Display student name and details in UI
        if (studentName != null) {
            binding.studentName.setText(studentName);
            binding.studentDetails.setText("ID: " + studentId); // You can add more student details if needed
        }

        loadViolations(studentId);
        setupViolationSpinner();

        binding.addViolationButton.setOnClickListener(v -> openAddViolationDialog(studentId));
        binding.settleViolationButton.setOnClickListener(v -> markAsSettled(studentId));

        return view;
    }

    private void loadViolations(String studentId) {
        // This method should be updated to load violations from your Firestore or Realtime Database
        // Placeholder for the code that loads violations into violationList
    }

    private void setupViolationSpinner() {
        List<String> violationTypes = new ArrayList<>();
        violationTypes.add("Light Offenses");
        violationTypes.add("Serious Offenses");
        violationTypes.add("Major Offenses");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, violationTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.violationSpinner.setAdapter(adapter);

        binding.violationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = violationTypes.get(position);
                setupViolationDetails(selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupViolationDetails(String violationType) {
        String[] violations;
        switch (violationType) {
            case "Light Offenses":
                violations = new String[]{
                        "Non-conformity to uniform regulations",
                        "Littering and improper waste disposal",
                        "Using electronic devices that disrupt classes",
                        "Simple misconduct (disruptive behavior)",
                        "Unauthorized entry to campus areas"
                };
                break;
            case "Serious Offenses":
                violations = new String[]{
                        "Possession or distribution of pornographic materials",
                        "Defacing University property",
                        "Intimidation during school activities",
                        "Alcohol consumption or gambling in public while in uniform",
                        "Unauthorized use of ID"
                };
                break;
            case "Major Offenses":
                violations = new String[]{
                        "Cheating and plagiarism",
                        "Gross misconduct (theft, insubordination)",
                        "Violent acts against peers or staff",
                        "Possession of drugs or weapons",
                        "Sexual misconduct and public displays of intimacy"
                };
                break;
            default:
                violations = new String[]{};
        }

        ViolationDialog.showViolationDialog(getContext(), violations, new ViolationDialog.ViolationSelectionListener() {
            @Override
            public void onViolationSelected(String violation, long timestamp) {
                // Handle the violation and timestamp
                String customPunishment = binding.customPunishmentText.getText().toString().trim();

                if (!customPunishment.isEmpty()) {
                    // Create new Violation with the timestamp
                    Violation newViolation = new Violation(violation, customPunishment, timestamp);

                    // Now save the violation to Firestore
                    DocumentReference violationRef = firestore.collection("violations")
                            .document(studentId)
                            .collection("violationRecords")
                            .document();  // Automatically generate a new document ID

                    violationRef.set(newViolation)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Violation added successfully.", Toast.LENGTH_SHORT).show();
                                    // Clear the input fields
                                    binding.violationInput.setText("");
                                    binding.customPunishmentText.setText("");
                                } else {
                                    Toast.makeText(getContext(), "Failed to add violation.", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getContext(), "Please enter a custom punishment.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openAddViolationDialog(String studentId) {
        String violationType = binding.violationSpinner.getSelectedItem().toString();
        String violationDescription = binding.violationInput.getText().toString();
        String customPunishment = binding.customPunishmentText.getText().toString();

        if (violationDescription.isEmpty() || customPunishment.isEmpty()) {
            Toast.makeText(getContext(), "Please select a violation and enter a punishment.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Violation object using the updated constructor
        Violation newViolation = new Violation(violationType + " - " + violationDescription, customPunishment, System.currentTimeMillis());

        DocumentReference violationsRef = firestore.collection("violations").document(studentId).collection("violationRecords").document();
        violationsRef.set(newViolation).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Violation added successfully", Toast.LENGTH_SHORT).show();
                binding.violationInput.setText("");
                binding.customPunishmentText.setText("");
            } else {
                Toast.makeText(getContext(), "Failed to add violation", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markAsSettled(String studentId) {
        // Logic to mark violation as settled
    }
}
