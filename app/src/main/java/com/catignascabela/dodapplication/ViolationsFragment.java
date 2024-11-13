package com.catignascabela.dodapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.catignascabela.dodapplication.databinding.FragmentViolationsBinding;

public class ViolationsFragment extends Fragment {

    private FragmentViolationsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentViolationsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Populate the TextViews with the violations content
        binding.title.setText("Student Discipline Violations");
        binding.generalPolicyDetails.setText("1. Purpose of Disciplinary Actions: Corrective measures to guide students towards expected conduct. Progressive discipline based on previous violations.\n\n" +
                "2. Types of Disciplinary Actions:\n" +
                "- Reprimand: A warning for future violations.\n" +
                "- Warning: A written notice that further violations may lead to more severe penalties.\n" +
                "- Suspension: Denial of attendance for serious offenses. Variants include:\n" +
                "   - Preventive Suspension: Immediate suspension during an investigation.\n" +
                "   - Reformative Suspension: Includes counseling and assessment sessions.\n" +
                "- Restitution: Compensation for damages to property.\n" +
                "- Non-readmission: Denial of admission for the next term after an offense.\n" +
                "- Exclusion: Permanent removal from student rolls for severe offenses.\n" +
                "- Expulsion: Permanent disqualification from all higher education institutions for serious misconduct.\n\n" +
                "3. Multiple Offenses: If multiple offenses occur simultaneously, the highest penalty applies.\n\n" +
                "4. Certificates:\n" +
                "- Certificate of Good Moral Character: Issued to students with no derogatory records.\n" +
                "- Certificate of Deportment: Issued for students with disciplinary records detailing corrective actions taken.\n\n" +
                "5. Handling of Major Cases: Resolutions are managed by the institution's Discipline Committee.");

        binding.offensesAndSanctions.setText("1. Offenses and Sanctions:\n\n" +
                "   - Light Offenses:\n" +
                "     - Non-conformity to uniform regulations (1st offense: Reprimand, 2nd offense: 1st Written Warning, 3rd offense: 2nd Written Warning, 4th offense: 3-day reformative suspension).\n" +
                "     - Littering and improper waste disposal.\n" +
                "     - Using electronic devices that disrupt classes.\n" +
                "     - Simple misconduct, including disruptive behavior and unauthorized use of the University logo.\n" +
                "     - Unauthorized entry to campus areas.\n\n" +
                "   - Serious Offenses:\n" +
                "     - Possession or distribution of pornographic materials.\n" +
                "     - Defacing University property.\n" +
                "     - Intimidation during school activities.\n" +
                "     - Alcohol consumption or gambling in public while in uniform.\n" +
                "     - Unauthorized use of ID.\n\n" +
                "   - Major Offenses:\n" +
                "     - Cheating and plagiarism.\n" +
                "     - Gross misconduct including theft and insubordination.\n" +
                "     - Violent acts against peers or staff.\n" +
                "     - Possession of drugs or weapons.\n" +
                "     - Sexual misconduct and public displays of intimacy.");

        // Add rows to the summary table
        addSummaryRow("Light Offenses", "Minor infractions, e.g., uniform violations, littering.", "Reprimand, Warnings, 3-day reformative suspension.");
        addSummaryRow("Serious Offenses", "More significant violations, e.g., possession of pornography, intimidation.", "Written warning to 2-week reformative suspension.");
        addSummaryRow("Major Offenses", "Severe infractions, e.g., cheating, theft, physical assault.", "Two-week suspension to exclusion or expulsion.");

        return view;
    }

    private void addSummaryRow(String offenseCategory, String description, String possibleSanctions) {
        TableRow tableRow = new TableRow(getContext());

        TextView categoryTextView = new TextView(getContext());
        categoryTextView.setText(offenseCategory);
        categoryTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

        TextView descriptionTextView = new TextView(getContext());
        descriptionTextView.setText(description);
        descriptionTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

        TextView sanctionsTextView = new TextView(getContext());
        sanctionsTextView.setText(possibleSanctions);
        sanctionsTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

        tableRow.addView(categoryTextView);
        tableRow.addView(descriptionTextView);
        tableRow.addView(sanctionsTextView);

        binding.summaryTable.addView(tableRow);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}
