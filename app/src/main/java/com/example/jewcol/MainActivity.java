package com.example.jewcol;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.toIntExact;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static String LOG_TAG = "myLogs";
    public static DB db;
    public static String URLString = "http://n91963vz.beget.tech/index.php";
    //public static String URLString = "http://jewcol.000webhostapp.com/api.php";
    //public static String URLString = "http://nailmail.h1n.ru/api.php";
    //public static String URLString = "http://alco.nailmail.h1n.ru/api.php";

    public static double todayTotalSum1;
    public static double todayTotalSum2;

    double _todayTotalSum1 = 0, _todayTotalSum2 = 0;

//    Calendar todayCalendar;
    SimpleDateFormat dateFormatForSorting;
//    String formattedDate;

    String[] selectionArgs;

    //Button btnGoToInfoActivity;
    ImageView imageView2;
    //Button btnGetAll;
    //Button btnGetNew;
    TextView tvTotalSumLeader;
    TextView tvTotalSumLooser;

//    Typeface tf1;

    public Timer timer;
    public MyTimerTask myTimerTask;

    public HttpURLConnection urlConnection = null;
    public BufferedReader reader = null;
    public String actualDt = "";

    public Cursor c;

    long countServer, countLocal;

    NotificationManager nm;

    private EditTextPreference mTitlePreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setTitle("Ювелирная Коллекция");

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (db == null)
            db = new DB(getApplicationContext());
        db.open();

        dateFormatForSorting = new SimpleDateFormat("yyyyMMdd");

        tvTotalSumLeader = findViewById(R.id.tvTotalSumLeader);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        setTitle(preferences.getString("titlePref", "Задайте заголовок!"));
        URLString = preferences.getString("hostAddressPref", "");
        Log.d("URLString", URLString);

        imageView2 = findViewById(R.id.imageView2);
        switch (preferences.getString("listPref", "").toString()) {
            //region Ювелирка
            case "1":
                imageView2.setImageResource(R.drawable.goldbar);
                break;
            case "6":
                imageView2.setImageResource(R.drawable.goldbar2);
                break;
            case "7":
                imageView2.setImageResource(R.drawable.hotgold);
                break;
            case "8":
                imageView2.setImageResource(R.drawable.goldsafe);
                break;
            case "9":
                imageView2.setImageResource(R.drawable.goldenrings2);
                break;
            case "10":
                imageView2.setImageResource(R.drawable.silverrings);
                break;
            //endregion

            //region Ритэйл
            case "2":
                imageView2.setImageResource(R.drawable.scarticon);
                break;
            case "11":
                imageView2.setImageResource(R.drawable.strolleyfull2);
                break;
            case "12":
                imageView2.setImageResource(R.drawable.strolley);
                break;
            case "13":
                imageView2.setImageResource(R.drawable.shoppingcart);
                break;
            case "14":
                imageView2.setImageResource(R.drawable.strolley22);
                break;
            //endregion

            //region Дрогери
            case "3":
                imageView2.setImageResource(R.drawable.drogerie);
                break;
            //endregion

            //region Фармацевтика
            case "4":
                imageView2.setImageResource(R.drawable.pharmacy);
                break;
            case "15":
                imageView2.setImageResource(R.drawable.pharmacy2);
                break;
            case "16":
                imageView2.setImageResource(R.drawable.pharmacy7);
                break;
            case "17":
                imageView2.setImageResource(R.drawable.pharmacy9);
                break;
            case "18":
                imageView2.setImageResource(R.drawable.pharmacy162);
                break;
            //endregion

            //region Алкоголь
            case "5":
                imageView2.setImageResource(R.drawable.wine);
                break;
            case "19":
                imageView2.setImageResource(R.drawable.champagne);
                break;
            case "20":
                imageView2.setImageResource(R.drawable.beer42);
                break;
            case "21":
                imageView2.setImageResource(R.drawable.beer52);
                break;
            case "22":
                imageView2.setImageResource(R.drawable.beer62);
                break;
            //endregion

            default:
                imageView2.setImageResource(R.drawable.goldbar);
                break;
        }
//        preferences = null;
        imageView2.setOnClickListener(this);

        tvTotalSumLooser = findViewById(R.id.tvTotalSumLooser);

        if (timer != null)
            timer.cancel();
        timer = new Timer();
        myTimerTask = new MyTimerTask();

        timer.schedule(myTimerTask, 100, 10000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        setTitle(preferences.getString("titlePref", "Задайте заголовок!"));
        URLString = preferences.getString("hostAddressPref", "");

        switch (preferences.getString("listPref", "").toString()) {
            //region Ювелирка
            case "1":
                imageView2.setImageResource(R.drawable.goldbar);
                break;
            case "6":
                imageView2.setImageResource(R.drawable.goldbar2);
                break;
            case "7":
                imageView2.setImageResource(R.drawable.hotgold);
                break;
            case "8":
                imageView2.setImageResource(R.drawable.goldsafe);
                break;
            case "9":
                imageView2.setImageResource(R.drawable.goldenrings2);
                break;
            case "10":
                imageView2.setImageResource(R.drawable.silverrings);
                break;
            //endregion

            //region Ритэйл
            case "2":
                imageView2.setImageResource(R.drawable.scarticon);
                break;
            case "11":
                imageView2.setImageResource(R.drawable.strolleyfull2);
                break;
            case "12":
                imageView2.setImageResource(R.drawable.strolley);
                break;
            case "13":
                imageView2.setImageResource(R.drawable.shoppingcart);
                break;
            case "14":
                imageView2.setImageResource(R.drawable.strolley22);
                break;
            //endregion

            //region Дрогери
            case "3":
                imageView2.setImageResource(R.drawable.drogerie);
                break;
            //endregion

            //region Фармацевтика
            case "4":
                imageView2.setImageResource(R.drawable.pharmacy);
                break;
            case "15":
                imageView2.setImageResource(R.drawable.pharmacy2);
                break;
            case "16":
                imageView2.setImageResource(R.drawable.pharmacy7);
                break;
            case "17":
                imageView2.setImageResource(R.drawable.pharmacy9);
                break;
            case "18":
                imageView2.setImageResource(R.drawable.pharmacy162);
                break;
            //endregion

            //region Алкоголь
            case "5":
                imageView2.setImageResource(R.drawable.wine);
                break;
            case "19":
                imageView2.setImageResource(R.drawable.champagne);
                break;
            case "20":
                imageView2.setImageResource(R.drawable.beer42);
                break;
            case "21":
                imageView2.setImageResource(R.drawable.beer52);
                break;
            case "22":
                imageView2.setImageResource(R.drawable.beer62);
                break;
            //endregion

            //region ПК
            case "23":
                imageView2.setImageResource(R.drawable.pc1);
                break;
            case "24":
                imageView2.setImageResource(R.drawable.pc2);
                break;
            case "25":
                imageView2.setImageResource(R.drawable.pc3);
                break;
            //endregion ПК

            case "26":
                imageView2.setImageResource(R.drawable.wear);
                break;
            case "27":
                imageView2.setImageResource(R.drawable.levis);
                break;

            case "28":
                imageView2.setImageResource(R.drawable.cafe1);
                break;
            case "29":
                imageView2.setImageResource(R.drawable.cafe2);
                break;

            case "30":
                imageView2.setImageResource(R.drawable.gifts1);
                break;
            case "31":
                imageView2.setImageResource(R.drawable.gifts2);
                break;

            case "32":
                imageView2.setImageResource(R.drawable.watches1);
                break;
            case "33":
                imageView2.setImageResource(R.drawable.watches2);
                break;

            case "34":
                imageView2.setImageResource(R.drawable.leather1);
                break;
            case "35":
                imageView2.setImageResource(R.drawable.leather2);
                break;

            case "36":
                imageView2.setImageResource(R.drawable.vape1);
                break;
            case "37":
                imageView2.setImageResource(R.drawable.vape2);
                break;
            case "38":
                imageView2.setImageResource(R.drawable.vape3);
                break;

            case "39":
                imageView2.setImageResource(R.drawable.parfum1);
                break;
            case "40":
                imageView2.setImageResource(R.drawable.parfum2);
                break;

            default:
                imageView2.setImageResource(R.drawable.goldbar);
                break;
        }
//        preferences = null;
        imageView2.setOnClickListener(this);

        if (preferences.getBoolean("updateDBPref", false)) {
            SharedPreferences.Editor prefEdit = preferences.edit();
            prefEdit.putBoolean("updateDBPref", false);
            prefEdit.commit();
            db.mDB.execSQL("DROP TABLE IF EXISTS receipts");
            db.mDB.execSQL("DROP TABLE IF EXISTS items");
            db.mDB.execSQL("DROP TABLE IF EXISTS newReceipts");
            db.mDBHelper.onCreate(db.mDB);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null)
            timer.cancel();
        timer = null;
        if (db != null) {
            db.deleteNewReceipts();
            db.close();
            db = null;
        }
    }

//    @Override
//    protected void onUserLeaveHint()
//    {
//        super.onUserLeaveHint();
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvTotalSumLooser:
//                db.mDB.execSQL("DROP TABLE IF EXISTS receipts");
//                db.mDB.execSQL("DROP TABLE IF EXISTS items");
//                db.mDB.execSQL("DROP TABLE IF EXISTS newReceipts");
//                db.mDBHelper.onCreate(db.mDB);
                break;
            case R.id.imageView2:
                Intent intent1 = new Intent(this, InfoFragmentActivity.class);
                startActivity(intent1);
                break;
            case R.id.tvTotalSumLeader:
                Intent intent = new Intent(this, InfoActivity.class);
                startActivity(intent);
                break;
            /*case R.id.btnGetAll:
                new UpdateTask("").execute();
                break;
            case R.id.btnGetNew:
                updateTables();
                break;*/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem mi = menu.add(0, 1, 0, "Настройки");
        mi.setIntent(new Intent(this, PrefActivity.class));
        mi.setIcon(android.R.drawable.ic_menu_preferences);
//        mi.setIcon(android.R.drawable.ic_menu_manage);
        mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        mi = menu.add(0, 1, 0, "Разработчики");
        mi.setIntent(new Intent(this, AboutActivity.class));
//        mi.setIcon(android.R.drawable.ic_dialog_info);
        mi.setIcon(android.R.drawable.ic_menu_call);
        mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return super.onCreateOptionsMenu(menu);
    }

    private void updateTables() {
        String dt = "";
        c = db.getLastReceipt();
        if (c.moveToLast()) {
            int dtColIndex = c.getColumnIndex("dt");
            dt = c.getString(dtColIndex);
        }
        if (dt != "")
            new UpdateTask(dt.substring(0, 8)).execute();
        else
            new UpdateTask(dt).execute();
    }

    void sendNotif(int newReceiptsCount) {
        String contentTitle;

        //Intent resultIntent = new Intent(this, MainActivity.class);
        //PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (newReceiptsCount == 1)
            contentTitle = "BINGO!!! У Вас новый чек!";
        else
            contentTitle = "У Вас " + newReceiptsCount + " новых чеков";

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Uri alarmSound = Uri.parse("android.resource://" + getPackageName() +"/" + R.raw.the_little_dwarf);
        //Uri alarmSound = Uri.parse("android.resource://" + getPackageName() +"/" + R.raw.pf_money_2);
        //Uri alarmSound = Uri.parse("android.resource://" + getPackageName() +"/" + R.raw.kachin);
        //Uri alarmSound = Uri.parse("android.resource://" + getPackageName() +"/" + R.raw.ends_with_a_harmonic);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "M_CH_ID")
//                .setChannelId("M_CH_ID")

//region оригинально этих строк не было
//какая-то из этих строк отключает выбранный alarmSound и устанавливает default-сигнал
//                .setDefaults(Notification.DEFAULT_LIGHTS)
//                .setWhen(System.currentTimeMillis())
//                .setTicker("Hearty365")
                .setPriority(Notification.PRIORITY_MAX)
//endregion оригинально этих строк не было

                .setSmallIcon(R.mipmap.ic_stat_attach_money)
                .setContentTitle(contentTitle)
                .setContentText(preferences.getString("titlePref", "Задайте заголовок!"))
//                .setContentText("Ювелирная Коллекция")
                //.setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setNumber(newReceiptsCount)
                .setContentInfo("Content info")
                .setColor(Color.YELLOW)
                .setVibrate(new long[] { 0L, 100L, 200L, 300L, 400L, 500L })
                .setLights(Color.BLUE, 100, 100)
                .setSound(alarmSound);
                //.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

//        Notification notification = builder.build();

        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        //  Для Android 8 Oreo следующие строки раскомментировать, для младших версий - закомментировать
        NotificationChannel notificationChannel = new NotificationChannel("M_CH_ID", "M_CH_ID", NotificationManager.IMPORTANCE_MAX);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        notificationChannel.shouldVibrate();
        notificationChannel.setSound(alarmSound, audioAttributes);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationChannel.setLightColor(Color.BLUE);
        manager.createNotificationChannel(notificationChannel);
        //---------------------------------------------------------------------------------------------------------------------------------------------------

//        try {
//            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);
//            r.play();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        manager.notify(0, builder.build());

//        nm.notify(1, notification);
    }

    class MyTimerTask extends TimerTask {
        String line;
        String resultJson = "empty result";
        //JSONObject dataJsonObj = null;

        @Override
        public void run() {
            URL url;

            c = db.getLastReceipt();

            if (c.moveToLast()) {
                int dtColIndex = c.getColumnIndex("dt");
                actualDt = c.getString(dtColIndex);
            }
            else {
                actualDt = "";
            }

            try {
                if (actualDt == "")
                    url = new URL(URLString + "/?id=2");
                else
                    url = new URL(URLString + "/?id=2&td=" + actualDt.substring(0, 8));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                line = reader.readLine();
                buffer.append(line);
                resultJson = buffer.toString();
                JSONObject dataJsonObj = null;
                dataJsonObj = new JSONObject(resultJson);
                countServer = dataJsonObj.getLong("COUNT");
                if (actualDt != "") {
                    String a1 = actualDt.substring(0, 8);
                    String a2 = actualDt.substring(0, 8) + "235959";
                    selectionArgs = new String[]{a1, a2};
                    countLocal = db.getRowCount(selectionArgs);
                }
                else
                    countLocal = 0L;
                if ((countServer - countLocal) >= 0)
                    countServer = countServer - countLocal;
                _todayTotalSum1 = dataJsonObj.getDouble("SUM1");
                _todayTotalSum2 = dataJsonObj.getDouble("SUM2");

                //  20210615 start
                if (countServer != 0)
                    sendNotif((int)countServer);
                //  20210615 finish
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "MyTimerTask caught " + e.getMessage());
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (countServer != 0) {
                        sendNotif((int)countServer);
//                        sendNotif(toIntExact(countServer));
                        Toast.makeText(MainActivity.this, "" + countServer + " новых чеков", Toast.LENGTH_SHORT).show();
                        updateTables();
                    }
                    //if (todayTotalSum1 != _todayTotalSum1 || todayTotalSum2 != _todayTotalSum2) {
                        todayTotalSum1 = _todayTotalSum1;
                        todayTotalSum2 = _todayTotalSum2;
                        if (todayTotalSum1 > todayTotalSum2) {
                            tvTotalSumLeader.setText(String.format("%,.0f", todayTotalSum1));
                            tvTotalSumLooser.setText(String.format("%,.0f", todayTotalSum2));
                        }
                        else {
                            tvTotalSumLeader.setText(String.format("%,.0f", todayTotalSum2));
                            tvTotalSumLooser.setText(String.format("%,.0f", todayTotalSum1));
                        }
                    //}
                }
            });
        }
    }
}
