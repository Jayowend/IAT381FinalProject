package com.team5.iat381finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.regex.Pattern;

class Database {
    private SQLiteDatabase db;
    private final Context context;
    private final DatabaseHelper databaseHelper;
    private final String[] columns;

    public Database (Context c) {
        context = c;
        databaseHelper = DatabaseHelper.getInstance(context);

        // get constants columns
        Field[] constantFields = Constants.class.getFields();
        Pattern p = Pattern.compile("\\$change|DATABASE_NAME|DATABASE_VERSION|TABLE_NAME|serialVersionUID");
        ArrayList<String> matches = new ArrayList<>();
        for (Field f : constantFields)
            if (!p.matcher(f.getName()).matches())
                try {
                    matches.add((String) f.get(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
        columns = matches.toArray(new String[matches.size()]);
    }

    public long insertData(String name, String date, byte[] photo) {
        db = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.NAME, name);
        contentValues.put(Constants.EXP, date);
        contentValues.put(Constants.IMG, photo);
        contentValues.put(Constants.EXPIRED, "false");
        long id = db.insert(Constants.TABLE_NAME, null, contentValues);

        return id;
    }

    public long setExpired(String expired, String uid) {
        db = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.EXPIRED, expired);
        long id = db.update(Constants.TABLE_NAME, contentValues, "_id=" + uid, null);

        return id;
    }

    public Cursor getData() {
        db = databaseHelper.getWritableDatabase();
        return db.query(Constants.TABLE_NAME, columns, null, null, null, null, null);
    }

    public Cursor getData(String constant, String query) // overloaded to get search selection for specific constant (e.g. Constants.EXP) and query
    {
        String selection = constant + "='" + query + "'";
        db = databaseHelper.getWritableDatabase();
        return db.query(Constants.TABLE_NAME, columns, selection, null, null, null, null);
    }

    public boolean RemoveData(String uid) {
        db = databaseHelper.getWritableDatabase();
        return db.delete(Constants.TABLE_NAME, Constants.UID +  "='" + uid + "'", null) > 0;
    }
}




