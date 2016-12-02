package com.team5.iat381finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    private TextView ringtoneTitle;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences(getString(R.string.sharedprefs_filename), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        ringtoneTitle = (TextView) findViewById(R.id.ringtoneTitle);
        String stringUri = sharedPreferences.getString(getString(R.string.settings_notification_tone_uri), "Ringtone Title");
        Uri uri = Uri.parse(stringUri);
        ringtoneTitle.setText(RingtoneManager.getRingtone(this, uri).getTitle(this));
        Spinner dropdown = (Spinner) findViewById(R.id.spinner);
        dropdown.setEnabled(false);

        // store settings in sharedprefs
        Switch notificationSwitch = (Switch) findViewById(R.id.notificationSwitch);
        notificationSwitch.setChecked(sharedPreferences.getBoolean("notificationsOn", true));
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("notificationsOn", b);
                editor.apply();
            }
        });

        Switch vibrationSwitch = (Switch) findViewById(R.id.vibrationSwitch);
        vibrationSwitch.setChecked(sharedPreferences.getBoolean("vibrationOn", false));
        vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("vibrationOn", b);
                editor.apply();
            }
        });

        Switch soundSwitch = (Switch) findViewById(R.id.soundSwitch);
        soundSwitch.setChecked(sharedPreferences.getBoolean("soundOn", false));
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("soundOn", b);
                editor.apply();
            }
        });

        Switch barcodePhotoSwitch = (Switch) findViewById(R.id.barcodePhotoSwitch);
        barcodePhotoSwitch.setChecked(sharedPreferences.getBoolean("barcodePhotoOn", false));
        barcodePhotoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("barcodePhotoOn", b);
                editor.apply();
            }
        });


    }

    public void selectRingtone(View view) {
        // notification ringtone set
        // http://stackoverflow.com/questions/2724871/how-to-bring-up-list-of-available-notification-sounds-on-android
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(sharedPreferences.getString(getString(R.string.settings_notification_tone_uri), "Ringtone Title")));
        this.startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == 1)
        {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null)
            {
                // store ringtone uri in sharedprefs
                ringtoneTitle.setText(RingtoneManager.getRingtone(this, uri).getTitle(this));
                editor.putString(getString(R.string.settings_notification_tone_uri), uri.toString());
            }
            else
            {
                ringtoneTitle.setText("Silent");
                editor.putString(getString(R.string.settings_notification_tone_uri), null);
            }
            editor.apply();
        }
    }
}
