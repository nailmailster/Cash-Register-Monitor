package com.example.jewcol;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.jewcol.DB.DB_NAME;
import static com.example.jewcol.DB.DB_VERSION;
import static com.example.jewcol.MainActivity.LOG_TAG;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS receipts ("
                        + "dt TEXT PRIMARY KEY,"
                        + "docnum TEXT,"
                        + "placeid TEXT,"
                        + "sum DECIMAL(10, 2),"
                        + "cash DECIMAL(10, 2),"
                        + "card DECIMAL(10, 2),"
                        + "discount INTEGER,"
                        + "credit INTEGER,"
                        + "positions INTEGER"
                        + ");"
        );

        sqLiteDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS items ("
                        + "dt TEXT,"
                        + "sku TEXT,"
                        + "ean TEXT,"
                        + "description TEXT,"
                        + "quantity INTEGER,"
                        + "sum DECIMAL(10, 2)"
                        + ");"
        );

        sqLiteDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS newReceipts ("
                        + "dt TEXT PRIMARY KEY,"
                        + "docnum TEXT,"
                        + "placeid TEXT,"
                        + "sum DECIMAL(10, 2),"
                        + "cash DECIMAL(10, 2),"
                        + "card DECIMAL(10, 2),"
                        + "discount INTEGER,"
                        + "credit INTEGER,"
                        + "positions INTEGER"
                        + ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(LOG_TAG, "current version = " + i);
        if (i1 > i) {
            //sqLiteDatabase.execSQL(
            //        "DROP TABLE IF EXISTS mytable"
            //);
            sqLiteDatabase.execSQL(
                    "DROP TABLE IF EXISTS receipts"
            );
            sqLiteDatabase.execSQL(
                    "DROP TABLE IF EXISTS items"
            );
            sqLiteDatabase.execSQL(
                    "DROP TABLE IF EXISTS newReceipts"
            );

            onCreate(sqLiteDatabase);
        }
    }
}
