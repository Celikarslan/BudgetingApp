package com.example.budgetingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ExpenseActivity extends AppCompatActivity {

    private EditText editTextName, editTextCategory, editTextAmount;
    private Button buttonAddExpense;
    private CheckBox checkboxRecurring; // Add this line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        editTextName = findViewById(R.id.editTextName);
        editTextCategory = findViewById(R.id.editTextCategory);
        editTextAmount = findViewById(R.id.editTextAmount);
        buttonAddExpense = findViewById(R.id.buttonAddExpense);
        checkboxRecurring = findViewById(R.id.checkboxRecurring); // Initialize checkbox

        buttonAddExpense.setOnClickListener(v -> {
            // Get input values
            String name = editTextName.getText().toString();
            String category = editTextCategory.getText().toString();
            String amount = editTextAmount.getText().toString();
            boolean isRecurring = checkboxRecurring.isChecked(); // Get checkbox state

            // Prepare intent to pass back expense data to AddBudgetActivity
            Intent intent = new Intent();
            intent.putExtra("name", name);
            intent.putExtra("category", category);
            intent.putExtra("amount", amount);
            intent.putExtra("isRecurring", isRecurring); // Pass checkbox state back to AddBudget activity

            setResult(RESULT_OK, intent);
            finish(); // Close ExpenseActivity and return to AddBudgetActivity
        });
    }
}
