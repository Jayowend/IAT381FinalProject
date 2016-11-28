package com.team5.iat381finalproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Blob;

public class ItemDetailsActivity extends AppCompatActivity {
    private String uid;
    private ImageView itemImageView;
    private TextView name, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemdetails);

        name = (TextView) findViewById(R.id.itemName);
        date = (TextView) findViewById(R.id.expirationDate);
        itemImageView = (ImageView) findViewById(R.id.itemImage);


        // get data for item
        uid = getIntent().getStringExtra("UID");
        name.setText(getIntent().getStringExtra("Name"));
        date.setText(getIntent().getStringExtra("Date"));

//        Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);
//        itemImageView.setImageBitmap(imageBitmap);
    }

    public void backButton (View view) {
        finish();
    }

    public void delete (View view) {
        Database db = new Database(this);
        db.RemoveData(uid);
        finish();
    }
}
