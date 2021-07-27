package com.example.jewcol;

import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.jewcol.MainActivity.LOG_TAG;
import static com.example.jewcol.MainActivity.db;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {
    float totalSum1, totalSum2, totalSum;

    public Timer timer;
    public MyInfoActivityTimerTask myInfoActivityTimerTask;
    public boolean timerIsPaused;

    Button btnPrev;
    TextView tvDate;
    Button btnNext;

    TextView tvTotalSum1;
    TextView tvTotalSum2;
    TextView tvTotalSum;

    TextView tvDocnum;

    int dtColIndex;
    int docnumColIndex;
    int placeIdColIndex;
    int sumColIndex;
    int cashColIndex;
    int cardColIndex;
    int discountColIndex;
    int creditColIndex;
    int positionsColIndex;

    Calendar todayCalendar;
    Calendar activeCalendar;
    SimpleDateFormat dateFormatForSorting = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat df2 = new SimpleDateFormat("dd.MM.yy");
    String formattedDateForTvDate;
    String formattedDate;

    LinearLayout linLayout;
    LayoutInflater ltInflater;

    String[] selectionArgs;
    Cursor c;

    String[] res;
    String dialogTitle;

    TextView tvDt;
    String documentDate, documentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setTitle("Ювелирная Коллекция");

        btnPrev = findViewById(R.id.btnPrev);
        btnPrev.setOnClickListener(this);
        tvDate = findViewById(R.id.tvDate);
        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);
        tvTotalSum1 = findViewById(R.id.tvTotalSum1);
        tvTotalSum2 = findViewById(R.id.tvTotalSum2);
        tvTotalSum = findViewById(R.id.tvTotalSum);

        timerIsPaused = false;

        linLayout = findViewById(R.id.linLayout);

        ltInflater = getLayoutInflater();

        if (db == null)
            db = new DB(this);
        db.open();

        db.deleteNewReceipts();

        todayCalendar = Calendar.getInstance();
        activeCalendar = Calendar.getInstance();

        fillList("onCreate");

        if (timer != null)
            timer.cancel();
        timer = new Timer();
        myInfoActivityTimerTask = new MyInfoActivityTimerTask();
        timer.schedule(myInfoActivityTimerTask, 1000, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myInfoActivityTimerTask != null)
            myInfoActivityTimerTask.cancel();
        myInfoActivityTimerTask = null;
        if (timer != null)
            timer.cancel();
        timer = null;
        if (db != null) {
            db.deleteNewReceipts();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPrev:
                timerIsPaused = true;
                db.deleteNewReceipts();
                activeCalendar.add(activeCalendar.DATE, -1);
                //linLayout.removeAllViews();
                fillList("-");
                timerIsPaused = false;
                break;
            case R.id.btnNext:
                timerIsPaused = true;
                db.deleteNewReceipts();
                activeCalendar.add(activeCalendar.DATE, 1);
                //linLayout.removeAllViews();
                fillList("+");
                timerIsPaused = false;
                break;
            case R.id.frame:
                tvDocnum = view.findViewById(R.id.tvDocnum);
                documentDate = tvDate.getText().toString();
                documentDate = "20" + documentDate.substring(6, 8) + documentDate.substring(3, 5) + documentDate.substring(0, 2);
                tvDt = view.findViewById(R.id.tvDt);
                documentTime = tvDt.getText().toString();
                documentTime = documentTime.substring(0, 2) + documentTime.substring(3, 5) + documentTime.substring(6, 8);
                //documentTime = documentTime.substring(0, 2) + documentTime.substring(3, 5);
                //Toast.makeText(InfoActivity.this, documentDate + documentTime, Toast.LENGTH_LONG).show();
                res = db.getItemsOfReceiptByDocnumAndDtAsArray(tvDocnum.getText().toString(), documentDate + documentTime);
                //res = db.getItemsOfReceiptByDocnumAsArray(tvDocnum.getText().toString());
                dialogTitle = tvDocnum.getText().toString();
                removeDialog(1);
                showDialog(1);
                //Toast.makeText(InfoActivity.this, db.getItemsOfReceiptByDocnum(tvDocnum.getText().toString()), Toast.LENGTH_LONG).show();
                break;
        }
    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Состав чека " + dialogTitle);
        adb.setItems(res, null);
        return adb.create();
    }

    protected void onPrepareDialog(int id, Dialog dialog) {
        AlertDialog aDialog = (AlertDialog) dialog;
        ListAdapter lAdapter = aDialog.getListView().getAdapter();
        if (lAdapter instanceof BaseAdapter) {
            BaseAdapter bAdapter = (BaseAdapter) lAdapter;
            bAdapter.notifyDataSetChanged();
        }
    }

    public void fillListFromCursor(Cursor c) {
        View item;
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
                View vSeparator = item.findViewById(R.id.vSeparator);

                if (placeid.equals("1")) {
                    totalSum1 += c.getFloat(sumColIndex);
                }
                else {
                    totalSum2 += c.getFloat(sumColIndex);
                    item.setBackgroundColor(Color.parseColor("#f1eaa6"));
                    vSeparator.setBackgroundColor(Color.WHITE);
                }

                TextView tvDt = item.findViewById(R.id.tvDt);
                tvDt.setText(dt);

                TextView tvDocnum = item.findViewById(R.id.tvDocnum);
                tvDocnum.setText(docnum);

                TextView tvDiscount = item.findViewById(R.id.tvDiscount);
                if (discount > 0)
                    tvDiscount.setText("-" + String.format("%d", discount) + "%");
                else
                    tvDiscount.setText("");

                TextView tvPositions = item.findViewById(R.id.tvPositions);
                tvPositions.setText(c.getString(positionsColIndex));

                cash = c.getFloat(cashColIndex);
                card = c.getFloat(cardColIndex);
                sum = c.getFloat(sumColIndex);

                TextView tvNal = item.findViewById(R.id.tvNal);
                TextView tvBeznal = item.findViewById(R.id.tvBeznal);
                TextView tvCash = item.findViewById(R.id.tvCash);
                TextView tvCard = item.findViewById(R.id.tvCard);

                if (cash != sum) {
                    tvNal.setText("НАЛ:");
                    tvBeznal.setText("Б/Н:");
//                    tvNal.setText("CASH:");
//                    tvBeznal.setText("CARD:");
                    tvCash.setText(String.format("%,.0f", cash));
                    tvCard.setText(String.format("%,.0f", card));
                }
                else {
                    tvNal.setText("");
                    tvBeznal.setText("");
                    tvCash.setText("");
                    tvCard.setText("");
                }

                TextView tvSum = item.findViewById(R.id.tvSum);
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
        String newActiveDt;
        int newYear = 0, newMonth = 0, newDay = 0;
        totalSum1 = 0;
        totalSum2 = 0;
        totalSum = 0;

        formattedDate = dateFormatForSorting.format(activeCalendar.getTime());
        formattedDateForTvDate = df2.format(activeCalendar.getTime());

        selectionArgs = new String[] { formattedDate, formattedDate + "235959" };

        c = db.getReceiptsForDay(selectionArgs);
        if (action == "-") {
            if (c.getCount() == 0) {
                selectionArgs = new String[] { "20160101", formattedDate + "235959" };
                c = db.getReceiptsForPeriod(selectionArgs);
                if (c.getCount() == 0) {
                    Toast.makeText(InfoActivity.this, "Достигнуто начало архива", Toast.LENGTH_SHORT).show();
                    activeCalendar.add(activeCalendar.DATE, 1);
                    return;
                }
                else {
                    c.moveToLast();
                    newActiveDt = c.getString(dtColIndex);
                    newYear = Integer.parseInt(newActiveDt.substring(0, 4), 10);
                    newMonth = Integer.parseInt(newActiveDt.substring(4, 6), 10);
                    newDay = Integer.parseInt(newActiveDt.substring(6, 8), 10);
                    activeCalendar.set(newYear, newMonth - 1, newDay, 0, 0, 0);
                    formattedDate = dateFormatForSorting.format(activeCalendar.getTime());
                    formattedDateForTvDate = df2.format(activeCalendar.getTime());
                    //Log.d(LOG_TAG, "formattedDate = " + formattedDate);
                    selectionArgs = new String[] { formattedDate, formattedDate + "235959" };
                    c = db.getReceiptsForDay(selectionArgs);
                    Toast.makeText(InfoActivity.this, formattedDateForTvDate, Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if (action == "+") {
            if (c.getCount() == 0 && !activeCalendar.equals(todayCalendar)) {
                selectionArgs = new String[] { formattedDate + "235959", "20201231235959" };
                c = db.getReceiptsForPeriod(selectionArgs);
                if (c.getCount() == 0) {
                    Toast.makeText(InfoActivity.this, "Достигнут конец архива", Toast.LENGTH_SHORT).show();
                    activeCalendar.add(activeCalendar.DATE, -1);
                    return;
                }
                else {
                    c.moveToFirst();
                    newActiveDt = c.getString(dtColIndex);
                    newYear = Integer.parseInt(newActiveDt.substring(0, 4), 10);
                    newMonth = Integer.parseInt(newActiveDt.substring(4, 6), 10);
                    newDay = Integer.parseInt(newActiveDt.substring(6, 8), 10);
                    activeCalendar.set(newYear, newMonth - 1, newDay, 0, 0, 0);
                    formattedDate = dateFormatForSorting.format(activeCalendar.getTime());
                    formattedDateForTvDate = df2.format(activeCalendar.getTime());
                    //Log.d(LOG_TAG, "formattedDate = " + formattedDate);
                    selectionArgs = new String[] { formattedDate, formattedDate + "235959" };
                    c = db.getReceiptsForDay(selectionArgs);
                    Toast.makeText(InfoActivity.this, formattedDateForTvDate, Toast.LENGTH_SHORT).show();
                }
            }
        }
        tvDate.setText(formattedDateForTvDate);
        linLayout.removeAllViews();
        fillListFromCursor(c);

        tvTotalSum1.setText(String.format("%,.0f", totalSum1));
        tvTotalSum2.setText(String.format("%,.0f", totalSum2));
        tvTotalSum.setText(String.format("%,.0f", totalSum));
    }

    public void updateList() {
        fillListFromCursor(c);
        if (c.getCount() > 0)
            db.deleteNewReceipts();
        tvTotalSum1.setText(String.format("%,.0f", totalSum1));
        tvTotalSum2.setText(String.format("%,.0f", totalSum2));
        tvTotalSum.setText(String.format("%,.0f", totalSum));
    }

    class MyInfoActivityTimerTask extends TimerTask {
        String line;

        @Override
        public void run() {
            if (timerIsPaused)
                return;
            c = db.getNewReceipts();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (c.getCount() > 0) {
                        if (df2.format(activeCalendar.getTime()).equals(df2.format(todayCalendar.getTime())))
                            fillList("");
                            //  updateList() был заменен на fillList("") в связи с тем, что первый инкременировал значения totalSum, totalSum1 и totalSum2
                            //updateList();
                    }
                }
            });
        }
    }
}
