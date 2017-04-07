package com.enableindia.texttospeechapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.texttospeechapp.R;

/**
 * Created by archds on 10/25/2015.
 */
public class SettingsActivity extends Activity {
    private String[] details = {"Network Connection","Set pitch/rate"};
    private String PREFS_NAME = "NETWORK_PREFS";
    private SharedPreferences sharedPreferences;
    protected void onCreate(Bundle savedInstanceState) {
        Context context = getBaseContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        getActionBar().setTitle("Settings");
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        CheckBox wifiCheckbox=(CheckBox)findViewById(R.id.wifiCheckbox);
        wifiCheckbox.setChecked(sharedPreferences.getBoolean("wifi",true));
        Log.d("SettingsActivity", "In sharedPreferences, isWifiOn=" + sharedPreferences.getBoolean("wifi",true));
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("SettingsActivity", "Entered onResume()");
        CheckBox wifiCheckbox=(CheckBox)findViewById(R.id.wifiCheckbox);


        wifiCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                                    @Override
                                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                        if (buttonView.isChecked()) {
                                                            //checked
                                                            Log.d("SettingsActivity", "wifi is on");
                                                            editor.putBoolean("wifi", true);

                                                        } else {
                                                            //not checked
                                                            Log.d("SettingsActivity", "wifi is off");
                                                            editor.putBoolean("wifi", false);


                                                        }
                                                        editor.commit();

                                                    }
                                                }
        );

        TextView ttsSettings=(TextView) findViewById(R.id.pitchRateButton);
        ttsSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ModifyPitchRate.class);
                startActivity(intent);
            }
        });
        /*            intent = new Intent(SettingsActivity.this, ModifyPitchRate.class);
                    startActivity(intent);
        */







        }


    }
