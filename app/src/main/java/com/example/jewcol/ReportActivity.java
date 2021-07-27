package com.example.jewcol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        setTitle(preferences.getString("titlePref", "Can't read!"));
    }
}
