package com.team5.iat381finalproject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class InventoryActivity extends Activity implements AdapterView.OnItemClickListener{
    private Database db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        populateList();
     }

    @Override
    public void onResume() {
        super.onResume();
        populateList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ItemDetailsActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    private void populateList() {
        db = new Database(this);
        cursor = db.getData();

        // For the cursor adapter, specify which columns go into which views
        String[] fromColumns = {Constants.NAME, Constants.EXP};
        int[] toViews = {R.id.nameTextView, R.id.dateTextView }; // The TextView in simple_list_item_1

        SimpleCursorAdapter myAdapter = new SimpleCursorAdapter(this, R.layout.list_row, cursor, fromColumns, toViews, 2);
        ListView myList = (ListView) findViewById(R.id.itemList);
        myList.setAdapter(myAdapter);
        myList.setOnItemClickListener(this);
    }
}

