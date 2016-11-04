package com.team5.iat381finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.SyncStateContract;

/**
 * Created by Chris on 11/2/2016.
 */

public class Database {
    SQLiteDatabase db;
    private Context context;
    private final Helper helper;

    public Database (Context c) {
        context = c;
        helper = new Helper(context);
    }
    public long insertData (String name, int year, int month, int day) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String expDate = Integer.toString(year) + "-" +Integer.toString(month)+ "-" + Integer.toString(day);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.NAME, name);
        contentValues.put(Constants.EXP, expDate);

        long id = db.insert (Constants.TABLE_NAME, null, contentValues);
        return id;
    }

    public Cursor getData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.UID, Constants.NAME, Constants.EXP};
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, null, null, null, null, null);
        return cursor;

    }

    public Cursor getSelectedData(String type)
    {
        //select plants from database of type 'herb'


//        String[] columns = {Constants.NAME, Constants.F, Constants.LATIN, Constants.LOCATION};
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.UID, Constants.NAME, Constants.EXP};
        String selection = Constants.NAME + "='" +type+ "'";  //Constants.TYPE = 'type'
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, null, null, null, null);

        return cursor;
    }
    public boolean RemoveData(String type) {
        return db.delete(Constants.TABLE_NAME, Constants.NAME + "=" + type, null) > 0;
    }

}




//    public String getSelectedData(String type)
//    {
//        //select plants from database of type 'herb'
//        SQLiteDatabase db = helper.getWritableDatabase();
//        String[] columns = {Constants.NAME, Constants.TYPE, Constants.LATIN, Constants.LOCATION};
//
//        String selection = Constants.TYPE + "='" +type+ "'";  //Constants.TYPE = 'type'
//        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, null, null, null, null);
//
//        StringBuffer buffer = new StringBuffer();
//        while (cursor.moveToNext()) {
//
//            int index1 = cursor.getColumnIndex(Constants.NAME);
//            int index2 = cursor.getColumnIndex(Constants.TYPE);
//            int index3 = cursor.getColumnIndex(Constants.LATIN);
//            int index4 = cursor.getColumnIndex(Constants.LOCATION);
//            String plantName = cursor.getString(index1);
//            String plantType = cursor.getString(index2);
//            String latin = cursor.getString(index3);
//            String location = cursor.getString(index4);
//            buffer.append(plantName + " " + plantType + " " + latin + " " + location + "\n");
//        }
//        return buffer.toString();
//    }




