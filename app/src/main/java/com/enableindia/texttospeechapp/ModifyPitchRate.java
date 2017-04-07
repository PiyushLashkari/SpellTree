package com.enableindia.texttospeechapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.texttospeechapp.R;


public class ModifyPitchRate extends Activity {
    private String PREFS_NAME = "TTS_PREFS";
    private TTSManager ttsManager=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle("Set Pitch/Rate");
        setContentView(R.layout.modify_pitch_rate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ModifyPitchRate", "Entered onResume()");
        SharedPreferences sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        EditText pitchEditText = (EditText) findViewById(R.id.pitchEditText);
        EditText rateEditText = (EditText) findViewById(R.id.rateEditText);
        pitchEditText.setText(sharedpreferences.getString("pitch", "0.7"));
        rateEditText.setText(sharedpreferences.getString("rate", "0.7"));
        int pos = pitchEditText.getText().length();
        pitchEditText.setSelection(pos);

        ttsManager = new TTSManager();
        ttsManager.init(this);
        Log.d("ModifyPitchRate", "TTS Manager initialised");


        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String message=new String();
                Log.d("ModifyPitchRate", "Entered onClick of submit button");
                SharedPreferences sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                EditText pitchEditText = (EditText) findViewById(R.id.pitchEditText);
                EditText rateEditText = (EditText) findViewById(R.id.rateEditText);
                String pitch = pitchEditText.getText().toString();
                String rate = rateEditText.getText().toString();
                Log.d("ModifyPitchRate", "Pitch is " + pitch + ", Rate is " + rate);
                if(!pitch.isEmpty()&&pitch!=null&&!rate.isEmpty()&&rate!=null)
                {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("pitch", pitch);
                    editor.putString("rate", rate);
                    editor.commit();
                    message="Pitch is set to "+pitch+". Rate is set to "+rate;
                }
                else {
                    message="Please enter valid values";
                }
                pitch=sharedpreferences.getString("pitch", "0.7");
                rate=sharedpreferences.getString("rate", "0.7");
                ttsManager.setTtsPitch(Float.parseFloat(pitch));
                ttsManager.setTtsRate(Float.parseFloat(rate));

                ttsManager.initQueue(message);



            }
        });


    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ModifyPitchRate", "Entered onPause");
        ttsManager.shutDown();
    }
}


