package com.example.electricitybillapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreateBillActivity extends AppCompatActivity {

    protected Cursor cursor;
    DataHelper dbHelper;
    TextView textView, textView1, textView2, textView3, textView4;
    EditText editText;
    RadioGroup radioGroup;
    Button button, button1, button2;
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> arrayAdapter;
    String[] month={"January","February","March","April","May","June","July","August","September",
            "October","November","December"};

    double totalCharges = 0;
    double finalCost = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_bill);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textView = findViewById(R.id.textView);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        editText = findViewById(R.id.editText);
        radioGroup = findViewById(R.id.radioGroup);
        button = findViewById(R.id.button);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);

        dbHelper = new DataHelper(this);
        button.setOnClickListener(v -> calculateCharges());
        button1.setOnClickListener(v -> saveBill());
        button2.setOnClickListener(v -> finish());

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, month);
        autoCompleteTextView.setAdapter(arrayAdapter);
    }

    private void calculateCharges() {
        String kwhText = editText.getText().toString();
        int selectedRebateId = radioGroup.getCheckedRadioButtonId();
        if (selectedRebateId == -1) {
            Toast.makeText(this, "Please select a rebate percentage", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRebate = findViewById(selectedRebateId);
        String rebateText = selectedRebate.getText().toString().replace("%", "");


        if (kwhText.isEmpty() || rebateText.isEmpty()) {
            Toast.makeText(this, "Please fill in both kWh and rebate fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double kwh = Double.parseDouble(kwhText);
        double rebate = Double.parseDouble(rebateText);

        if (rebate < 0 || rebate > 5) {
            Toast.makeText(this, "Rebate must be between 0% and 5%", Toast.LENGTH_SHORT).show();
            return;
        }

        totalCharges = calculateTotalCharges(kwh);
        finalCost = totalCharges - (totalCharges * rebate / 100);

        textView3.setText(String.format("Total Charges: RM %.2f", totalCharges));
        textView4.setText(String.format("Final Cost: RM %.2f", finalCost));
    }
    private void saveBill() {
        if (totalCharges == 0 || finalCost == 0) {
            Toast.makeText(this, "Please calculate before saving!", Toast.LENGTH_SHORT).show();
            return;
        }

        String month = autoCompleteTextView.getText().toString();
        double kwh = Double.parseDouble(editText.getText().toString());
        int selectedRebateId = radioGroup.getCheckedRadioButtonId();
        if (selectedRebateId == -1) {
            Toast.makeText(this, "Please select a rebate percentage", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRebate = findViewById(selectedRebateId);
        double rebate = Double.parseDouble(selectedRebate.getText().toString().replace("%", ""));


        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("month", month);
        values.put("kwh_used", kwh);
        values.put("total_charges", totalCharges);
        values.put("rebate_percent", rebate);
        values.put("final_cost", finalCost);

        long result = db.insert("bills", null, values);

        if (result != -1) {
            Toast.makeText(this, "Bill saved successfully!", Toast.LENGTH_SHORT).show();
            MainActivity.ma.RefreshList();
            finish(); // back to main screen
        } else {
            Toast.makeText(this, "Error saving bill.", Toast.LENGTH_SHORT).show();
        }
    }

    private double calculateTotalCharges(double kwh) {
        double total = 0;

        if (kwh <= 200) {
            total = kwh * 0.218;
        } else if (kwh <= 300) {
            total = (200 * 0.218) + ((kwh - 200) * 0.334);
        } else if (kwh <= 600) {
            total = (200 * 0.218) + (100 * 0.334) + ((kwh - 300) * 0.516);
        } else if (kwh <= 900) {
            total = (200 * 0.218) + (100 * 0.334) + (300 * 0.516) + ((kwh - 600) * 0.546);
        } else {
            total = (200 * 0.218) + (100 * 0.334) + (300 * 0.516) + (300 * 0.546) + ((kwh - 900) * 0.546);
        }

        return total;
    }
}