package com.catignascabela.dodapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ViolationDialog {

    public static void showViolationDialog(@NonNull Context context, String[] violations, ViolationSelectionListener listener) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_violation, null);

        // Get references to the ListView and title TextView
        ListView violationListView = dialogView.findViewById(R.id.violation_list);
        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        dialogTitle.setText("Select Violation");

        // Create an ArrayAdapter for the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, violations);
        violationListView.setAdapter(adapter);

        // Create the dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setView(dialogView);

        // Set up the dialog actions
        builder.setNegativeButton("Cancel", null);

        // Show the dialog
        builder.setOnDismissListener(dialog -> { /* Handle dismiss if needed */ });
        // Explicitly declare the type of the dialog variable
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

        // Set up item click listener for the ListView
        violationListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedViolation = violations[position];

            // Add timestamp to the violation
            long timestamp = System.currentTimeMillis(); // Get current time in milliseconds

            // Notify the listener with the selected violation and timestamp
            listener.onViolationSelected(selectedViolation, timestamp); // Pass both violation and timestamp
            dialog.dismiss(); // Dismiss the dialog when an item is selected
        });
    }

    public interface ViolationSelectionListener {
        void onViolationSelected(String violation, long timestamp); // Add timestamp as an argument
    }
}
