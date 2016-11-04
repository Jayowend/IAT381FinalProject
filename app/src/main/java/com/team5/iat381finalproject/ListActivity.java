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
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        myList = (ListView)findViewById(R.id.itemList);
        db = new Database(this);


        Intent intent = getIntent();
        cursor = db.getData();
        // For the cursor adapter, specify which columns go into which views
        String[] fromColumns = {Constants.NAME, Constants.EXP};

        int[] toViews = {R.id.nameTextView, R .id.dateTextView }; // The TextView in simple_list_item_1

        myAdapter = new SimpleCursorAdapter(this, R.layout.list_row, cursor, fromColumns, toViews, 2);
        myList.setAdapter(myAdapter);
        myList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ItemActivity.class);
        intent.putExtra("Name",cursor.getString(1).toString());
        intent.putExtra("Date",cursor.getString(2).toString());
        startActivity(intent);

//        Cursor cursor = db.getData();
//        String object = db.getData();
//
//        Item item = (Item) object;
//        String name = item.getName()





//
////        String name = v.findViewById(R.id.nameTextView).toString();
//
//
////
//        Toast.makeText(this, "works", Toast.LENGTH_SHORT).show();
//
////        Toast.makeText(this, position +"", Toast.LENGTH_SHORT).show();
//
////
    }
}

