package com.example.jewcol;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class PrefActivity extends PreferenceActivity {
    private ListPreference mListPreference;
    private EditTextPreference mTextPreference;
    private EditTextPreference mTitlePreference;
    private CheckBoxPreference mUpdateDBPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mListPreference = (ListPreference)getPreferenceScreen().findPreference("listPref");

        mTextPreference = (EditTextPreference)getPreferenceScreen().findPreference("hostAddressPref");
        mTextPreference.setSummary(mTextPreference.getText());

        mTitlePreference = (EditTextPreference)getPreferenceScreen().findPreference("titlePref");
        mTitlePreference.setSummary(mTitlePreference.getText());

        mUpdateDBPreference = (CheckBoxPreference)getPreferenceScreen().findPreference("updateDBPref");
    }

    @Override
    protected void onResume() {
        super.onResume();

        mListPreference.setSummary(mListPreference.getEntry().toString());

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(prefListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(prefListener);
    }

    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (s.equals("listPref")) {
                mListPreference.setSummary(mListPreference.getEntry().toString());
            }
            else if (s.equals("hostAddressPref")) {
                mTextPreference.setSummary(mTextPreference.getText());
            }
            else if (s.equals("titlePref")) {
                mTitlePreference.setSummary(mTitlePreference.getText());
            }
        }
    };
}
