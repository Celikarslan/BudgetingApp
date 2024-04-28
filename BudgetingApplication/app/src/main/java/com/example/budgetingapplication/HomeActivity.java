package com.example.budgetingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ListView listViewBudgets;
    private Button buttonNewBudget, buttonClearBudgets;
    private TextView textViewWagesTotal, textViewExpensesTotal, textViewSavingsTotal, textViewRemainingTotal;

    private ArrayList<String> pastBudgets;
    private static final int ADD_BUDGET_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        listViewBudgets = findViewById(R.id.listViewBudgets);
        buttonNewBudget = findViewById(R.id.buttonNewBudget);
        buttonClearBudgets = findViewById(R.id.buttonClearBudgets);
        textViewWagesTotal = findViewById(R.id.textViewWagesTotal);
        textViewExpensesTotal = findViewById(R.id.textViewExpensesTotal);
        textViewSavingsTotal = findViewById(R.id.textViewSavingsTotal);
        textViewRemainingTotal = findViewById(R.id.textViewRemainingTotal);

        pastBudgets = new ArrayList<>(); // Initialize pastBudgets

        // Retrieve past budgets from file using FileManager
        pastBudgets = FileManager.retrieveFromFile("past_budgets", this);

        // Populate listViewBudgets with past budgets
        BudgetAdapter adapter = new BudgetAdapter(this, pastBudgets);
        listViewBudgets.setAdapter(adapter);

        updateRunningTotals();

        // Set click listener for budget items
        listViewBudgets.setOnItemClickListener((parent, view, position, id) -> {
            String budgetDetails = pastBudgets.get(position);

            // Split the budgetDetails string
            String[] parts = budgetDetails.split(";");
            if (parts.length >= 2) {
                String summaryDetails = parts[0].trim(); // Get the summary details

                // Parse the summary details
                String[] summaryParts = summaryDetails.split(",");
                if (summaryParts.length >= 5) {
                    double totalSalary = Double.parseDouble(summaryParts[2].trim());
                    double remaining = Double.parseDouble(summaryParts[3].trim());
                    double savings = Double.parseDouble(summaryParts[4].trim());

                    // Calculate expenses
                    double expenses = totalSalary - remaining - savings;

                    // Concatenate all parts after the first one to get the expenses details
                    StringBuilder expensesDetailsBuilder = new StringBuilder();
                    for (int i = 1; i < parts.length; i++) {
                        expensesDetailsBuilder.append(parts[i].trim());
                        if (i < parts.length - 1) {
                            expensesDetailsBuilder.append(";"); // Add separator between expenses
                        }
                    }
                    String expensesDetails = expensesDetailsBuilder.toString();

                    // Split the expensesDetails string to extract each individual expense
                    String[] expensesArray = expensesDetails.split(";");

                    // Create an ArrayList to hold the expenses details
                    ArrayList<String> expensesList = new ArrayList<>();

                    // Loop through each expense and create a new expense using the details
                    for (String expense : expensesArray) {
                        String[] expenseDetails = expense.split(",");
                        if (expenseDetails.length >= 4) {
                            // Retrieve expense details
                            String name = expenseDetails[0];
                            String category = expenseDetails[1];
                            String amount = expenseDetails[2];
                            boolean isRecurring = Boolean.parseBoolean(expenseDetails[3]);

                            // Create a new expense string using the provided details
                            String newExpense = name + "," + category + "," + amount + "," + isRecurring;

                            // Add the new expense to the expenses list
                            expensesList.add(newExpense);
                        }
                    }

                    // Launch BudgetSummaryActivity to display the budget summary
                    Intent intent = new Intent(HomeActivity.this, BudgetSummaryActivity.class);
                    intent.putExtra("monthYear", summaryParts[0] + ", " + summaryParts[1]);
                    intent.putExtra("totalSalary", totalSalary);
                    intent.putExtra("expenses", expenses);
                    intent.putExtra("savings", savings);

                    // Pass the list of expenses to BudgetSummaryActivity
                    intent.putExtra("expensesDetails", expensesList);

                    startActivity(intent);
                    finish();
                }
            }
        });

        buttonNewBudget.setOnClickListener(v -> {
            // Navigate to AddBudgetActivity
            startActivityForResult(new Intent(HomeActivity.this, AddBudget.class), ADD_BUDGET_REQUEST_CODE);
        });
        buttonClearBudgets.setOnClickListener(v -> {
            // Clear all budgets
            pastBudgets.clear();

            // Update the list view
            ((BudgetAdapter) listViewBudgets.getAdapter()).notifyDataSetChanged();

            // Save the updated list of past budgets
            FileManager.saveToFile("past_budgets", pastBudgets, HomeActivity.this);
            updateRunningTotals();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_BUDGET_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Retrieve budget details from AddBudgetActivity
            String budgetDetails = data.getStringExtra("budgetDetails");

            // Add the new budget details to the list of past budgets
            pastBudgets.add(budgetDetails);

            // Update the list view
            ((BudgetAdapter) listViewBudgets.getAdapter()).notifyDataSetChanged();

            // Save the updated list of past budgets
            FileManager.saveToFile("past_budgets", pastBudgets, this);

            updateRunningTotals();
        }
    }
    private void updateRunningTotals() {
        double totalWages = 0;
        double totalExpenses = 0;
        double totalSavings = 0;
        double totalRemaining = 0;

        for (String budget : pastBudgets) {
            String[] parts = budget.split(";");
            if (parts.length >= 2) {
                String summaryDetails = parts[0].trim();
                String[] summaryParts = summaryDetails.split(",");

                if (summaryParts.length >= 5) {
                    double wages = Double.parseDouble(summaryParts[2].trim());
                    double remaining = Double.parseDouble(summaryParts[3].trim());
                    double savings = Double.parseDouble(summaryParts[4].trim());

                    // Calculate expenses (total wages - remaining - savings)
                    totalExpenses += wages - remaining - savings;

                    // Add wages and savings to their respective totals
                    totalWages += wages;
                    totalSavings += savings;
                    totalRemaining += remaining;
                }
            }
        }

        // Update TextViews with running totals
        textViewWagesTotal.setText("Total Wages: " + String.format("%.2f", totalWages));
        textViewExpensesTotal.setText("Total Expenses: " + String.format("%.2f", totalExpenses));
        textViewSavingsTotal.setText("Total Savings: " + String.format("%.2f", totalSavings));
        textViewRemainingTotal.setText("Total Remaining: " + String.format("%.2f", totalRemaining));
    }
}
