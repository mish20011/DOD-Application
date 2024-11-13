package com.catignascabela.dodapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ViolationAdapter extends RecyclerView.Adapter<ViolationAdapter.ViolationViewHolder> {

    private List<Violation> violationList;
    private OnViolationClickListener clickListener;

    public interface OnViolationClickListener {
        void onViolationClick(Violation violation);
    }

    public ViolationAdapter(List<Violation> violationList, OnViolationClickListener listener) {
        this.violationList = violationList;
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ViolationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_violation, parent, false);
        return new ViolationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViolationViewHolder holder, int position) {
        Violation violation = violationList.get(position);
        holder.bind(violation, clickListener);
    }

    @Override
    public int getItemCount() {
        return violationList.size();
    }

    static class ViolationViewHolder extends RecyclerView.ViewHolder {
        TextView violationTextView;
        TextView punishmentTextView;

        public ViolationViewHolder(@NonNull View itemView) {
            super(itemView);
            violationTextView = itemView.findViewById(R.id.violationTextView); // Adjust based on your item layout
            punishmentTextView = itemView.findViewById(R.id.punishmentTextView); // Add a punishment TextView
        }

        public void bind(final Violation violation, final OnViolationClickListener listener) {
            violationTextView.setText(violation.getDescription());
            punishmentTextView.setText(violation.getPunishment()); // Display punishment

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViolationClick(violation);
                }
            });
        }
    }
}
