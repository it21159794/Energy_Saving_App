package com.example.energysavingappnew

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MonthBillPredictor : AppCompatActivity() {
    private lateinit var editTextLastMonth: EditText
    private lateinit var editTextThisMonth: EditText
    private lateinit var buttonCalculate: Button
    private lateinit var textViewNextMonth: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_month_bill_predicter)

        // Get references to UI elements
        editTextLastMonth = findViewById(R.id.editTextLastMonth)
        editTextThisMonth = findViewById(R.id.editTextThisMonth)
        buttonCalculate = findViewById(R.id.buttonCalculate)
        textViewNextMonth = findViewById(R.id.textViewNextMonth)

        // Set button click listener
        buttonCalculate.setOnClickListener {
            // Get the bill amounts entered by the user
            val lastMonthBill = editTextLastMonth.text.toString().toDoubleOrNull()
            val thisMonthBill = editTextThisMonth.text.toString().toDoubleOrNull()

            // Validate input values
            if (lastMonthBill == null || thisMonthBill == null) {
                textViewNextMonth.text = "Please enter valid bill amounts."
            } else {
                // Calculate predicted bill amount
                val predictedBill = predictNextMonthBill(thisMonthBill, lastMonthBill)
                textViewNextMonth.setText(getString(R.string.predicted_bill, predictedBill))
            }
        }
    }

    private fun predictNextMonthBill(thisMonthBill: Double, lastMonthBill: Double): Double {
        // Calculate the difference between this month's bill and last month's bill
        val diff = thisMonthBill - lastMonthBill

        // Calculate the predicted bill based on the difference and add it to this month's bill
        val predictedBill = thisMonthBill + diff

        // Return the predicted bill
        return predictedBill
    }
}

