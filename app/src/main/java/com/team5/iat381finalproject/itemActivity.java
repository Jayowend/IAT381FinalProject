package com.team5.iat381finalproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ItemActivity extends AppCompatActivity {
    TextView name, date;
    Database db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);
        db = new Database(this);
        name = (TextView)findViewById(R.id.nameOfItem);
        date = (TextView)findViewById(R.id.expirarydate);
        

//        activeSensor = mList.get(Integer.parseInt(getIntent().getStringExtra("Order")));

    }





    public void backButton (View view) {
        finish();
    }
    public void delete (View view) {
        finish();
    }
}
