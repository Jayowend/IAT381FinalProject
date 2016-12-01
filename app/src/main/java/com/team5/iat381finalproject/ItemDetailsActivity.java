package com.team5.iat381finalproject;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ItemDetailsActivity extends AppCompatActivity {
    private Database db;
    private Cursor cursor;
    private Switch reminderSwitch;
    private Spinner reminderOption;
    private int uid;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private PendingIntent pendingIntent;
    private boolean setEnableToast;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemdetails);

        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        sharedPreferences = getSharedPreferences(getString(R.string.sharedprefs_filename), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        TextView name = (TextView) findViewById(R.id.itemName);
        final TextView date = (TextView) findViewById(R.id.expirationDate);
        TextView isExpired = (TextView) findViewById(R.id.isExpired);
        ImageView itemImageView = (ImageView) findViewById(R.id.itemImage);
        reminderSwitch = (Switch) findViewById(R.id.reminderSwitch);
        reminderOption = (Spinner) findViewById(R.id.reminderOptions);

        uid = getIntent().getIntExtra("uid", -1);
        db = new Database(this);

        // set expired if coming from expired notification
        if (getString(R.string.intent_action_name_expire_notification).equals(getIntent().getAction())) {
            db.setExpired("true", Integer.toString(uid));
        }

        cursor = db.getData(Constants.UID, Integer.toString(uid));
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            // set data
            name.setText(cursor.getString(cursor.getColumnIndexOrThrow(Constants.NAME)));
            date.setText(cursor.getString(cursor.getColumnIndexOrThrow(Constants.EXP)));

            // set expired if item expired
            boolean expired = false;
            try {
                if (!expired && new SimpleDateFormat("MM/dd/yy", Locale.CANADA).parse(date.getText().toString()).before(Calendar.getInstance().getTime())) {
                    db.setExpired("true", Integer.toString(uid));
                    expired = true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (cursor.getString(cursor.getColumnIndexOrThrow(Constants.EXPIRED)).equals("true") || expired) {
                isExpired.setVisibility(View.VISIBLE);
                reminderSwitch.setChecked(false);
                reminderSwitch.setEnabled(false);
                reminderOption.setEnabled(false);
            }

            byte[] imageByte = cursor.getBlob(cursor.getColumnIndexOrThrow(Constants.IMG));
            if (imageByte.length > 0) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                itemImageView.setImageBitmap(imageBitmap);
            }
        } else {
            finish();
        }

        // check for existing reminder notification
        Intent notificationIntent = new Intent(getString(R.string.intent_action_name_expire_notification_reminder));
        notificationIntent.addCategory("android.intent.category.DEFAULT");

        // matching pendingIntent to the one scheduled
        pendingIntent = PendingIntent.getBroadcast(this, uid, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null) {
            reminderSwitch.setChecked(true);
            setEnableToast = true;
            reminderOption.setSelection(sharedPreferences.getInt("alarmtime_" + uid, reminderOption.getSelectedItemPosition()), false);
        } else {
            reminderSwitch.setChecked(false);
            setEnableToast = false;
            reminderOption.setSelection(sharedPreferences.getInt("reminderOptionSelectedItemPosition", reminderOption.getSelectedItemPosition()), false);
        }

        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                setEnableToast = b;
                // enable reminder notification
                if (b) {
                    // set notification calendar
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.CANADA);
                    try {
                        calendar.setTime(sdf.parse(date.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar nCalendar = Calendar.getInstance();
                    nCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

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

                    // new intent
                    Intent notificationIntent = new Intent(getString(R.string.intent_action_name_expire_notification_reminder));
                    notificationIntent.addCategory("android.intent.category.DEFAULT");
                    notificationIntent.putExtra("uid", uid);
                    editor.putInt("alarmtime_" + uid, reminderOption.getSelectedItemPosition());
                    editor.apply();

                    // create new pending intent and schedule pending intent with AlarmManager
                    pendingIntent = PendingIntent.getBroadcast(ItemDetailsActivity.this, uid, notificationIntent, 0);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, nCalendar.getTimeInMillis(), pendingIntent);
                    toast.setText("Reminder notification Enabled");
                    toast.show();

                // disable reminder notification
                } else {
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    pendingIntent.cancel();
                    editor.remove("alarmtime_" + uid);
                    editor.apply();

                    // clear current notification if already popped
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.cancel(uid);
                    toast.setText("Reminder notification Disabled");
                    toast.show();
                }
            }
        });
        SpinnerInteractionListener reminderListener = new SpinnerInteractionListener();
        reminderOption.setOnTouchListener(reminderListener);
        reminderOption.setOnItemSelectedListener(reminderListener);

        // clear pendingIntent if (user enters through notification || notification cleared)
        if (getString(R.string.intent_action_name_expire_notification_reminder).equals(getIntent().getAction()) || sharedPreferences.getInt("alarmtime_" + uid, -1) == -1) {
            reminderSwitch.setChecked(false);
            setEnableToast = false;
        }
    }

    public void backButton (View view) {
        finish();
    }

    public void delete (View view) {
        reminderSwitch.setChecked(false);
        db.RemoveData(Integer.toString(uid));
        toast.setText("Item deleted");
        toast.show();
        finish();
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
                boolean currentState = setEnableToast;
                reminderSwitch.setChecked(false);
                reminderSwitch.setChecked(true);
                if (currentState) {
                    toast.setText("Reminder notification Updated");
                    toast.show();
                } else {
                    toast.setText("Reminder notification Enabled");
                    toast.show();
                }
                userSelect = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}
