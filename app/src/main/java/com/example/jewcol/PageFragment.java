package com.example.jewcol;

import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.v4.app.ActivityCompat.invalidateOptionsMenu;
import static com.example.jewcol.InfoFragmentActivity.allDts;
import static com.example.jewcol.MainActivity.LOG_TAG;
import static com.example.jewcol.MainActivity.db;

public class PageFragment extends Fragment implements View.OnClickListener {
    static final String PAGE_NUMBER_ARGUMENT = "page_number_argument";

    int pageNumber;

    float totalSum1, totalSum2, totalSum;

    String[] selectionArgs;
    Cursor c;

    LayoutInflater ltInflater;
    LinearLayout linLayout;

    //region переменные даты
    Calendar todayCalendar;
    Calendar activeCalendar;
    SimpleDateFormat dateFormatForSorting = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat df2 = new SimpleDateFormat("dd.MM.yy");
    String formattedDateForTvDate;
    String formattedDate;
    //endregion

    //region переменные таймера
    public Timer timer;
    public PageFragmentTimerTask pageFragmentTimerTask;
    public boolean timerIsPaused;
    //endregion

    //region объявление переменных-ссылок на view-элементы
    TextView tvTotalSum1;
    TextView tvTotalSum2;
    TextView tvTotalSum;

    TextView tvDocnum;
    TextView tvDt;
    //endregion

    //region объявление переменных для диалога
    String documentDate, documentTime;
    String[] res;
    String dialogTitle;
    //endregion

    //region
    int dtColIndex;
    int docnumColIndex;
    int placeIdColIndex;
    int sumColIndex;
    int cashColIndex;
    int cardColIndex;
    int discountColIndex;
    int creditColIndex;
    int positionsColIndex;

    View item;

    View vSeparator;
//    TextView tvDt;
//    TextView tvDocnum;
    TextView tvDiscount;
    TextView tvPositions;
    TextView tvNal;
    TextView tvBeznal;
    TextView tvCash;
    TextView tvCard;
    TextView tvSum;

    public PageFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
//        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(PAGE_NUMBER_ARGUMENT);
//        if (db == null)
//            db = new DB(this);
//        db.open();
//
//        db.deleteNewReceipts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFragmentPage = inflater.inflate(R.layout.fragment_page, null);

        linLayout = viewFragmentPage.findViewById(R.id.linLayout);
        ltInflater = getLayoutInflater();

        //region присваивание значений переменным-ссылкам на view-элементы
        tvTotalSum1 = viewFragmentPage.findViewById(R.id.tvTotalSum1);
        tvTotalSum2 = viewFragmentPage.findViewById(R.id.tvTotalSum2);
        tvTotalSum = viewFragmentPage.findViewById(R.id.tvTotalSum);
        //endregion

        todayCalendar = Calendar.getInstance();
        activeCalendar = Calendar.getInstance();

        fillList("onCreate");

        if (pageNumber == allDts.length - 1) {
            if (timer != null)
                timer.cancel();
            timer = new Timer();
            pageFragmentTimerTask = new PageFragmentTimerTask();
            timer.schedule(pageFragmentTimerTask, 1000, 1000);
        }

        return viewFragmentPage;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (pageNumber == allDts.length - 1) {
            if (pageFragmentTimerTask != null)
                pageFragmentTimerTask.cancel();
            pageFragmentTimerTask = null;
            if (timer != null)
                timer.cancel();
            timer = null;
            if (db != null)
                db.deleteNewReceipts();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.btnPrev:
//                fillList("");
//                break;
//            case R.id.btnNext:
//                break;
            case R.id.frame:
                tvDocnum = view.findViewById(R.id.tvDocnum);
                documentDate = allDts[pageNumber];
                tvDt = view.findViewById(R.id.tvDt);
                documentTime = tvDt.getText().toString();
                documentTime = documentTime.substring(0, 2) + documentTime.substring(3, 5) + documentTime.substring(6, 8);
                res = db.getItemsOfReceiptByDocnumAndDtAsArray(tvDocnum.getText().toString(), documentDate + documentTime);
                dialogTitle = tvDocnum.getText().toString();
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setTitle("Состав чека " + dialogTitle);
                adb.setItems(res, null);
                AlertDialog alertDialog = adb.create();
                alertDialog.show();
                break;
        }
    }

    public void fillListFromCursor(Cursor c) {
        float cash, card, sum;

        if (c.moveToFirst()) {
            dtColIndex = c.getColumnIndex("dt");
            docnumColIndex = c.getColumnIndex("docnum");
            placeIdColIndex = c.getColumnIndex("placeid");
            sumColIndex = c.getColumnIndex("sum");
            cashColIndex = c.getColumnIndex("cash");
            cardColIndex = c.getColumnIndex("card");
            discountColIndex = c.getColumnIndex("discount");
            creditColIndex = c.getColumnIndex("credit");
            positionsColIndex = c.getColumnIndex("positions");

            do {
                totalSum += c.getFloat(sumColIndex);
                String dt = c.getString(dtColIndex);
                dt = dt.substring(dt.length() - 6, dt.length() - 4) + ":" + dt.substring(dt.length() - 4, dt.length() - 2) + ":" + dt.substring(dt.length() - 2, dt.length());
                //dt = dt.substring(dt.length() - 6, dt.length() - 4) + ":" + dt.substring(dt.length() - 4, dt.length() - 2);
                String docnum = c.getString(docnumColIndex);
                String placeid = c.getString(placeIdColIndex);
                int discount = c.getInt(discountColIndex);

                item = ltInflater.inflate(R.layout.item, linLayout, false);
                vSeparator = item.findViewById(R.id.vSeparator);

                if (placeid.equals("1")) {
                    totalSum1 += c.getFloat(sumColIndex);
                }
                else {
                    totalSum2 += c.getFloat(sumColIndex);
                    item.setBackgroundColor(Color.parseColor("#f1eaa6"));
                    vSeparator.setBackgroundColor(Color.WHITE);
                }

                tvDt = item.findViewById(R.id.tvDt);
                tvDt.setText(dt);

                tvDocnum = item.findViewById(R.id.tvDocnum);
                tvDocnum.setText(docnum);

                tvDiscount = item.findViewById(R.id.tvDiscount);
                if (discount > 0)
                    tvDiscount.setText("-" + String.format("%d", discount) + "%");
                else
                    tvDiscount.setText("");

                tvPositions = item.findViewById(R.id.tvPositions);
                tvPositions.setText(c.getString(positionsColIndex));

                cash = c.getFloat(cashColIndex);
                card = c.getFloat(cardColIndex);
                sum = c.getFloat(sumColIndex);

                tvNal = item.findViewById(R.id.tvNal);
                tvBeznal = item.findViewById(R.id.tvBeznal);
                tvCash = item.findViewById(R.id.tvCash);
                tvCard = item.findViewById(R.id.tvCard);

                if (cash != sum) {
                    tvNal.setText("НАЛ:");
                    tvBeznal.setText("Б/Н:");
                    tvCash.setText(String.format("%,.0f", cash));
                    tvCard.setText(String.format("%,.0f", card));
                }
                else {
                    tvNal.setText("");
                    tvBeznal.setText("");
                    tvCash.setText("");
                    tvCard.setText("");
                }

                tvSum = item.findViewById(R.id.tvSum);
                tvSum.setText(String.format("%,.0f", sum));

                item.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                item.setOnClickListener(this);

                linLayout.addView(item);
            } while (c.moveToNext());
        }
        else
            Log.d(LOG_TAG, "Empty");
    }

    public void fillList(String action) {
        totalSum1 = 0;
        totalSum2 = 0;
        totalSum = 0;

        formattedDate = dateFormatForSorting.format(activeCalendar.getTime());
        formattedDateForTvDate = df2.format(activeCalendar.getTime());

        selectionArgs = new String[] { allDts[pageNumber], allDts[pageNumber] + "235959" };

        c = db.getReceiptsForDay(selectionArgs);
        linLayout.removeAllViews();
        fillListFromCursor(c);

        tvTotalSum1.setText(String.format("%,.0f", totalSum1));
//        tvTotalSum1.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "orbitron.ttf"));
        tvTotalSum2.setText(String.format("%,.0f", totalSum2));
//        tvTotalSum2.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "orbitron.ttf"));
        tvTotalSum.setText(String.format("%,.0f", totalSum));
//        tvTotalSum.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "orbitron.ttf"), Typeface.BOLD);
    }

    class PageFragmentTimerTask extends TimerTask {
        @Override
        public void run() {
            if (pageNumber != allDts.length - 1)
                return;
            if (timerIsPaused)
                return;
            c = db.getNewReceipts();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (pageNumber != allDts.length - 1)
                        return;
                    if (c.getCount() > 0) {
                        if (df2.format(activeCalendar.getTime()).equals(df2.format(todayCalendar.getTime())))
                            fillList("");
                    }
                }
            });
        }
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
////        inflater.inflate(R.menu.main, menu);
////        setHasOptionsMenu(isVisible());
////        getActivity().invalidateOptionsMenu();
//        super.onCreateOptionsMenu(menu, inflater);
//    }

//    @Override
//    public void onResume() {
//        super.onResume();
//    }
}
