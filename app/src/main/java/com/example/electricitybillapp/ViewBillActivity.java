package com.example.electricitybillapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewBillActivity extends AppCompatActivity {

    // to move through the data returned from a database query
    protected Cursor cursor;
    //to access the SQLite database using a custom helper class
    DataHelper dbHelper;

    //declare variables
    TextView textMonth, textkWh, textTotal, textRebate, textFinal;
    Button button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_bill);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DataHelper(this);
        textMonth = findViewById(R.id.textMonth);
        textkWh = findViewById(R.id.textkWh);
        textTotal = findViewById(R.id.textTotal);
        textRebate = findViewById(R.id.textRebate);
        textFinal = findViewById(R.id.textFinal);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM bills WHERE month = '" + getIntent().getStringExtra("month") +"'", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            cursor.moveToPosition(0);
            textMonth.setText(cursor.getString(1));
            textkWh.setText(cursor.getString(2));

            // Format total cost and final charge to 2 decimal places
            double totalCost = cursor.getDouble(3);
            double rebatePercentage = cursor.getDouble(4);
            double finalCharge = cursor.getDouble(5);

            textTotal.setText(String.format("%.2f", totalCost));
            textRebate.setText(String.format("%.0f%%", rebatePercentage));
            textFinal.setText(String.format("%.2f", finalCharge));
        }

        button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
    }
}