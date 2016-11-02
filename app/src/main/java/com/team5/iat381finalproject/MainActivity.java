package com.team5.iat381finalproject;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    //test
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    protected void buttonSuggestions(View view) {
        Uri webPage = Uri.parse("http://allrecipes.com/recipes/1947/everyday-cooking/quick-and-easy/");
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webPage);
        startActivity(webIntent);
    }
    protected void addItem(View view) {
        Intent intent  = new Intent(this, AddActivity.class);
        startActivity(intent);

    }
    protected void viewList(View view) {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }
    protected void options(View view) {

    }
}
