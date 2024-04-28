package com.example.budgetingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BudgetSummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_summary);

        TextView textViewMonthYear = findViewById(R.id.textViewMonthYear);
        TextView textViewWages = findViewById(R.id.textViewWages);
        TextView textViewExpenses = findViewById(R.id.textViewExpenses);
        TextView textViewSavings = findViewById(R.id.textViewSavings);
        ListView listViewExpenses = findViewById(R.id.listViewExpenses);
        Button backButton = findViewById(R.id.buttonBack); // Initialize the back button

        // Set OnClickListener for the back button
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(BudgetSummaryActivity.this, HomeActivity.class));
            finish();
        });

        // Retrieve the budget details from the intent
        String monthYear = getIntent().getStringExtra("monthYear");
        double wages = getIntent().getDoubleExtra("totalSalary", 0.0);
        double expenses = getIntent().getDoubleExtra("expenses",0.0);
        double savings = getIntent().getDoubleExtra("savings", 0.0);

        // Display the budget summary
        textViewMonthYear.setText(monthYear);
        textViewWages.setText(String.format("Wages: $%.2f", wages));
        textViewExpenses.setText(String.format("Expenses: $%.2f", expenses));
        textViewSavings.setText(String.format("Savings: $%.2f", savings));

        // Retrieve and set the list of expenses
        ArrayList<String> expensesList = getIntent().getStringArrayListExtra("expensesDetails");
        if (expensesList != null) {
            // Create an adapter for the expenses list
            ExpenseAdapter adapter = new ExpenseAdapter(this, expensesList);
            listViewExpenses.setAdapter(adapter);
        } else {
            // Handle the case where the list is null
            Log.e("BudgetSummaryActivity", "Expense list is null");
            // You may want to show a message or take appropriate action here
        }
    }
}
