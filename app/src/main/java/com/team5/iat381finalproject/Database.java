package com.team5.iat381finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Database {
    private SQLiteDatabase db;
    private Context context;
    private final DatabaseHelper databaseHelper;
    private ContentValues contentValues;
    private String[] columns;

    public Database (Context c) {
        context = c;
        databaseHelper = new DatabaseHelper(context);

        // get constants columns
        Field[] constantFields = Constants.class.getFields();
        Pattern p = Pattern.compile("\\$change|DATABASE_NAME|DATABASE_VERSION|TABLE_NAME|serialVersionUID");
        ArrayList<String> matches = new ArrayList<String>();
        for (Field f : constantFields)
            if (!p.matcher(f.getName()).matches())
                try {
                    matches.add((String) f.get(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
        columns = (String[]) matches.toArray(new String[matches.size()]);
    }
    public long insertData (String name, String date, byte[] photo) {
        db = databaseHelper.getWritableDatabase();

        contentValues = new ContentValues();
        contentValues.put(Constants.NAME, name);
        contentValues.put(Constants.EXP, date);
        contentValues.put(Constants.IMG, photo);

        long id = db.insert(Constants.TABLE_NAME, null, contentValues);
        return id;
    }

    public Cursor getData() {
        db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }

    public Cursor getData(String constant, String query) // overloaded to get search selection for specific constant (e.g. Constants.EXP) and query
    {
        String selection = constant + "='" + query + "'";;
        db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, null, null, null, null);
        return cursor;
    }

    public boolean RemoveData(String uid) {
        db = databaseHelper.getWritableDatabase();
        return db.delete(Constants.TABLE_NAME, Constants.UID +  "='" + uid + "'", null) > 0;
    }
}




