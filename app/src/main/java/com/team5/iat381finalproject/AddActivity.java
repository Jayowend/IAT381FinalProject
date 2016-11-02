package com.team5.iat381finalproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {

    EditText nameEdit, expiredateEdit;
//    database db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);
        nameEdit = (EditText) findViewById(R.id.Name_editText);
        expiredateEdit = (EditText) findViewById(R.id.date_editText);
    }

    public void addItem (View view) {
        String name = nameEdit.getText().toString();
        String expireDate = expiredateEdit.getText().toString();

//        long id = db.insertData(name,expireDate);
//        if (id < 0)
//        {
//            Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
//        }
//        plantName.setText("");
//        plantType.setText("");
//        latinName.setText("");
//        location.setText("");
    }
}
