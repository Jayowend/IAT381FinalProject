package com.team5.iat381finalproject;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
    }

    public void buttonSuggestions(View view) {
        Uri webPage = Uri.parse("http://allrecipes.com/recipes/1947/everyday-cooking/quick-and-easy/");
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webPage);
        startActivity(webIntent);
    }

    public void addItem(View view) {
        Intent intent  = new Intent(this, AddItemActivity.class);
        startActivity(intent);
    }

    public void viewList(View view) {
        Intent intent = new Intent(this, InventoryActivity.class);
        startActivity(intent);
    }

    //public void options(View view) { }
}
