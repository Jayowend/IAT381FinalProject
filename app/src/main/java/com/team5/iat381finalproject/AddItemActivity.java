package com.team5.iat381finalproject;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
//import android.icu.text.SimpleDateFormat;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
//import android.icu.util.Calendar;
import java.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

public class AddItemActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText nameEditText, expireDateEditText;
    private Switch reminderSwitch;
    private Spinner reminderOption;
    private boolean reminderSet;
    private int year, month, day;
    private Database db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        expireDateEditText = (EditText) findViewById(R.id.expirationEditText);
        reminderSwitch = (Switch) findViewById(R.id.reminderSwitch);
        reminderOption = (Spinner) findViewById(R.id.reminderOptions);

        db = new Database(this);

        // grab current date in calendar
        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH); // NOTE: THIS IS THE INDEX, ADD 1 TO GET ACTUAL MONTH NUMBER
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // expiry date formatting with DatePickerDialog
        expireDateEditText.setOnClickListener(new View.OnClickListener() {
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
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.CANADA);
                        expireDateEditText.setText(sdf.format(calendar.getTime()));
                    }
                }, year, month, day);
                datePickerDialog.setTitle("Select expiry date");
                datePickerDialog.show();
            }
        });

        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                reminderSet = b;
            }
        });
        reminderOption.setOnItemSelectedListener(this);
    }


    public void addItem (View view) {
        String name = nameEditText.getText().toString();
        String date = (month + 1) + "/" + day + "/" + year;

        // convert and store bitmap into byte array for SQL database
        byte[] photo = new byte[0];
        if (imageBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            photo = stream.toByteArray();
        }

        // insert data into db
        long id = db.insertData(name, date, photo);

        // set reminder notification
        if (reminderSet) {
            // new intent
            Intent notificationIntent = new Intent("android.media.action.EXPIRE_NOTIFICATION");
            notificationIntent.addCategory("android.intent.category.DEFAULT");
            cursor = db.getData();
            notificationIntent.putExtra("uid", cursor.getCount());

            // create new pending intent
            PendingIntent pendingIntent = PendingIntent.getBroadcast(AddItemActivity.this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // schedule pending intent with AlarmManager
            Calendar nCalendar = Calendar.getInstance();
            nCalendar.set(year, month, day);
            String test = (String) reminderOption.getSelectedItem();

            String a = R.string.reminder_1week;
            switch (test) {
                case :
                    break;

            }
            nCalendar.add(Calendar.DAY_OF_MONTH, -7);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, nCalendar.getTimeInMillis(), pendingIntent);
            Log.i("debug", "pendingNotificaiton set");
        }

        if (id < 0)
            Toast.makeText(this, "Failed to add Item", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Item Added", Toast.LENGTH_SHORT).show();

        finish();
    }

    public void discardItem(View view) {
        finish();
    }

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    public void addPhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo_placeholder should go
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

    private Bitmap imageBitmap = null;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageView mImageView = (ImageView) findViewById(R.id.imageButton);
            mImageView.setImageBitmap(imageBitmap);
        }
    }

    private String mCurrentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CANADA).format(new Date());
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.i("test", ""+adapterView.getItemAtPosition(i));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
