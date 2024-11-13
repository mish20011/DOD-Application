package com.catignascabela.dodapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.ViewHolder> {

    private List<SummaryItem> summaryItems;

    public SummaryAdapter(List<SummaryItem> summaryItems) {
        this.summaryItems = summaryItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_summary_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SummaryItem item = summaryItems.get(position);
        holder.offenseCategory.setText(item.getOffenseCategory());
        holder.description.setText(item.getDescription());
        holder.possibleSanctions.setText(item.getPossibleSanctions());
    }

    @Override
    public int getItemCount() {
        return summaryItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView offenseCategory;
        TextView description;
        TextView possibleSanctions;

        ViewHolder(View itemView) {
            super(itemView);
            offenseCategory = itemView.findViewById(R.id.offense_category);
            description = itemView.findViewById(R.id.description);
            possibleSanctions = itemView.findViewById(R.id.possible_sanctions);
        }
    }
}
