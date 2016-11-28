package com.team5.iat381finalproject;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Blob;

public class ItemDetailsActivity extends AppCompatActivity {
    private Database db;
    private Cursor cursor;
    private String uid;
    private byte[] imageByte = new byte[0];

    private ImageView itemImageView;
    private TextView name, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemdetails);

        name = (TextView) findViewById(R.id.itemName);
        date = (TextView) findViewById(R.id.expirationDate);
        itemImageView = (ImageView) findViewById(R.id.itemImage);

        db = new Database(this);
        cursor = db.getData();
        cursor.moveToPosition(getIntent().getIntExtra("position", 0));

        // get cursor data based on position from intentExtra
        uid = cursor.getString(cursor.getColumnIndexOrThrow(Constants.UID));
        name.setText(cursor.getString(cursor.getColumnIndexOrThrow(Constants.NAME)));
        date.setText(cursor.getString(cursor.getColumnIndexOrThrow(Constants.EXP)));

        imageByte = cursor.getBlob(cursor.getColumnIndexOrThrow(Constants.IMG));
        if (imageByte.length > 0) {
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
            itemImageView.setImageBitmap(imageBitmap);
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
