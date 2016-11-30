package com.team5.iat381finalproject;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemDetailsActivity extends AppCompatActivity {
    private Database db;
    private Cursor cursor;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemdetails);

        TextView name = (TextView) findViewById(R.id.itemName);
        TextView date = (TextView) findViewById(R.id.expirationDate);
        ImageView itemImageView = (ImageView) findViewById(R.id.itemImage);

        db = new Database(this);
        cursor = db.getData();
        if (cursor.getCount() > 0) {
            cursor.moveToPosition(getIntent().getIntExtra("position", 0));
            Log.i("debug", "got position " + getIntent().getIntExtra("position", 0));
            // get cursor data based on position from intentExtra
            uid = cursor.getString(cursor.getColumnIndexOrThrow(Constants.UID));
            name.setText(cursor.getString(cursor.getColumnIndexOrThrow(Constants.NAME)));
            date.setText(cursor.getString(cursor.getColumnIndexOrThrow(Constants.EXP)));

            byte[] imageByte = cursor.getBlob(cursor.getColumnIndexOrThrow(Constants.IMG));
            if (imageByte.length > 0) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                itemImageView.setImageBitmap(imageBitmap);
            }
        }
    }

    public void backButton (View view) {
        finish();
    }

    public void delete (View view) {
        db.RemoveData(uid);
        finish();
    }
}
