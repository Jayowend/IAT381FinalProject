package com.team5.iat381finalproject;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ItemActivity extends AppCompatActivity {
    TextView name, date;
    String uid;
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
        uid = getIntent().getStringExtra("UID");
        name.setText(getIntent().getStringExtra("Name"));
        date.setText(getIntent().getStringExtra("Date"));
        cursor = db.getSelectedData(getIntent().getStringExtra("Name"));
//        cursor.move(1);
//        Toast.makeText(this, cursor.getColumnCount(), Toast.LENGTH_SHORT).show();

//        name.setText("textStyle");
//        date.setText(cursor.getString(2));


//        activeSensor = mList.get(Integer.parseInt(getIntent().getStringExtra("Order")));

    }





    public void backButton (View view) {
        finish();
    }

    public void delete (View view) {
        db.RemoveData(uid);

        finish();
    }
}
