package com.team5.iat381finalproject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class ListActivity extends Activity implements AdapterView.OnItemClickListener{
    ListView myList;
    Database db;
    SimpleCursorAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        myList = (ListView)findViewById(R.id.itemList);
        db = new Database(this);
        Cursor cursor;

        Intent intent = getIntent();
        cursor = db.getData();
        // For the cursor adapter, specify which columns go into which views
        String[] fromColumns = {Constants.NAME, Constants.EXP};

//        int[] toViews = {R.id.plantNameEntry, R .id.plantTypeEntry, R.id.latinNameEntry, R.id.locationEntry }; // The TextView in simple_list_item_1

//        myAdapter = new SimpleCursorAdapter(this, R.layout.list_row, cursor, fromColumns, toViews, 2);
        myList.setAdapter(myAdapter);
        myList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}

