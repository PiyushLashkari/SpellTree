package com.enableindia.texttospeechapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.enableindia.texttospeechapp.SimpleGestureFilter.SimpleGestureListener;
import com.example.texttospeechapp.R;

import java.util.List;

/**
 * Created by archds on 5/27/2015.
 */
public class LearnMode extends Activity implements SimpleGestureListener {

    TTSManager ttsManager = null;
    private GestureDetectorCompat gestureDetectorCompat;
    private SimpleGestureFilter detector;
    private int MAX_RETRIES = 3;
    private String PREFS_NAME_TTS = "TTS_PREFS";
    private String PREFS_NAME_NETWORK = "NETWORK_PREFS";

    // String words1,words2,sentences1,sentences2,
    static int index = 0, size = 0;
    static int nRetryCount = 0;
    static String currentWord, currentSentence;
    boolean correct = false, isEmpty = true;
    static String categoryName;
    static String exerciseName;
    static String exerciseID;
    static List<Content> contents = null;
    public static boolean bLearnComplete = false;


    public static boolean bCloseActivity;

    AccessibilityManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Learn Mode", "Entered onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learn_mode);
        getActionBar().setTitle("Learn Mode");
        am = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);


    /* if (android.os.Build.VERSION.SDK_INT > 14) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/
        //  gestureDetectorCompat = new GestureDetectorCompat(this, new MyGestureListener());
        // Detect touched area
        detector = new SimpleGestureFilter(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Learn Mode", "Entered onResume()");

        SharedPreferences sharedpreferences = getSharedPreferences(PREFS_NAME_TTS, Context.MODE_PRIVATE);
        ttsManager = new TTSManager();
        ttsManager.init(this);
        String pitch=sharedpreferences.getString("pitch", "0.7");
        String rate=sharedpreferences.getString("rate", "0.7");
        Log.d("LearnMode", "Pitch is " + pitch);
        Log.d("LearnMode", "Rate is " + rate);
        ttsManager.setTtsPitch(Float.parseFloat(pitch));
        ttsManager.setTtsRate(Float.parseFloat(rate));
        Log.d("Learn Mode", "TTS Manager initialised");


        EditText editText = (EditText) findViewById(R.id.txtContent);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        correct = true;

        Intent prevIntent = getIntent();
        categoryName = prevIntent.getStringExtra("category_name");
        exerciseName = prevIntent.getStringExtra("exercise_name");
        exerciseID = prevIntent.getStringExtra("exercise_id");

        if (contents == null) {
            // Getting the data from the DB.
            EnableIndiaDataAdapter mDbHelper = new EnableIndiaDataAdapter(this);
            mDbHelper.open();
            Log.d("Learn Mode", "Loading contents");
            contents = mDbHelper.getContent(exerciseID);
            mDbHelper.close();
        } else {
            if (contents.size() > 0) {
                if (contents.get(0).Subcategory_Id != Integer.parseInt(exerciseID)) {

                    if (contents != null) {
                        contents.clear();
                        contents = null;
                    }

                    // Getting the data from the DB for the new module
                    EnableIndiaDataAdapter mDbHelper = new EnableIndiaDataAdapter(this);
                    mDbHelper.open();
                    Log.d("Learn Mode", "Loading contents");
                    contents = mDbHelper.getContent(exerciseID);
                    mDbHelper.close();

                }
            }
        }

        ttsManager.initQueue(exerciseName);

        bLearnComplete = false;

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        Button replayButton = (Button) findViewById(R.id.btnReplay);
        size = contents.size();
        if (size > 0) {
            Log.d("Learn Mode", "Contents size is: " + size);
            currentWord = contents.get(index).Content_String.trim().toUpperCase();
            if (contents.get(index).Content_Example != null) {
                currentSentence = contents.get(index).Content_Example.toUpperCase();
            } else {
                currentSentence = "";
            }
            replay();
        } else {
            ttsManager.initQueue("There is no Content in this Module, Please select another Module");
        }
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // String currentWord = editText.getText().toString();
                replay();
            }
        });


        Button meaningButton = (Button) findViewById(R.id.meaningButton);
        meaningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showMeaning();
            }
        });

        ImageButton leftButton = (ImageButton) findViewById(R.id.leftButton);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                previousWord();
            }
        });

        ImageButton rightButton = (ImageButton) findViewById(R.id.rightButton);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.d("Learn Mode", "Clicked on right button for index: " + index);
                Log.d("Learn Mode", "Inside RightButton Listener");
                nextWord();
            }
        });


    }

    void replay() {
        Log.d("Learn Mode", "Inside replay()");
        Log.d("Learn Mode", "Index: " + index);

        if (!bLearnComplete) {
            if (index < contents.size()) {
                currentWord = contents.get(index).Content_String.trim().toUpperCase();
                Log.d("Learn Mode", "Current Word: " + currentWord);
                if (contents.get(index).Content_Example != null) {
                    currentSentence = contents.get(index).Content_Example.toUpperCase();
                } else {
                    currentSentence = "";
                }
            }

            if (!currentSentence.equals("")) {
                Log.d("Learn Mode", "Current Sentence: " + currentSentence);

                ttsManager.initQueue("Spell");
                ttsManager.addQueue(currentWord);
                ttsManager.addQueue(" as used in the sentence ");
                ttsManager.addQueue(currentSentence);
            } else {
                ttsManager.initQueue("Spell the Word" + currentWord);
            }

            if (nRetryCount > 0) {
                ttsManager.addQueue("Spelling of " + currentWord + " is");
                //To spell word
                for (int i = 0; i < currentWord.length(); i++) {
                    String sChar = (Character.toString(currentWord.charAt(i)));
                    if (!sChar.equalsIgnoreCase(" ")) {
                        ttsManager.addQueue(sChar);
                    } else {
                        ttsManager.addQueue("space");
                    }

                }
            }
            correct = false;

        } /*else {
            ttsManager.initQueue("This module has been completed.");
        }*/
    }
/*

    void checkSpelling() {
        Log.d("Learn Mode", "inside checkSpelling()");
       */
/* EditText editText = (EditText) findViewById(R.id.txtContent);
        String textEntered = editText.getText().toString().trim();
        if (!textEntered.trim().isEmpty()) {
            Log.d("Text: ", textEntered);
        } else {
            Log.d("Learn Mode", "Text: <Empty>");
        }
        Log.d("Current Word: ", currentWord);
        Log.d("correct: ", " " + correct);

       *//*


      */
/*  if (textEntered.equalsIgnoreCase(currentWord)) {
            correct = true;
        } else {
            correct = false;
        }
        Log.d("correct: ", " " + correct);


        //  Toast.makeText(getBaseContext(), "It's wrong. " + (3 - clicks) + " chances left", Toast.LENGTH_SHORT).show();
        if (!correct) {
            ttsManager.initQueue("It's wrong. ");
            nRetryCount++;

            ttsManager.addQueue(("Spelling is "));
            for (int i = 0; i < currentWord.length(); i++) {
                String sChar = ((Character.toString(currentWord.charAt(i))));
                if (!sChar.equalsIgnoreCase(" ")) {
                    ttsManager.addQueue(sChar);
                } else {
                    ttsManager.addQueue("space");
                }
            }
        } else {
            // Toast.makeText(getBaseContext(), "It's Correct", Toast.LENGTH_SHORT).show();
            ImageButton rightButton = (ImageButton) findViewById(R.id.rightButton);
            ttsManager.initQueue("It's correct. Click on Next");
            rightButton.setBackgroundResource(R.drawable.right_button_bright);
            //    rightButton.performClick();
        }
      *//*
  //editText.setText("");
    }
*/

    void nextWord() {
        Log.d("Learn Mode", "inside nextWord()");
        EditText editText = (EditText) findViewById(R.id.txtContent);
        String textEntered = editText.getText().toString().trim();


        if (!bLearnComplete) {
            Log.d("Current Word: ", currentWord);
            Log.d("correct: ", " " + correct);

            if (textEntered.trim().equalsIgnoreCase(currentWord)) {
                editText.setText("");
                Log.d("Learn Mode", "Spelling Entered is " + textEntered);
                correct = true;
                isEmpty = false;
                // Move on to the next word if there were 3 retries.
                ttsManager.initQueue("Spelling is correct, moving on to the next word");

                // Starting fresh
                index++;
                nRetryCount = 0;
                correct = false;
                isEmpty = true;

                if (index < size) {
                    try {
                        while (ttsManager.isTtsSpeaking())
                            Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    replay();

                } else {
                    Log.d("Learn Mode", "Index is greater than or equal to list size");

                    Log.d("Learn Mode", "nextWord (index>=size): This module has been completed.");

                    ttsManager.initQueue("You have completed this Module");
                    //ttsManager.addQueue("Click on back to go to categories list");
                    // TO hide the virtual keyboard

                    // Disabling the Buttons
                    ImageButton btnNext = (ImageButton) findViewById(R.id.rightButton);
                    ImageButton btnPrev = (ImageButton) findViewById(R.id.leftButton);
                    editText.setText("");
                    btnNext.setEnabled(false);
                    btnPrev.setEnabled(false);
                    bLearnComplete = true;

                    try {
                        while (ttsManager.isTtsSpeaking())
                            Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.d("Learn Mode", "Interrupted exception caught");
                    }


                    // Updating the DB
                    // TODO: Check whether the status has to be updated as 'Partial Completion'
                    // if the student has skipped a word. [That is, entered the wrong spelling thrice]

                    EnableIndiaDataAdapter mDbHelper = new EnableIndiaDataAdapter(LearnMode.this.getBaseContext());
                    mDbHelper.open();

                    mDbHelper.writeTestScore(exerciseID, "Done", "Learn");
                    mDbHelper.close();

                    if (ttsManager != null) {
                        ttsManager.shutDown();

                    }

                    finish();
                }
            } else {
                if (textEntered.isEmpty()) {
                    ttsManager.initQueue("Text box is empty, please enter the word");
                    Log.d("Learn Mode", "Spelling Entered is <Empty>");
                } else {
                    editText.setText("");
                    nRetryCount++;

                    Log.d("Learn Mode", "Spelling Entered is " + textEntered);
                    String sAttemptsLeft;
                    if ((MAX_RETRIES - nRetryCount) > 0) {
                        if (MAX_RETRIES - nRetryCount == 1)
                            sAttemptsLeft = "You have " + (MAX_RETRIES - nRetryCount) + " attempt left.";
                        else
                            sAttemptsLeft = "You have " + (MAX_RETRIES - nRetryCount) + " attempts left.";
                        Log.d("Learn Mode", MAX_RETRIES - nRetryCount + " attempts left");
                        ttsManager.initQueue("Spelling is wrong");
                        ttsManager.addQueue(sAttemptsLeft);
                        ttsManager.addQueue(("Spelling is "));
                        for (int i = 0; i < currentWord.length(); i++) {
                            String sChar = ((Character.toString(currentWord.charAt(i))));
                            if (!sChar.equalsIgnoreCase(" ")) {
                                ttsManager.addQueue(sChar);
                            } else {
                                ttsManager.addQueue("space");
                            }
                        }
                    } else {
                        ttsManager.initQueue("Three attempts are over ");
                        ttsManager.addQueue("Spelling is wrong, but still moving on to the next word");
                        // Starting fresh
                        index++;
                        nRetryCount = 0;
                        correct = false;
                        isEmpty = true;

                        if (index < size) {
                            try {
                                while (ttsManager.isTtsSpeaking())
                                    Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                Log.d("Learn Mode", "Interrupted exception caught");
                            }
                            replay();

                            editText = (EditText) findViewById(R.id.txtContent);
                            editText.setText("");

                        } else {
                            Log.d("Learn Mode", "Index is greater than or equal to list size");
                            // Disabling the Buttons
                            ImageButton btnNext = (ImageButton) findViewById(R.id.rightButton);
                            ImageButton btnPrev = (ImageButton) findViewById(R.id.leftButton);
                            editText.setText("");
                            btnNext.setEnabled(false);
                            btnPrev.setEnabled(false);
                            bLearnComplete = true;

                            Log.d("Learn Mode", "nextWord (index>=size, after wrong attempt): This module has been completed.");
                            ttsManager.initQueue("You have completed this Module");
                            //ttsManager.addQueue("Click on back to go to categories list");

                            try {
                                while (ttsManager.isTtsSpeaking())
                                    Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                Log.d("Learn Mode", "Interrupted exception caught");
                            }


                            // Updating the DB
                            // TODO: Check whether the status has to be updated as 'Partial Completion'
                            // if the student has skipped a word. [That is, entered the wrong spelling thrice]

                            EnableIndiaDataAdapter mDbHelper = new EnableIndiaDataAdapter(LearnMode.this.getBaseContext());
                            mDbHelper.open();

                            mDbHelper.writeTestScore(exerciseID, "Done", "Learn");
                            mDbHelper.close();


                            if (ttsManager != null) {
                                ttsManager.shutDown();

                            }

                            finish();
                        }
                    }
                }
            }


        } else {
            Log.d("Learn Mode", "nextWord(blearncomplete=true): This module has been completed.");
            ttsManager.initQueue("You have completed this Module.");
            //ttsManager.addQueue("Click on back to go to categories list");

            try {
                while (ttsManager.isTtsSpeaking())
                    Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.d("Learn Mode", "Interrupted exception caught");
            }

            if (ttsManager != null) {
                ttsManager.shutDown();

            }

            finish();
        }
    }

    void previousWord() {

        if (!bLearnComplete) {

            Log.d("Learn Mode", "inside previousWord()");
            index--;

            // Starting fresh
            nRetryCount = 0;

            if (index >= 0) {
                Log.d("Learn Mode", "New Index: " + index);
                currentWord = contents.get(index).Content_String.trim().toUpperCase();
                if (contents.get(index).Content_Example != null) {
                    currentSentence = contents.get(index).Content_Example.toUpperCase();
                } else {
                    currentSentence = "";
                }
                Log.d("Learn Mode", "Current Word: " + currentWord);
                if (!currentSentence.equals("")) {
                    Log.d("Learn Mode", "Current Sentence: " + currentSentence);
                }
                EditText editText = (EditText) findViewById(R.id.txtContent);
                editText.setText(" ");
                replay();

            } else {
                index = 0;
                Log.d("Learn Mode", "This is the first word");
                ttsManager.initQueue("You have reached first word");
            }
        } else {
            // Module complete
            ttsManager.initQueue("This module has been completed.");
        }
    }

    void showMeaning() {
        Log.d("LearnMode", "Inside showMeaning()");
        String sMeaning = "";
        Boolean isWifiOn;
        SharedPreferences sharedpreferences = getSharedPreferences(PREFS_NAME_NETWORK, Context.MODE_PRIVATE);
        isWifiOn = sharedpreferences.getBoolean("wifi", true);


        if (!bLearnComplete)
        {
            if (contents.get(index).Content_Meaning != null) {
                Log.d("LearnMode", "Yes, meaning is stored in database");
                sMeaning = contents.get(index).Content_Meaning.toUpperCase();
                ttsManager.initQueue("The meaning of the word ");
                ttsManager.addQueue(contents.get(index).Content_String);
                ttsManager.addQueue("is :");
                ttsManager.addQueue(sMeaning);
            } else {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                android.net.NetworkInfo wifi = cm
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                android.net.NetworkInfo datac = cm
                        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (isWifiOn) {
                    Log.d("LearnMode", "Use only wifi = " + isWifiOn);
                    if (wifi != null && wifi.isConnected()) {
                        Log.d("LearnMode", "wifi is On in this device");
                        cm.setNetworkPreference(ConnectivityManager.TYPE_WIFI); //Always set network prefernce as WiFi
                        startMeaningActivity();
                    } else {
                        Log.d("LearnMode", "WiFi is off in this device");
                        ttsManager.addQueue("Please turn on WiFi ");

                    }
                } else if ((wifi != null && wifi.isConnected()) || (datac != null && datac.isConnected())) {
                    ttsManager.addQueue("Please turn on  WiFi or data connection ");
                    cm.setNetworkPreference(ConnectivityManager.TYPE_WIFI); //Always set network prefernce as WiFi
                    startMeaningActivity();
                } else
                    ttsManager.addQueue("No network connection");

                cm.setNetworkPreference(ConnectivityManager.DEFAULT_NETWORK_PREFERENCE);

            /*Toast toast = Toast.makeText(LearnMode.this, "No Internet Connection", Toast.LENGTH_LONG);
            toast.show();*/

                //  GetMeaningOfWord gmow=new GetMeaningOfWord();
                // String meaning= gmow.getMeaning();
                //Toast.makeText(getBaseContext(), meaning, LENGTH_SHORT).show();

            }
    }

    else

    {
        ttsManager.initQueue("This module has been completed.");
    }

    }


   void startMeaningActivity()
    {
        Intent intent = new Intent(
                LearnMode.this, GetMeaningOfWord.class);
        intent.putExtra("word", contents.get(index).Content_String.trim());
        intent.putExtra("content_id", Integer.toString(contents.get(index).Content_Id));
        intent.putExtra("category_name", categoryName);
        intent.putExtra("exercise_name", exerciseName);
        intent.putExtra("category_id", exerciseID);

        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        bCloseActivity = true;
        if (!bLearnComplete) {
            new AlertDialog.Builder(LearnMode.this)
                    .setTitle("The Exercise is in Progress")
                    .setMessage("Do you really want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Close the Activity
                            if (ttsManager != null) {
                                ttsManager.shutDown();
                            }
                            finish();

                            if (contents != null) {
                                // Clearing Contents
                                contents.clear();
                                contents = null;
                            }

                            Intent intent = new Intent(LearnMode.this, CategorySelection.class);
                            String name = "Learn Mode";
                            intent.putExtra("mode_name", name);
                            startActivity(intent);

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Close the Activity
                            bCloseActivity = false;
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {

            // TODO: Remove code repetition. Decide based on flag 'bCloseActivity'
            // Close the Activity
            if (ttsManager != null) {
                ttsManager.shutDown();
            }
            finish();

            // Clearing Contents
            if (contents != null) {
                contents.clear();
                contents = null;
            }

            Intent intent = new Intent(LearnMode.this, CategorySelection.class);
            String name = "Learn Mode";
            intent.putExtra("mode_name", name);
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            //Press Ctrl+R to replay the word
            case KeyEvent.KEYCODE_R:
                if (event.isCtrlPressed()) {
                    replay();

                }
                return true;
            //press Ctrl+M to get meaning
            case KeyEvent.KEYCODE_M:
                if (event.isCtrlPressed()) {
                    showMeaning();
                }
                return true;

            case KeyEvent.KEYCODE_N:
                if (event.isCtrlPressed()) {
                    nextWord();
                    return true;
                }
            case KeyEvent.KEYCODE_P:
                if (event.isCtrlPressed()) {
                    previousWord();
                    return true;
                }
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {
        String str = "";

        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT:
                str = "Swipe Right";

                Log.d("SwipeRight", "Done " + index);
                previousWord();
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                str = "Swipe Left";
                Log.d("SwipeLeft", "Done " + index);
                nextWord();
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                str = "Swipe Down";
                replay();
                // Log.d("SwipeDown", "Done " + index);
                break;
            case SimpleGestureFilter.SWIPE_UP:
                str = "Swipe Up";
                Log.d("SwipeUp", "Done " + index);
                break;

        }
        // Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoubleTap() {
        //Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
        showMeaning();
    }

    @Override
    public void onLongPress() {
        // Toast.makeText(this, "LongPress", Toast.LENGTH_SHORT).show();
        // checkSpelling();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ttsManager != null) {
            ttsManager.shutDown();
        }
        Log.d("Learn Mode", "Entered onPause for activity index " + index);
        //finish();
    }

    /**
     * Releases the resources used by the TextToSpeech engine.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Learn Mode", "Entered onDestroy");

        if (ttsManager != null) {
            ttsManager.shutDown();
        }
    }

}
