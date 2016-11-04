package com.team5.iat381finalproject;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ItemActivity extends AppCompatActivity {
    TextView name, date;
    Database db;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);
        db = new Database(this);
        name = (TextView)findViewById(R.id.nameOfItem);
        date = (TextView)findViewById(R.id.expirarydate);
        Intent intent = getIntent();
        name.setText(getIntent().getStringExtra("Name"));
        cursor = db.getSelectedData(getIntent().getStringExtra("Name"));
//        name.setText("test");
//        date.setText(cursor.getString(2));


//        activeSensor = mList.get(Integer.parseInt(getIntent().getStringExtra("Order")));

    }





    public void backButton (View view) {
        finish();
    }
    public void delete (View view) {
        finish();
    }
}
