package com.example.jewcol;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.jewcol.MainActivity.LOG_TAG;
import static com.example.jewcol.MainActivity.URLString;
import static com.example.jewcol.MainActivity.db;

public class UpdateTask extends AsyncTask<Void, Void, String> {
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String resultJson = "empty result";
    String actualDt = "";

    public UpdateTask(String actualDt) {
        this.actualDt = actualDt;
    }

    @Override
    protected String doInBackground(Void... voids) {
        URL url;
        try {
            if (actualDt == "")
                url = new URL(URLString + "/?id=1");
            else
                url = new URL(URLString + "/?id=1&td=" + actualDt);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            resultJson = buffer.toString();
            return resultJson;
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "doInBackground e = " + e.getLocalizedMessage());
            return "caught";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d(LOG_TAG, s);
        JSONObject dataJsonObj = null;
        JSONArray receipts = null;
        JSONArray items = null;
        try
        {
            dataJsonObj = new JSONObject(s);
            receipts = dataJsonObj.getJSONArray("receipts");
            items = dataJsonObj.getJSONArray("items");

            for (int i = 0; i < receipts.length(); i++) {
                JSONObject receipt = receipts.getJSONObject(i);
                db.addReceipt(receipt);
                db.addNewReceipt(receipt);
            }

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                db.addItem(item);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            Log.d(LOG_TAG, "onPostExecute e = " + e.getLocalizedMessage());
        }
    }
}
