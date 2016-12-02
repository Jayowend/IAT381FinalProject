package com.team5.iat381finalproject;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper mInstance = null;
    private static Context context;

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + Constants.TABLE_NAME;
    private static final String CREATE_TABLE =
            "CREATE TABLE " +
                    Constants.TABLE_NAME + " (" +
                    Constants.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Constants.NAME + " TEXT, " +
                    Constants.EXP + " TEXT, " +
                    Constants.IMG + " BLOB, " +
                    Constants.EXPIRED + " TEXT);" ;

    // use singleton instance for database helper to prevent database/cursor leaks
    // http://stackoverflow.com/questions/18147354/sqlite-connection-leaked-although-everything-closed/18148718#18148718
    public static DatabaseHelper getInstance(Context context) {
        if (mInstance == null)
            mInstance = new DatabaseHelper((context.getApplicationContext()));
        return mInstance;
    }

    private DatabaseHelper(Context context){
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE);
            Toast.makeText(context, "Database created", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            Toast.makeText(context, "exception onCreate() db", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DROP_TABLE);
            onCreate(db);
            Toast.makeText(context, "onUpgrade called", Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            Toast.makeText(context, "exception onUpgrade() db", Toast.LENGTH_LONG).show();
        }
    }
}
