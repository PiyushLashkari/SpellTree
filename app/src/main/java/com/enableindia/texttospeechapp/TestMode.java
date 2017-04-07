package com.enableindia.texttospeechapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.texttospeechapp.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by archds on 5/27/2015.
 */
public class TestMode extends Activity implements SimpleGestureFilter.SimpleGestureListener {

    static String categoryID;
    static String categoryName;
    static List<Content> contents = null;
    TTSManager ttsManager = null;
    private String PREFS_NAME = "TTS_PREFS";

    int index = 0, size;
    public static String currentWord, currentSentence;
    private SimpleGestureFilter detector;
    public static boolean bTestComplete = false;
    public static boolean bCloseActivity;

    public static Result testResult = null;
    public static JsonUtil objJsonUtil = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Test Mode", "Entered onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_mode);
        getActionBar().setTitle("Test Mode");
        // Detect touched area
        detector = new SimpleGestureFilter(this, this);
        bTestComplete = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Test Mode", "Entered onResume()");

        SharedPreferences sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        ttsManager = new TTSManager();
        ttsManager.init(this);
        String pitch=sharedpreferences.getString("pitch", "0.7");
        String rate=sharedpreferences.getString("rate", "0.7");
        Log.d("LearnMode", "Pitch is " + pitch);
        Log.d("LearnMode", "Rate is " + rate);
        ttsManager.setTtsPitch(Float.parseFloat(pitch));
        ttsManager.setTtsRate(Float.parseFloat(rate));
        Log.d("Learn Mode", "TTS Manager initialised");


        Intent prevIntent = getIntent();
        categoryID = prevIntent.getStringExtra("category_id");
        categoryName = prevIntent.getStringExtra("category_name");

        if (contents == null) {
            // Getting the data from the DB.
            EnableIndiaDataAdapter mDbHelper = new EnableIndiaDataAdapter(this);
            mDbHelper.open();
            contents = mDbHelper.getTestContentForCategory(categoryID);
            mDbHelper.close();
        }

        EditText editText = (EditText) findViewById(R.id.txtContent);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        // Init the TestResult class
        if (testResult == null) {
            testResult = new Result();
            testResult.lstWrongAnswers = null;
        }
        testResult.nScore = 0;
        testResult.nNumCorrectAns = 0;
        testResult.nNumQuestions = 0;
        testResult.nNumQuestionsAttended = 0;
        if (testResult.lstWrongAnswers == null) {
            testResult.lstWrongAnswers = new LinkedList<Result.WrongAnswer>();
        }
        if (testResult.lstWrongAnswers.size() > 0) {
            testResult.lstWrongAnswers.clear();
        }

        if (objJsonUtil == null) {
            objJsonUtil = new JsonUtil();
        }

        Button speakNowButton = (Button) findViewById(R.id.btnReplay);
        // Button enterButton = (Button) findViewById(R.id.btnEnter);
        ImageButton rightButton = (ImageButton) findViewById(R.id.rightButton);
        ImageButton leftButton = (ImageButton) findViewById(R.id.leftButton);

        //leftButton.setVisibility(View.INVISIBLE);


        //   enterButton.setVisibility(View.INVISIBLE);
        speakNowButton.setText("Play");
        size = contents.size();
        testResult.nNumQuestions = size;
        // index = getIntent().getIntExtra("index", 1);

        if (size > 0) {
            if ((index) < size) {
                currentWord = contents.get(index).Content_String.trim().toUpperCase();
                if (contents.get(index).Content_Example != null) {
                    currentSentence = contents.get(index).Content_Example.toUpperCase();
                } else {
                    currentSentence = "";
                }
                replay();

            }
        }
        //If there is no content for that category
        else if (size == 0) {
            ttsManager.initQueue("There is no Content in this Module, Please select another Module");
        }

        speakNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                replay();
            }
        });
        Button checkButton = (Button) findViewById(R.id.btnCheck);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // String currentWord = editText.getText().toString();
                checkSpellingEntered();
            }
        });

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //TODO: Update the Wrong Answer List properly if the User decides to
                // go back and answer the question for which he gave the wrong answer or
                // skipped.
                //previousWord();
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                nextWord();
            }
        });
    }


    void replay() {
        Button speakNowButton = (Button) findViewById(R.id.btnReplay);
        // Button enterButton = (Button) findViewById(R.id.btnEnter);
        Log.d("Test Mode", "In replay()");

        if (!speakNowButton.getText().toString().trim().equalsIgnoreCase("Result")) {
            Log.d("Test Mode", "Index is" + index);

            currentWord = currentWord.toUpperCase();
            if (!currentSentence.equals("")) {
                ttsManager.initQueue(currentSentence);
                ttsManager.addQueue("Spell " + currentWord);
            } else {
                ttsManager.initQueue("Spell " + currentWord);
            }
            //   enterButton.setVisibility(View.INVISIBLE);
            speakNowButton.setText("Replay");
        } else {

        }

    }


    void checkSpellingEntered() {
        Log.d("Test Mode", "inside checkSpelling()");
        EditText editText = (EditText) findViewById(R.id.txtContent);
        String textEntered = editText.getText().toString().trim();
        Log.d("Test Mode", "text is" + textEntered);
        if (textEntered.equals(null) || textEntered.equals(""))
            ttsManager.initQueue("You have not entered any answer");
        else {
            ttsManager.initQueue("The answer that was entered is. ");
            //To spell word
            for (int i = 0; i < textEntered.length(); i++) {
                String sChar = (Character.toString(textEntered.charAt(i)));
                if (!sChar.equalsIgnoreCase(" ")) {
                    ttsManager.addQueue(sChar);
                } else {
                    ttsManager.addQueue("space");
                }
            }
        }
    }


    void nextWord() {
        // Button enterButton = (Button) findViewById(R.id.btnEnter);
        Log.d("Test Mode", "In nextWord()");

        final EditText editText = (EditText) findViewById(R.id.txtContent);
        final String textEntered = editText.getText().toString().trim();

        if (index >= size) {
            ttsManager.initQueue("The test is complete.");
            // Test complete
            // Todo:show Test result here
        } else {
            if (textEntered.trim().isEmpty()) {
                new AlertDialog.Builder(TestMode.this)
                        .setTitle("Are you sure?")
                        .setCancelable(false)
                        .setMessage("You have not entered any answer. Do you really want to continue?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                proceedForward();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("Test Mode", "User chose to answer the Question.");
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else {
                proceedForward();
            }
        }
    }

    void proceedForward() {
        final EditText editText = (EditText) findViewById(R.id.txtContent);
        final String textEntered = editText.getText().toString().trim();

        testResult.nNumQuestionsAttended++;
        if (textEntered.trim().equalsIgnoreCase(currentWord)) {
            testResult.nScore++;
        } else {
            Result.WrongAnswer wrongAns;
            wrongAns = testResult.new WrongAnswer();

            wrongAns.nContentId = contents.get(index).Content_Id;
            wrongAns.sWrongAnswer = textEntered.trim();
            testResult.lstWrongAnswers.add(wrongAns);
        }

        index++;

        if (index < size) {
            Log.d("Test Mode", "New Index is " + index);
            currentWord = contents.get(index).Content_String.trim().toUpperCase();
            if (contents.get(index).Content_Example != null) {
                currentSentence = contents.get(index).Content_Example.toUpperCase();
            } else {
                currentSentence = "";
            }
            Log.d("Test Mode", "Current Word is " + currentWord);
            if (!currentSentence.equals("")) {
                Log.d("Test Mode", "Current Sentence is " + currentSentence);
            }
            editText.setText(" ");
            //enterButton.setVisibility(View.INVISIBLE);

            replay();
        } else {
            // Test complete
            editText.setText(" ");
            TextView txtViewTestResult = (TextView) findViewById(R.id.textViewTestResult);
            StringBuilder strResult = new StringBuilder("\nTest Result: ");

            // TO hide the virtual keyboard
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            ttsManager.initQueue("The test is complete.");

            txtViewTestResult.setVisibility(View.VISIBLE);
            txtViewTestResult.setText(strResult.toString());

            String sScore = "The Result is: " + testResult.nScore + " out of " + testResult.nNumQuestions;
            strResult.append(testResult.nScore + " out of " + testResult.nNumQuestions + "\n\n");
            txtViewTestResult.setText(strResult.toString());
            ttsManager.addQueue(sScore);

            String sWrongAnswerTitle = "";
            String sWord = "";
            String sSentence = "";
            String sWrongAnswerIndex = "";
            if (testResult.lstWrongAnswers.size() > 0) {
                if (testResult.lstWrongAnswers.size() > 1) {
                    sWrongAnswerTitle = "The number of wrong answers is: " + testResult.lstWrongAnswers.size();
                } else if (testResult.lstWrongAnswers.size() == 1) {
                    sWrongAnswerTitle = "Wrong answer was given for 1 question.";
                }
                if (!sWrongAnswerTitle.isEmpty()) {
                    strResult.append("\n" + sWrongAnswerTitle + "\n");
                    txtViewTestResult.setText(strResult.toString());
                    ttsManager.addQueue(sWrongAnswerTitle);
                }

                for (int i = 0; i < testResult.lstWrongAnswers.size(); i++) {
                    for (int j = 0; j < contents.size(); j++) {
                        if (contents.get(j).Content_Id == testResult.lstWrongAnswers.get(i).nContentId) {
                            sWord = contents.get(j).Content_String.trim().toUpperCase();
                            sSentence = contents.get(j).Content_Example;
                            if (sSentence != null) {
                                sSentence = sSentence.toUpperCase();
                            } else {
                                sSentence = "";
                            }

                            sWrongAnswerIndex = "Wrong Answer Number: " + (i + 1) + ". \nThe question was to spell " + sWord;
                            strResult.append("\n" + sWrongAnswerIndex + "\n");
                            txtViewTestResult.setText(strResult.toString());
                            ttsManager.addQueue(sWrongAnswerIndex);

                            if (!sSentence.equals("")) {
                                strResult.append("as used in the sentence: ");
                                strResult.append(sSentence + "\n");
                                txtViewTestResult.setText(strResult.toString());
                                ttsManager.addQueue(" as used in the sentence ");
                                ttsManager.addQueue(sSentence);
                            }

                            boolean bQuestionSkipped = false;

                            if (!testResult.lstWrongAnswers.get(i).sWrongAnswer.equals("")) {
                                strResult.append("\n" + "The spelling that was entered is:  ");
                                strResult.append(testResult.lstWrongAnswers.get(i).sWrongAnswer + "\n");
                                txtViewTestResult.setText(strResult.toString());
                                ttsManager.addQueue("The spelling that was entered is: ");
                                for (int k = 0; k < testResult.lstWrongAnswers.get(i).sWrongAnswer.length(); k++) {
                                    String sChar = (Character.toString(testResult.lstWrongAnswers.get(i).sWrongAnswer.charAt(k)));
                                    if (!sChar.equalsIgnoreCase(" ")) {
                                        ttsManager.addQueue(sChar);
                                    } else {
                                        ttsManager.addQueue("space");
                                    }
                                }
                            } else {
                                strResult.append("\n" + "This question was skipped." + "\n");
                                txtViewTestResult.setText(strResult.toString());
                                ttsManager.addQueue("This question was skipped.");
                                bQuestionSkipped = true;
                            }

                            if (!bQuestionSkipped) {
                                strResult.append("\n" + "The correct spelling is: ");
                                strResult.append(contents.get(j).Content_String + "\n\n");
                                txtViewTestResult.setText(strResult.toString());

                                ttsManager.addQueue("The correct spelling is: ");
                            } else {
                                strResult.append("\n" + "The spelling is: ");
                                strResult.append(contents.get(j).Content_String + "\n\n");
                                txtViewTestResult.setText(strResult.toString());

                                ttsManager.addQueue("The spelling is: ");
                            }

                            for (int k = 0; k < contents.get(j).Content_String.length(); k++) {
                                String sChar = (Character.toString(contents.get(j).Content_String.charAt(k)));
                                if (!sChar.equalsIgnoreCase(" ")) {
                                    ttsManager.addQueue(sChar);
                                } else {
                                    ttsManager.addQueue("space");
                                }
                            }
                        }
                    }
                }
            }

            // Updating the DB
            EnableIndiaDataAdapter mDbHelper = new EnableIndiaDataAdapter(TestMode.this.getBaseContext());
            mDbHelper.open();

            mDbHelper.writeTestScore(categoryID, objJsonUtil.toJSon(testResult), "Test");
            mDbHelper.close();

            bTestComplete = true;

            // Disabling the Buttons
            ImageButton btnNext = (ImageButton) findViewById(R.id.rightButton);
            ImageButton btnPrev = (ImageButton) findViewById(R.id.leftButton);
            Button btnReplay = (Button) findViewById(R.id.btnReplay);
            Button btnCheck = (Button) findViewById(R.id.btnCheck);
            EditText txtAnswer = (EditText) findViewById(R.id.txtContent);
            btnNext.setEnabled(false);
            btnPrev.setEnabled(false);
            btnReplay.setEnabled(false);
            btnCheck.setEnabled(false);
            txtAnswer.setText(" ");
        }

    }

    void previousWord() {
        // Button enterButton = (Button) findViewById(R.id.btnEnter);
        if (!bTestComplete) {
            Button speakNowButton = (Button) findViewById(R.id.btnReplay);
            Log.d("Test Mode", "In previousWord()");
            speakNowButton.setText("Play");

            Log.d("Test Mode", "inside previousWord()");
            index--;

            if (index >= 0) {
                Log.d("Test Mode", "New Index is " + index);
                currentWord = contents.get(index).Content_String.trim().toUpperCase();
                if (contents.get(index).Content_Example != null) {
                    currentSentence = contents.get(index).Content_Example.toUpperCase();
                } else {
                    currentSentence = "";
                }
                Log.d("Test Mode", "Current Word is " + currentWord);
                if (!currentSentence.equals("")) {
                    Log.d("Test Mode", "Current Sentence is " + currentSentence);
                }
                EditText editText = (EditText) findViewById(R.id.txtContent);
                //  enterButton.setVisibility(View.INVISIBLE);
                replay();
                editText.setText(" ");

            } else {
                Log.d("Learn Mode", "This is the first word");
                ttsManager.initQueue("You have reached first word");
            }

        } else {
            // Test complete
            ttsManager.initQueue("The test is complete.");
        }
    }


    @Override
    public void onBackPressed() {
        bCloseActivity = true;
        if (!bTestComplete) {
            new AlertDialog.Builder(TestMode.this)
                    .setTitle("Test is in Progress")
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

                            Intent intent = new Intent(TestMode.this, CategorySelection.class);
                            String name = "Test Mode";
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

            Intent intent = new Intent(TestMode.this, CategorySelection.class);
            String name = "Test Mode";
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

            case KeyEvent.KEYCODE_N:
                if (event.isCtrlPressed()) {
                    nextWord();
                    return true;
                }
//            case KeyEvent.KEYCODE_P:
//                if (event.isCtrlPressed()) {
//                    previousWord();
//                    return true;
//                }
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
        Log.d("Test Mode", "Inside onSwipe()");
        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT:
                str = "Swipe Right";
                Log.d("Test Mode", "Swiped right");
                if (!bTestComplete) {
                    //TODO: Update the Wrong Answer List properly.
                    //previousWord();
                }
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                str = "Swipe Left";
                Log.d("Test Mode", "Swiped left");
                if (!bTestComplete) {
                    nextWord();
                }
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                str = "Swipe Down";
                Log.d("Test Mode", "Swiped down");
                if (!bTestComplete) {
                    replay();
                }
                // Log.d("SwipeDown", "Done " + index);
                break;
            case SimpleGestureFilter.SWIPE_UP:
                str = "Swipe Up";
                Log.d("SwipeUp", "Done " + index);
                break;

        }
        // Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    //Not used in this activity
    @Override
    public void onDoubleTap() {
    }

    //not used in this activity
    @Override
    public void onLongPress() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Test Mode", "Entered onPause for activity index " + index);
        ttsManager.shutDown();
    }

    /**
     * Releases the resources used by the TextToSpeech engine.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Test Mode", "Entered onDestroy");

        if (contents != null) {
            // Clearing Contents
            contents.clear();
            contents = null;
        }
    }
}