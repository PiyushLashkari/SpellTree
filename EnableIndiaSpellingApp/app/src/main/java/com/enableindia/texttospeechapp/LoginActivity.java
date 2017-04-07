package com.cisco.texttospeechapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import android.widget.Button;

import com.example.texttospeechapp.R;

public class LoginActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        Button button1 = (Button) findViewById(R.id.btnGetStarted);
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(arg0.getContext(), ModeSelection.class);
                startActivity(intent);

            }

        });


    }

}
