package com.enableindia.texttospeechapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;

import com.example.texttospeechapp.R;


public class NetworkModeSelection extends Activity {

    private String PREFS_NAME = "NETWORK_PREFS";
    private CheckBox wifiCheckbox,dataCheckbox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("NetworkModeSelection", "Entered onCreate)");
        setContentView(R.layout.network_selection);
        wifiCheckbox=(CheckBox)findViewById(R.id.wifiCheckbox);
        dataCheckbox=(CheckBox)findViewById(R.id.dataCheckbox);

    }

    @Override
    protected void onResume()
    {
     super.onResume();
        Log.d("NetworkModeSelection", "Entered onResume()");
        Boolean isWifiOn, isDataOn;
        SharedPreferences sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        isWifiOn=sharedpreferences.getBoolean("wifi",true);
        isDataOn=sharedpreferences.getBoolean("data",true);
        wifiCheckbox.setChecked(isWifiOn);
        dataCheckbox.setChecked(isDataOn);
        Log.d("NetworkModeSelection","In sharedPreferences, isWifiOn="+isWifiOn);
        Log.d("NetworkModeSelection","In sharedPreferences, isDataOn="+isDataOn);

    }

     @Override
    protected void onPause(){
         super.onPause();
         Log.d("NetworkModeSelection","Entered onPause()");
         SharedPreferences sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
         wifiCheckbox=(CheckBox)findViewById(R.id.wifiCheckbox);
         dataCheckbox=(CheckBox)findViewById(R.id.dataCheckbox);
         SharedPreferences.Editor editor = sharedpreferences.edit();
         Boolean isWifiOn=wifiCheckbox.isChecked();
         Boolean isDataOn=dataCheckbox.isChecked();
         Log.d("NetworkModeSelection","wifi isChecked="+isWifiOn);
         Log.d("NetworkModeSelection","data isChecked="+isDataOn);
         editor.putBoolean("wifi", isWifiOn);
         editor.putBoolean("data",isDataOn);

         editor.commit();

     }


}
