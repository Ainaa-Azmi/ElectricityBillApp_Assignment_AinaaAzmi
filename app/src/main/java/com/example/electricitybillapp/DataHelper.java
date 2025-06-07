package com.example.electricitybillapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper extends SQLiteOpenHelper{

    //Database Name
    private static final String DATABASE_NAME = "electricitybills.db";

    //Database Version
    private static final int DATABASE_VERSION = 1;

    //Create Constructor for Data Helper
    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table bills(id integer primary key autoincrement, " +
                "month text, kwh_used real, total_charges real, rebate_percent real, final_cost real);";
        Log.d("Data","onCreate: "+sql);
        db.execSQL(sql);
    }

    //create method to upgrade database version if database exist
    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2){

    }
}
