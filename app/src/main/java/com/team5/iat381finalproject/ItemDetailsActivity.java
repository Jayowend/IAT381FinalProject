package com.team5.iat381finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class ItemDetailsActivity extends AppCompatActivity {
    TextView name, date;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemdetails);

        name = (TextView)findViewById(R.id.itemName);
        date = (TextView)findViewById(R.id.expirationDate);

        name.setText(getIntent().getStringExtra("Name"));
        date.setText(getIntent().getStringExtra("Date"));
        uid = getIntent().getStringExtra("UID");
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
