package com.team5.iat381finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Database {
    SQLiteDatabase db;
    private Context context;
    private final Helper helper;

    public Database (Context c) {
        context = c;
        helper = new Helper(context);
    }
    public long insertData (String name, String date, byte[] photo) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.NAME, name);
        contentValues.put(Constants.EXP, date);
        contentValues.put(Constants.IMG, photo);

        long id = db.insert(Constants.TABLE_NAME, null, contentValues);
        return id;
    }

    public Cursor getData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.UID, Constants.NAME, Constants.EXP};
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }

    public Cursor getSelectedData(String uid)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.UID, Constants.NAME, Constants.EXP};
        String selection = Constants.UID + "='" + uid + "'";  //Constants.UID = 'uid'
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, null, null, null, null);
        return cursor;
    }

    public boolean RemoveData(String uid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(Constants.TABLE_NAME, Constants.UID +  "='" + uid + "'", null) > 0;
    }
}




