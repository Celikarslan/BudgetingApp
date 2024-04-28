package com.example.budgetingapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AddBudget extends AppCompatActivity {

    private ToggleButton toggleButtonSalaryHourly, toggleButtonSavings;
    private EditText editTextSalary, editTextSavings, editTextHourlyRate, editTextHours;
    private TextView textViewTotalSalary, textViewRemainingAmount;
    private ListView listViewExpenses;
    private Button buttonAddExpense, buttonClearExpenses, buttonSaveBudget;
    private ArrayList<String> expenses;
    private ActivityResultLauncher<Intent> expenseActivityResultLauncher;


    private double totalSalary = 0, remainingAmount = 0, savingsPerc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);

        toggleButtonSalaryHourly = findViewById(R.id.toggleButtonSalaryHourly);
        toggleButtonSavings = findViewById(R.id.toggleButtonSavings);
        editTextSalary = findViewById(R.id.editTextSalary);
        editTextSavings = findViewById(R.id.editTextSavings);
        textViewTotalSalary = findViewById(R.id.textViewTotalSalary);
        textViewRemainingAmount = findViewById(R.id.textViewRemainingAmount);
        listViewExpenses = findViewById(R.id.listViewExpenses);
        buttonAddExpense = findViewById(R.id.buttonAddExpense);
        editTextHourlyRate = findViewById(R.id.editTextHourlyRate);
        editTextHours = findViewById(R.id.editTextHours);
        buttonClearExpenses = findViewById(R.id.buttonClearExpenses);
        buttonSaveBudget = findViewById(R.id.buttonSaveBudget);

        editTextSalary.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); // Set input type to accept numbers and decimals
        editTextSavings.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editTextHourlyRate.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editTextHours.setInputType(InputType.TYPE_CLASS_NUMBER);


        expenses = new ArrayList<>(); // Initialize the expenses list


        // Retrieve expenses from file using FileManager
        expenses = FileManager.retrieveFromFile("expenses", this);

        // Implement logic for adding budget and expenses
        // Dummy logic: Calculate and display total salary and remaining amount
        textViewTotalSalary.setText("Total Salary: $" + totalSalary);

        // Populate listViewExpenses with expenses
        ExpenseAdapter adapter = new ExpenseAdapter(this, expenses);
        listViewExpenses.setAdapter(adapter);

        // Dummy logic: Calculate and display remaining amount
        textViewRemainingAmount.setText("Remaining Amount: $" + remainingAmount);

        // Use ActivityResultLauncher to start ExpenseActivity
        expenseActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Retrieve expense data from ExpenseActivity
                        Intent data = result.getData();
                        String name = data.getStringExtra("name");
                        String category = data.getStringExtra("category");
                        String amount = data.getStringExtra("amount");
                        boolean isRecurring = data.getBooleanExtra("isRecurring", false); // Retrieve isRecurring with a default value

                        // Add the new expense to the list and update the adapter
                        String newExpense = name + "," + category + "," + amount + "," + isRecurring; // Include isRecurring
                        expenses.add(newExpense);
                        FileManager.saveToFile("expenses", expenses, AddBudget.this);
                        ((ExpenseAdapter) listViewExpenses.getAdapter()).notifyDataSetChanged();

                        // Recalculate remaining amount after expense is added
                        double remainingAmount = calculateRemainingAmount(totalSalary);
                        textViewRemainingAmount.setText("Remaining Amount: $" + String.format("%.2f", remainingAmount));
                        editTextSavings.setText("0.0");
                    }
                });

        buttonAddExpense.setOnClickListener(view -> {
            // Open ExpenseActivity to add a new expense
            Intent intent = new Intent(AddBudget.this, ExpenseActivity.class);
            expenseActivityResultLauncher.launch(intent);
            // Recalculate total salary after adding expense
            totalSalary = calculateTotalSalary();
            textViewTotalSalary.setText("Total Salary: $" + totalSalary);

            // Recalculate remaining amount after adding expense
            double remainingAmount = calculateRemainingAmount(totalSalary);
            textViewRemainingAmount.setText("Remaining Amount: $" + String.format("%.2f", remainingAmount));

            editTextSavings.setText("");
        });

        buttonClearExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearExpenses();
                editTextSavings.setText("0.0");
            }
        });

        toggleButtonSalaryHourly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Toggle visibility of editTextSalary, editTextHours, and editTextHourlyRate based on the toggle button state
            if (isChecked) {
                editTextHours.setVisibility(View.VISIBLE);
                editTextHourlyRate.setVisibility(View.VISIBLE);
                editTextSalary.setVisibility(View.GONE);
            } else {
                editTextSalary.setVisibility(View.VISIBLE);
                editTextHours.setVisibility(View.GONE);
                editTextHourlyRate.setVisibility(View.GONE);
            }

        });

        editTextSalary.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    // Calculate total salary if the input is not empty
                    double totalSalary = calculateTotalSalary();
                    textViewTotalSalary.setText("Total Salary: $" + totalSalary);

                    // Recalculate remaining amount after adding expense
                    double remainingAmount = calculateRemainingAmount(totalSalary);
                    textViewRemainingAmount.setText("Remaining Amount: $" + String.format("%.2f", remainingAmount));
                } else {
                    // Set total salary and remaining amount to 0 if the input is empty
                    totalSalary = 0;
                    remainingAmount = 0;
                    textViewTotalSalary.setText("Total Salary: $" + totalSalary);
                    textViewRemainingAmount.setText("Remaining Amount: $" + String.format("%.2f", remainingAmount));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });

        editTextHourlyRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty() && !editTextHours.getText().toString().isEmpty()) {
                    double totalSalary = calculateTotalSalary();
                    textViewTotalSalary.setText("Total Salary: $" + totalSalary);

                    double remainingAmount = calculateRemainingAmount(totalSalary);
                    textViewRemainingAmount.setText("Remaining Amount: $" + String.format("%.2f", remainingAmount));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });
        editTextHours.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    double totalSalary = calculateTotalSalary();
                    textViewTotalSalary.setText("Total Salary: $" + totalSalary);

                    double remainingAmount = calculateRemainingAmount(totalSalary);
                    textViewRemainingAmount.setText("Remaining Amount: $" + String.format("%.2f", remainingAmount));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });

        editTextSavings.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    double savings = Double.parseDouble(s.toString());
                    double totalSalary = calculateTotalSalary();
                    if(toggleButtonSavings.isChecked()){
                        savingsPerc = calculateRemainingAmount(totalSalary)*(savings/100);
                        remainingAmount = calculateRemainingAmount(totalSalary) - (calculateRemainingAmount(totalSalary)*(savings/100));
                    }else {
                        remainingAmount = calculateRemainingAmount(totalSalary) - savings;
                    }

                    if (remainingAmount < 0) {
                        // If remaining amount is negative, set it to 0
                        remainingAmount = 0;
                    }

                    // Display the remaining amount
                    textViewRemainingAmount.setText("Remaining Amount: $" + String.format("%.2f", remainingAmount));
                }else{
                    textViewRemainingAmount.setText("Remaining Amount: $" + calculateRemainingAmount(calculateTotalSalary()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        buttonSaveBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listViewExpenses != null && listViewExpenses.getAdapter() != null) {
                    saveBudget();
                }
            }
        });


    }
    private void clearExpenses() {
        if (listViewExpenses != null && listViewExpenses.getAdapter() != null) {
            // Clear the expenses list
            expenses.clear();

            // Update the adapter
            ((ExpenseAdapter) listViewExpenses.getAdapter()).notifyDataSetChanged();

            // Recalculate remaining amount
            if (!editTextSalary.getText().toString().isEmpty()) {
                double totalSalary = calculateTotalSalary();
                textViewTotalSalary.setText("Total Salary: $" + totalSalary);

                remainingAmount = calculateRemainingAmount(totalSalary);
                textViewRemainingAmount.setText("Remaining Amount: $" + String.format("%.2f", remainingAmount));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Retrieve expense data from ExpenseActivity
            String name = data.getStringExtra("name");
            String category = data.getStringExtra("category");
            String amount = data.getStringExtra("amount");
            boolean isRecurring = data.getBooleanExtra("isRecurring", false); // Get the recurring status

            // Add the new expense to the list
            String newExpense = name + "," + category + "," + amount + "," + isRecurring; // Include recurring status
            expenses.add(newExpense);
            FileManager.saveToFile("expenses", expenses, this);
            ((ExpenseAdapter) listViewExpenses.getAdapter()).notifyDataSetChanged();

            // Recalculate remaining amount after adding the expense
            double remainingAmount = calculateRemainingAmount(totalSalary);
            textViewRemainingAmount.setText("Remaining Amount: $" + String.format("%.2f", remainingAmount));
        }
    }



    private double calculateTotalSalary() {
        double salary = 0; // Default salary value

        if (toggleButtonSalaryHourly.isChecked()) {
            // Check if both hourly rate and hours fields are not empty
            String hourlyRateText = editTextHourlyRate.getText().toString().trim();
            String hoursText = editTextHours.getText().toString().trim();
            if (!hourlyRateText.isEmpty() && !hoursText.isEmpty()) {
                float hourlyRate = Float.parseFloat(hourlyRateText);
                float hoursWorked = Float.parseFloat(hoursText);
                salary = hourlyRate * hoursWorked; // Assuming 4 weeks in a month
            }
        } else {
            // Check if salary field is not empty
            String salaryText = editTextSalary.getText().toString().trim();
            if (!salaryText.isEmpty()) {
                float total = Float.parseFloat(salaryText);
                salary = total / 12;
            }
        }

        // Format salary with two decimal places
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.parseDouble(decimalFormat.format(salary));
    }

    private double calculateRemainingAmount(double totalSalary) {
        // Dummy logic: Calculate remaining amount after expenses deducted
        remainingAmount = totalSalary;
        for (String expense : expenses) {
            // Split the expense data (name, category, amount)
            String[] parts = expense.split(",");
            double amount = Double.parseDouble(parts[2]);
            remainingAmount -= amount;
        }
        // Format remainingAmount with two decimal places
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.parseDouble(decimalFormat.format(remainingAmount));
    }
    private void saveBudget() {
        // Create an AlertDialog.Builder instance
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Month and Year");

        // Set up the input
        final EditText inputMonth = new EditText(this);
        final EditText inputYear = new EditText(this);
        inputMonth.setInputType(InputType.TYPE_CLASS_TEXT);
        inputMonth.setHint("Month");
        inputYear.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputYear.setHint("Year");

        // Add the EditText fields to the dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputMonth);
        layout.addView(inputYear);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String month = inputMonth.getText().toString();
                String year = inputYear.getText().toString();
                double savings;
                if(toggleButtonSavings.isChecked()){
                    savings = savingsPerc;
                }else{
                    savings = Double.parseDouble(editTextSavings.getText().toString());
                }


                // Format the budget details string
                StringBuilder budgetDetailsBuilder = new StringBuilder();
                budgetDetailsBuilder.append(month).append(", ").append(year).append(", ").append(calculateTotalSalary()).append(", ").append(remainingAmount).append(", ").append(savings).append("; "); // Include savings

                // Append all expenses to the budget details string
                for (String expense : expenses) {
                    budgetDetailsBuilder.append(expense).append("; ");
                }

                // Filter out non-recurring expenses
                ArrayList<String> recurringExpenses = new ArrayList<>();
                for (String expense : expenses) {
                    String[] parts = expense.split(",");
                    if (parts[3].equals("true")) {
                        recurringExpenses.add(expense);
                    }
                }

                // Save the filtered recurring expenses
                FileManager.saveToFile("expenses", recurringExpenses, AddBudget.this);


                // Pass the budget details back to the HomeActivity
                Intent intent = new Intent();
                intent.putExtra("budgetDetails", budgetDetailsBuilder.toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Show the dialog
        builder.show();
    }

}