package com.example.electricitybillapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.electricitybillapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    String[] electricityBill;
    ListView ListViewBills;
    Menu menu;
    //access Database
    protected Cursor cursor;
    //use polymorphism to access DataHelper
    DataHelper dbcenter;
    //to access class CreateBill,and ViewBill
    public static MainActivity ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        //sets up a click listener for the FloatingActionButton Add
        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateBillActivity.class);
                startActivity(intent);
            }
        });

        //sets up a click listener for the FloatingActionButton Info About
        binding.fabAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        ma = this;
        dbcenter = new DataHelper(this);
        RefreshList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void RefreshList() {
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM bills", null);
        electricityBill = new String[cursor.getCount()];
        cursor.moveToFirst();
        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);
            electricityBill[cc] = cursor.getString(1).toString();
        }

        //connected to the ListView in the layout using its ID.
        ListViewBills = (ListView) findViewById(R.id.listViewBills);
        //display the month values in the ListView.
        ListViewBills.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, electricityBill));
        ListViewBills.setSelected(true);

        //Create OnItemClickListener for open the ViewBillActivity
        ListViewBills.setOnItemClickListener((parent, view, position, id) -> {
            cursor.moveToPosition(position);
            String selectedMonth = cursor.getString(cursor.getColumnIndexOrThrow("month"));

            Intent intent = new Intent(MainActivity.this, ViewBillActivity.class);
            intent.putExtra("month", selectedMonth);
            startActivity(intent);
        });

        //refreshes the list to show any updated data.
        ((ArrayAdapter) ListViewBills.getAdapter()).notifyDataSetInvalidated();
    }
}