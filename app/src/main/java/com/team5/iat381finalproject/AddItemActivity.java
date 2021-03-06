package com.team5.iat381finalproject;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
//import android.icu.text.SimpleDateFormat;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
//import android.icu.util.Calendar;
import java.util.Calendar;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

public class AddItemActivity extends AppCompatActivity {
    private EditText nameEditText, expireDateEditText;
    private Switch reminderSwitch;
    private Spinner reminderOption;
    private boolean reminderSet;
    private int year, month, day;
    private Database db;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);

        mImageView = (ImageView) findViewById(R.id.imageButton);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        expireDateEditText = (EditText) findViewById(R.id.expirationEditText);
        reminderSwitch = (Switch) findViewById(R.id.reminderSwitch);
        reminderOption = (Spinner) findViewById(R.id.reminderOptions);

        // get user default prefs
        sharedPreferences = getSharedPreferences(getString(R.string.sharedprefs_filename), Context.MODE_PRIVATE);
        int reminderOptionSelectedItemPosition = sharedPreferences.getInt("reminderOptionSelectedItemPosition", -1);
        if(reminderOptionSelectedItemPosition != -1)
            reminderOption.setSelection(reminderOptionSelectedItemPosition, false);
        reminderSet = sharedPreferences.getBoolean("reminderSet", false);
        reminderSwitch.setChecked(reminderSet);
        editor = sharedPreferences.edit();

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
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.CANADA);
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
                editor.putBoolean("reminderSet", reminderSet);
                editor.apply();
            }
        });
        SpinnerInteractionListener reminderListener = new SpinnerInteractionListener();
        reminderOption.setOnTouchListener(reminderListener);
        reminderOption.setOnItemSelectedListener(reminderListener);
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
        int uid = (int) db.insertData(name, date, photo);

        // set reminder notification
        if (reminderSet) {
            // set notification calendar
            Calendar nCalendar = Calendar.getInstance();
            nCalendar.set(year, month, day);

            // offset notification date with user selected setting
            String reminderSelected = (String) reminderOption.getSelectedItem();
            int offset = 0;
            if (reminderSelected.equals(getString(R.string.reminder_1week))) {
                offset = -7;
            } else if (reminderSelected.equals(getString(R.string.reminder_3days))) {
                offset = -3;
            } else if (reminderSelected.equals(getString(R.string.reminder_1day))) {
                offset = -1;
            }
            nCalendar.add(Calendar.DAY_OF_MONTH, offset);

            // create new intent
            Intent notificationIntent = new Intent(getString(R.string.intent_action_name_expire_notification_reminder));
            notificationIntent.addCategory("android.intent.category.DEFAULT");
            notificationIntent.putExtra("uid", uid);
            editor.putInt("alarmtime_" + uid, reminderOption.getSelectedItemPosition());
            editor.apply();

            // create new pending intent and schedule pending intent with AlarmManager
            PendingIntent pendingIntent = PendingIntent.getBroadcast(AddItemActivity.this, uid, notificationIntent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, nCalendar.getTimeInMillis(), pendingIntent);
        }

        // set expiry notification, similar to above
        Intent notificationIntent = new Intent(getString(R.string.intent_action_name_expire_notification));
        notificationIntent.addCategory("android.intent.category.DEFAULT");
        notificationIntent.putExtra("uid", uid);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddItemActivity.this, uid, notificationIntent, 0);
        Calendar eCalendar = Calendar.getInstance();
        eCalendar.set(year, month, day);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, eCalendar.getTimeInMillis(), pendingIntent);

        // save barcode data into sharedprefs
        if (barcode != null) {
            editor.putString("upc_" + barcode + "_name", name);
            editor.putString("upc_" + barcode + "_expiry", date);
            editor.apply();
        }

        if (uid < 0)
            Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show();

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
            imageBitmap = decodeSampledBitmapFromResource(mCurrentPhotoPath, 500, 500);
            // full size image too resource intensive, need to scale down!
//            try {
//                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
            //params.width = imageBitmap.getWidth();
            params.height = Math.min(imageBitmap.getHeight(), 500);
            mImageView.setLayoutParams(params);
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
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // scale down photo
    // https://developer.android.com/training/displaying-bitmaps/load-bitmap.html
    public static Bitmap decodeSampledBitmapFromResource(String pathName, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    boolean override;
    // fixes bug spinner onItemSelected being fired on start
    // http://stackoverflow.com/a/28466568/2709339
    public class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

        boolean userSelect = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userSelect = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (userSelect) {
                // Your selection handling code here
                editor.putInt("reminderOptionSelectedItemPosition", reminderOption.getSelectedItemPosition());
                editor.apply();
                reminderSwitch.setChecked(true);
                userSelect = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private String barcode;
    // barcode scan to store repeated items in sharedprefs, for easier data entry (only name so far)
    public void scanBarcode(View view) {
        // Google Play Services Barcode API
        // https://developers.google.com/vision/barcodes-overview
        if (imageBitmap != null) {
            BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext())
                    .setBarcodeFormats(Barcode.UPC_A | Barcode.UPC_E | Barcode.EAN_8 | Barcode.EAN_13 | Barcode.PRODUCT)
                    .build();
            if (!detector.isOperational()) {
                return;
            }

            Frame frame = new Frame.Builder().setBitmap(imageBitmap).build();
            SparseArray<Barcode> barcodes = detector.detect(frame);

            try {
                if (barcodes.size() > 0) {
                    Barcode thisCode = barcodes.valueAt(0);
                    barcode = thisCode.rawValue;
                    ((TextView) findViewById(R.id.barcodeTextView)).setText("UPC: " + barcode);
                    ((TextView) findViewById(R.id.barcodeTextView)).setVisibility(View.VISIBLE);

                    // get sharedprefs data matching upc barcode
                    String itemName = sharedPreferences.getString("upc_" + barcode + "_name", null);
                    String expiryDate = sharedPreferences.getString("upc_" + barcode + "_expiry", null);
                    if (itemName != null) {
                      nameEditText.setText(itemName);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new SimpleDateFormat("MM/dd/yyyy", Locale.CANADA).parse(expiryDate));
                        year = calendar.get(Calendar.YEAR);
                        month = calendar.get(Calendar.MONTH);
                        day = calendar.get(Calendar.DAY_OF_MONTH);
                        expireDateEditText.setText(expiryDate);
                      Toast.makeText(this, "Barcode found! Data updated", Toast.LENGTH_SHORT).show();
                    } else {
                        ((TextView) findViewById(R.id.barcodeTextView)).append(" (new)");
                        Toast.makeText(this, "Barcode detected but no data found\nSave this item first", Toast.LENGTH_LONG).show();
                        nameEditText.setText("");
                        expireDateEditText.setText("");
                    }
                    if (!sharedPreferences.getBoolean("barcodePhotoOn", false)) {
                        mImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.photo_placeholder));
                        imageBitmap = null;
                    }
                } else {
                    mImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.photo_placeholder));
                    imageBitmap = null;
                    barcode = null;
                    ((TextView) findViewById(R.id.barcodeTextView)).setVisibility(View.INVISIBLE);
                    Toast.makeText(this, "Barcode not recognized\nPlease try again", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Tap the picture & take a photo of the barcode first!", Toast.LENGTH_LONG).show();
        }
    }
}
