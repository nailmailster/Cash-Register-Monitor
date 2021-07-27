package com.example.jewcol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        setTitle(preferences.getString("titlePref", "Can't read!"));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvPromo2:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel","+79196382773", null));
                startActivity(intent);
                break;
        }
    }
}
