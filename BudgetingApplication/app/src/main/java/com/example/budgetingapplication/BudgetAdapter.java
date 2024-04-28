package com.example.budgetingapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class BudgetAdapter extends ArrayAdapter<String> {

    public BudgetAdapter(Context context, List<String> budgets) {
        super(context, 0, budgets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String budgetDetails = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_budget, parent, false);
        }

        TextView textViewBudgetTitle = convertView.findViewById(R.id.textViewBudgetTitle);
        TextView textViewTotalSalary = convertView.findViewById(R.id.textViewTotalSalary);
        TextView textViewRemainingAmount = convertView.findViewById(R.id.textViewRemainingAmount);

        // Split the budget details string
        String[] parts = budgetDetails.split(", ");

        // Set the title and summary text views accordingly
        if (parts.length >= 4) { // Ensure at least 4 elements exist (month, year, total salary, remaining amount)
            textViewBudgetTitle.setText(parts[0] + " " + parts[1]);

            // Set the total salary and remaining amount dynamically
            textViewTotalSalary.setText("Total Salary: $" + parts[2]);

            double remainingAmount = Double.parseDouble(parts[3]);
            textViewRemainingAmount.setText("Remaining Amount: $" + String.format("%.2f", remainingAmount));

            // Check if savings value is present
            if (parts.length >= 5) {
                try {
                    double savings = Double.parseDouble(parts[4]);
                    // You can handle the savings value as needed
                } catch (NumberFormatException e) {
                    // Handle the case where savings value is not a valid double
                    e.printStackTrace();
                }
            }
        }

        //Set OnClickListener for each budget item
        return convertView;
    }


}