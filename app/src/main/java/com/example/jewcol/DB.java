package com.example.jewcol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONObject;

import static com.example.jewcol.MainActivity.LOG_TAG;
import static com.example.jewcol.MainActivity.db;

public class DB {
    public static String DB_NAME = "myDB";
    public static int DB_VERSION = 46;

    private Context mCtx;

    public DBHelper mDBHelper;  //  originally private
    public SQLiteDatabase mDB;  //  originally private

    public DB(Context ctx) {
        mCtx = ctx;
    }

    public void open() {
        if (mDBHelper == null)
            mDBHelper = new DBHelper(mCtx);
        mDB = mDBHelper.getWritableDatabase();
    }

    public void close() {
        if (mDBHelper != null)
            mDBHelper.close();
    }

    public Cursor getLastReceipt() {
        return mDB.query("receipts", null, null, null, null, null, "dt DESC", "1");
    }

    public void addReceipt(JSONObject receipt) {
        String dt, docnum, placeid;
        double sum, cash, card;
        int discount, credit, positions;

        long res;

        try {
            dt = receipt.getString("DT");
            docnum = receipt.getString("DOCNUM");
            placeid = receipt.optString("PLACEID");
            sum = receipt.getDouble("SUM");
            cash = receipt.getDouble("CASH");
            card = receipt.getDouble("CARD");
            discount = receipt.getInt("DISCOUNT");
            credit = receipt.getInt("CREDIT");
            positions = receipt.getInt("POSITIONS");

            ContentValues cv = new ContentValues();

            cv.clear();
            cv.put("DT", dt);
            cv.put("DOCNUM", docnum);
            cv.put("PLACEID", placeid);
            cv.put("SUM", sum);
            cv.put("CASH", cash);
            cv.put("CARD", card);
            cv.put("DISCOUNT", discount);
            cv.put("CREDIT", credit);
            cv.put("POSITIONS", positions);

            res = mDB.insert("receipts", null, cv);
//            Log.d(LOG_TAG, "res = " + res);
            //  здесь если res = -1, то нужно удалять ранее записанные items данного чека, т.к. они будут задублированы
            if (res == -1)
                deleteItemsOfReceipt(dt);
        }
        catch (Exception e) {
//            Log.d(LOG_TAG, "addReceipt e = " + e.getMessage());
        }
        finally {
            //  здесь бы добавлять items только что добавленного чека
        }
    }

    public void deleteItemsOfReceipt(String dt) {
        mDB.delete("items", "DT = " + dt, null);
    }

    public void addNewReceipt(JSONObject receipt) {
        String dt, docnum, placeid;
        double sum, cash, card;
        int discount, credit, positions;

        long res;

        try {
            dt = receipt.getString("DT");
            docnum = receipt.getString("DOCNUM");
            placeid = receipt.getString("PLACEID");
            sum = receipt.getDouble("SUM");
            cash = receipt.getDouble("CASH");
            card = receipt.getDouble("CARD");
            discount = receipt.getInt("DISCOUNT");
            credit = receipt.getInt("CREDIT");
            positions = receipt.getInt("POSITIONS");

            ContentValues cv = new ContentValues();

            cv.clear();
            cv.put("DT", dt);
            cv.put("DOCNUM", docnum);
            cv.put("PLACEID", placeid);
            cv.put("SUM", sum);
            cv.put("CASH", cash);
            cv.put("CARD", card);
            cv.put("DISCOUNT", discount);
            cv.put("CREDIT", credit);
            cv.put("POSITIONS", positions);

            res = mDB.insert("newReceipts", null, cv);
//            Log.d(LOG_TAG, "new res = " + res);
        }
        catch (Exception e) {
//            Log.d(LOG_TAG, "addNewReceipt e = " + e.getMessage());
        }
    }

    public void deleteNewReceipts() {
        mDB.delete("newReceipts", null, null);
    }

    public Cursor getReceiptsForDay(String[] selectionArgs) {
        return mDB.query("receipts", null, "dt >= ? AND dt <= ?", selectionArgs, null, null, null);
    }

    public Cursor getReceiptsForPeriod(String[] selectionArgs) {
        return mDB.query("receipts", null, "dt >= ? AND dt <= ?", selectionArgs, null, null, null);
    }

    public Cursor getNewReceipts() {
        return mDB.query("newReceipts", null, null, null, null, null, null);
    }

    public void addItem(JSONObject item) {
        String dt, sku, ean, description;
        int quantity;
        double sum;

        long res;

        try {
            dt = item.getString("DT");
            sku = item.getString("SKU");
            ean = item.optString("EAN");
            description = item.optString("DESCRIPTION");
            quantity = item.getInt("QUANTITY");
            sum = item.getDouble("SUM");

            ContentValues cv = new ContentValues();

            cv.clear();
            cv.put("DT", dt);
            cv.put("SKU", sku);
            cv.put("EAN", ean);
            cv.put("DESCRIPTION", description);
            cv.put("QUANTITY", quantity);
            cv.put("SUM", sum);

            res = mDB.insert("items", null, cv);
//            Log.d(LOG_TAG, "addItem res = " + res);
        }
        catch (Exception e) {
//            Log.d(LOG_TAG, "addItem e = " + e.getMessage());
        }
    }

    public String getItemsOfReceiptByDocnum(String docNum) {
        String[] selectionArgs = new String[] { docNum };
        String result = "";
        String dt;

        Cursor c = mDB.query("receipts", null, "docnum = ?", selectionArgs, null, null, null);
        if (c.getCount() > 0) {
            int dtColIndex = c.getColumnIndex("dt");
            c.moveToFirst();
            dt = c.getString(dtColIndex);
            selectionArgs = new String[] { dt };
            c = mDB.query("items", null, "dt = ?", selectionArgs, null, null, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                int descriptionColIndex = c.getColumnIndex("description");
                result = c.getString(descriptionColIndex);
                while (c.moveToNext()) {
                    result += "\n" + c.getString(descriptionColIndex);
                }
            }
        }
        return result;
    }

    public String[] getItemsOfReceiptByDocnumAsArray(String docNum) {
        String[] selectionArgs = new String[] { docNum };
        String dt;
        String[] res;

        Cursor c = mDB.query("receipts", null, "docnum = ?", selectionArgs, null, null, null);
        if (c.getCount() > 0) {
            int dtColIndex = c.getColumnIndex("dt");
            c.moveToFirst();
            dt = c.getString(dtColIndex);
            selectionArgs = new String[] { dt };
            c = mDB.query("items", null, "dt = ?", selectionArgs, null, null, null);
            if (c.getCount() > 0) {
                res = new String[c.getCount()];
                c.moveToFirst();
                int descriptionColIndex = c.getColumnIndex("description");
                int i = 0;
                res[i] = c.getString(descriptionColIndex);
                while (c.moveToNext()) {
                    i++;
                    res[i] = c.getString(descriptionColIndex);
                }
            }
            else
                res = new String[1];
        }
        else
            res = new String[1];
        return res;
    }

    public String[] getItemsOfReceiptByDocnumAndDtAsArray(String docNum, String docDt) {
        String[] selectionArgs = new String[] { docDt + "%" };
        String dt;
        String[] res;

        Cursor c = mDB.query("receipts", null, "dt LIKE ?", selectionArgs, null, null, null);
        if (c.getCount() > 0) {
            int dtColIndex = c.getColumnIndex("dt");
            c.moveToFirst();
            dt = c.getString(dtColIndex);
            selectionArgs = new String[] { dt };
            c = mDB.query("items", null, "dt = ?", selectionArgs, null, null, null);
            if (c.getCount() > 0) {
                res = new String[c.getCount()];
                c.moveToFirst();
                int descriptionColIndex = c.getColumnIndex("description");
                int i = 0;
                res[i] = c.getString(descriptionColIndex);
                while (c.moveToNext()) {
                    i++;
                    res[i] = c.getString(descriptionColIndex);
                }
            }
            else {
                res = new String[1];
                res[0] = "Нет записей!";
            }
        }
        else {
            res = new String[1];
            res[0] = "Нет записей!";
        }
        return res;
    }

    public void getTodayTotalSums(String[] selectionArgs) {
    }

    public long getRowCount(String[] selectionArgs) {
        return DatabaseUtils.queryNumEntries(mDB,"receipts", "dt >= ? AND dt <= ?", selectionArgs);
    }

//    public int getDaysCount() {
//        Cursor c = mDB.query(true,"receipts", null, "dt", null, null, null, null, null);
//        return c.getCount();
//    }

    public Cursor getDistinctDt() {
        String sqlQuery = "select distinct substr(dt, 1, 8) as dt from receipts";
        return mDB.rawQuery(sqlQuery, null);
    }
}
