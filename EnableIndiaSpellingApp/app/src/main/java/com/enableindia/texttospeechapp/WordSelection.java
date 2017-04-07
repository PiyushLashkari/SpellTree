package com.cisco.texttospeechapp;

import com.example.texttospeechapp.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class WordSelection extends Activity {

    TTSManager ttsManager = null;
    private GestureDetectorCompat gestureDetectorCompat;
    String[] words;
    int pos=1,clicks=0;
    String currentWord;
    Boolean correct=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("sample", "Entered onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    //    gestureDetectorCompat = new GestureDetectorCompat(this, new MyGestureListener());
        ttsManager = new TTSManager();
        ttsManager.init(this);
        words = new String[]{"apple", "bat", "cat"};

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("sample", "Entered onResume()");
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        Button speakNowButton = (Button) findViewById(R.id.btnReplay);
        currentWord = words[pos - 1].toUpperCase();
        ttsManager.initQueue(currentWord);
        speakNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // String currentWord = editText.getText().toString();
                pos = getIntent().getIntExtra("pos", 1);
                Log.d("position is", "pos is" + pos);
                currentWord = currentWord.toUpperCase();
                ttsManager.initQueue(currentWord);
                //To spell word
               /* for (int i = 0; i < currentWord.length(); i++)
                    ttsManager.addQueue((Character.toString(currentWord.charAt(i))));*/

            }
        });

        Button checkButton = (Button) findViewById(R.id.btnCheck);
        final ImageButton rightButton = (ImageButton) findViewById(R.id.rightButton);
        final ImageButton leftButton=(ImageButton) findViewById(R.id.leftButton);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                clicks++;
                EditText editText = (EditText) findViewById(R.id.txtContent);
                String textEntered = editText.getText().toString();
                if(textEntered.equalsIgnoreCase(currentWord))
                    correct=true;
                if (correct!= true && clicks<=3 )
                {
                  //  Toast.makeText(getBaseContext(), "It's wrong. " + (3 - clicks) + " chances left", Toast.LENGTH_SHORT).show();
                    ttsManager.initQueue("It's wrong. ");
                            if( (3 - clicks)==1)
                                ttsManager.addQueue((1)+" chance remaining");
                            else
                                ttsManager.addQueue((3-clicks)+" chances remaining");
                    if(3-clicks<=0) {
                        ttsManager.addQueue(("Spelling is "));
                        for (int i = 0; i < currentWord.length(); i++)
                        {
                            ttsManager.addQueue((Character.toString(currentWord.charAt(i))));
                            ttsManager.addQueue("Type word again");
                        }

                    }
                }

                else if (correct==true) {
                   // Toast.makeText(getBaseContext(), "It's Correct", Toast.LENGTH_SHORT).show();
                    ttsManager.initQueue("It's correct, click on next");
                    rightButton.setBackgroundResource(R.drawable.right_button_bright);
                }
         }
        });



        speakNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // String currentWord = editText.getText().toString();
                pos = getIntent().getIntExtra("pos", 1);
                Log.d("position is", "pos is" + pos);
                currentWord = words[pos - 1];
                ttsManager.initQueue(currentWord);
                //To spell word
               /* for (int i = 0; i < currentWord.length(); i++)
                    ttsManager.addQueue((Character.toString(currentWord.charAt(i))));*/

            }
        });
       // haven't enables as of now
       /*leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(correct==true) {
                    Intent intent = new Intent(
                            WordSelection.this, WordSelection.class);
                    Log.d("sample", "pos-toRight is " + pos);
                    intent.putExtra("pos", pos-1);
                    startActivity(intent);
                }
            }
        });
*/
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(correct==true) {
                    Intent intent = new Intent(
                            WordSelection.this, WordSelection.class);
                    Log.d("sample", "pos-toRight is " + pos);
                    intent.putExtra("pos", pos + 1);
                    startActivity(intent);
                }
            }
        });
    }
   //To enable swipe left and right
  /*  @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        //handle 'swipe left' action only

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            if (correct == true) {
                if (event2.getX() < event1.getX()) {
                    //switch another activity
                    Intent intent = new Intent(
                            WordSelection.this, WordSelection.class);
                    WordSelection.this.overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);

                    Log.d("sample", "pos-toRight is " + pos);
                    intent.putExtra("pos", pos + 1);
                    startActivity(intent);


                } else if (event2.getX() > event1.getX()) {
                    Intent intent = new Intent(
                            WordSelection.this, WordSelection.class);
                    WordSelection.this.overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
                    Log.d("sample", "pos-toLeft is " + pos);
                    intent.putExtra("pos", pos - 1);
                    startActivity(intent);


                }

                return true;
            }
            else
                return false;
        }

    }
*/

    @Override
      protected void onPause() {
        super.onPause();
        Log.d("sample", "Entered onPause for activity pos " + pos);
        finish();

    }


    /**
     * Releases the resources used by the TextToSpeech engine.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("sample", "Entered onDestroy");
        ttsManager.shutDown();
    }

}

