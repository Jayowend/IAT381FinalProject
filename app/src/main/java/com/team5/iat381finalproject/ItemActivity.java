package com.team5.iat381finalproject;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class ItemActivity extends AppCompatActivity {
    TextView name, date;
    String uid;
    Database db;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);

        name = (TextView)findViewById(R.id.nameOfItem);
        date = (TextView)findViewById(R.id.expiryDate);

        name.setText(getIntent().getStringExtra("Name"));
        date.setText(getIntent().getStringExtra("Date"));
        uid = getIntent().getStringExtra("UID");

        db = new Database(this);
        cursor = db.getSelectedData(getIntent().getStringExtra("Name"));
    }

    public void backButton (View view) {
        finish();
    }

    public void delete (View view) {
        db.RemoveData(uid);
        finish();
    }
}
