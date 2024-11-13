package com.catignascabela.dodapplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class ViolationListAdapter extends ArrayAdapter<String> {
    private Context context;
    private String[] offenses;
    private int selectedPosition = -1; // To track the selected position

    public ViolationListAdapter(Context context, String[] offenses) {
        super(context, android.R.layout.simple_list_item_1, offenses);
        this.context = context;
        this.offenses = offenses;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the default list item view
        View view = super.getView(position, convertView, parent);

        // Set the background color for the selected item
        if (position == selectedPosition) {
            // Highlight the selected item
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.selectedItemBackground)); // You can define this color in colors.xml
        } else {
            // Normal background
            view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }

        // Set the text for the list item
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(offenses[position]);

        return view;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();  // Notify the adapter to update the view
    }
}
