package com.example.jewcol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import static com.example.jewcol.MainActivity.db;

public class InfoFragmentActivity extends AppCompatActivity {
    static final String PAGE_NUMBER_ARGUMENT = "page_number_argument";
    static Cursor c;

    public static String[] allDts;

    ViewPager viewPager;
    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_fragment);

        c = db.getDistinctDt();
        if (c.getCount() > 0) {
            allDts = new String[c.getCount()];
            c.moveToFirst();
            int i = 0;
            int dtColIndex = c.getColumnIndex("dt");
            allDts[i] = c.getString(dtColIndex).substring(0, 8);
            while (c.moveToNext()) {
                i++;
                allDts[i] = c.getString(dtColIndex).substring(0, 8);
            }
        }

        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(c.getCount() - 1);
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                invalidateOptionsMenu(position);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        setTitle(preferences.getString("titlePref", "Can't read!"));
    }

//    private void invalidateOptionsMenu(int position) {
//        for (int i = 0; i < pagerAdapter.getCount(); i++) {
//            Fragment fragment = ((MyFragmentPagerAdapter)pagerAdapter).getItem(position);
//            fragment.setHasOptionsMenu(i == position);
//            if (position % 2 == 0)
//                fragment.setHasOptionsMenu(false);
//        }
//        invalidateOptionsMenu();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem mi = menu.add(0, 1, 0, "Отчеты");
        mi.setIntent(new Intent(this, ReportActivity.class));
        mi.setIcon(android.R.drawable.ic_menu_view);
        mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return super.onCreateOptionsMenu(menu);
    }

    private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            PageFragment pageFragment = new PageFragment();
            Bundle arguments = new Bundle();
            arguments.putInt(PAGE_NUMBER_ARGUMENT, position);
            pageFragment.setArguments(arguments);
            return pageFragment;
        }

        @Override
        public int getCount() {
            return c.getCount();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String allDtsposition = allDts[position];
            allDtsposition = allDtsposition.substring(6, 8) + "." + allDtsposition.substring(4, 6) + "." + allDtsposition.substring(2, 4);
            return allDtsposition;
        }
    }
}
