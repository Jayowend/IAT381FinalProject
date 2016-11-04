package com.team5.iat381finalproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
//import android.icu.text.SimpleDateFormat;
import java.text.SimpleDateFormat;
//import android.icu.util.Calendar;
import java.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class AddActivity extends AppCompatActivity{

    EditText nameEdit, expiredateEdit;
    private int year, month, day;
    Database db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);
        nameEdit = (EditText) findViewById(R.id.Name_editText);
        db= new Database(this);
        expiredateEdit = (EditText) findViewById(R.id.date_editText);

        // grab current date in calendar
        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // expiry date formatting with DatePickerDialog
        expiredateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // new DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int pYear, int pMonth, int pDay) {
                        // save date for next picker opening
                        year = pYear;
                        month = pMonth;
                        day = pDay;

                        // format date
                        calendar.set(pYear, pMonth, pDay);
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
                        expiredateEdit.setText(sdf.format(calendar.getTime()));
                    }
                }, year, month, day);
                datePickerDialog.setTitle("Select expiry date");
                datePickerDialog.show();
            }
        });
    }

    public void discardItem(View view) {
        finish();
    }

    public void addItem (View view) {
        String name = nameEdit.getText().toString();

        String expireDate = expiredateEdit.getText().toString();
        long id = db.insertData(name, year, month, day);
//        long id = db.insertData(name,expireDate);
        if (id < 0)
        {
            Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public void addPhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
            Bitmap imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageView mImageView = (ImageView) findViewById(R.id.imageButton);
            mImageView.setImageBitmap(imageBitmap);
        }
    }


}
