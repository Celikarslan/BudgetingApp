package com.example.budgetingapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class ExpenseAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private ArrayList<String> mExpenses;

    public ExpenseAdapter(Context context, ArrayList<String> expenses) {
        super(context, 0, expenses);
        mContext = context;
        mExpenses = expenses;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_expense, parent, false);
        }

        String expense = mExpenses.get(position);

        TextView textViewName = listItem.findViewById(R.id.textViewName);
        TextView textViewCategory = listItem.findViewById(R.id.textViewCategory);
        TextView textViewAmount = listItem.findViewById(R.id.textViewAmount);
        TextView textViewRecurring = listItem.findViewById(R.id.textViewRecurring);

        String[] parts = expense.split(",");
        String name = parts[0];
        String category = parts[1];
        String amount = parts[2];
        boolean isRecurring = Boolean.parseBoolean(parts[3]); // Parse the boolean value

        textViewName.setText("Name: " + name);
        textViewCategory.setText("Category: " + category);
        textViewAmount.setText("Amount: $" + amount);

        // Set the recurring text based on the isRecurring value
        if (isRecurring) {
            textViewRecurring.setText("Recurring: Yes");
        } else {
            textViewRecurring.setText("Recurring: No");
        }

        return listItem;
    }
}
