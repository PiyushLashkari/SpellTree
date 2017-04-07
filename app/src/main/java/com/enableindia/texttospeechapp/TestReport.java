package com.enableindia.texttospeechapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.texttospeechapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TestReport extends Activity {

    private TTSManager ttsManager = null;
    private static String word;
    private static final String KEY = "prefs";
    private String PREFS_NAME = "TTS_PREFS";

    private TextView txtViewTestReport;
    private String testID;
    List<Progress> progress;
    private String sTestReport;
    private static Result testResult = null;
    static List<Content> contents = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle("Test Report");
        setContentView(R.layout.show_meaning);
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("entered on resume");

        SharedPreferences sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        ttsManager = new TTSManager();
        ttsManager.init(this);

        String pitch=sharedpreferences.getString("pitch", "0.7");
        String rate=sharedpreferences.getString("rate", "0.7");
        Log.d("TestReport", "Pitch is " + pitch);
        Log.d("TestReport", "Rate is " + rate);
        ttsManager.setTtsPitch(Float.parseFloat(pitch));
        ttsManager.setTtsRate(Float.parseFloat(rate));
        Log.d("TestReport", "TTS Manager initialised");

        Button playButton=(Button)findViewById(R.id.playButton);
        playButton.setContentDescription("Click to know results");
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                replay();
            }
        });

        constructReport();
    }

    void replay()
    {
        Log.d("TestReport", "Inside replay()");
        constructReport();
    }

    private void constructReport() {

        boolean bTestFound = false;
        int nProgressIndex = 0;

        EnableIndiaDataAdapter mDbHelper = new EnableIndiaDataAdapter(this);
        mDbHelper.open();
        progress = mDbHelper.getProgress();
        mDbHelper.close();

        txtViewTestReport = (TextView) findViewById(R.id.textView);
        //txtViewTestReport.setBackgroundResource(@drawable/gradient_box);

        testID = getIntent().getStringExtra("test_id");

        for (int j = 0; j < progress.size(); j++) {
            if (progress.get(j).Progress_Type.trim().equalsIgnoreCase("Test")) {
                if (testID.trim().equalsIgnoreCase(Integer.toString(progress.get(j).Test_Id).trim())) {
                    sTestReport = progress.get(j).Result;
                    bTestFound = true;
                    nProgressIndex = j;
                    break;
                }
            }
        }

        if (bTestFound == false) {
            ttsManager.initQueue("Test Report not found.");
        } else {
            if (testResult == null) {
                testResult = new Result();
                testResult.lstWrongAnswers = null;
            }

            StringBuilder strResult = new StringBuilder("\nTest Result: ");
            JSONArray jsonWrongAnswers;

            try {
                JSONObject jObj = new JSONObject(progress.get(nProgressIndex).Result);
                testResult.nScore = Integer.parseInt(jObj.getString(Result.TAG_SCORE));
                testResult.nNumQuestions = Integer.parseInt(jObj.getString(Result.TAG_NUM_QUESTIONS));

                String sScore = "The Score is: " + testResult.nScore + " out of " + testResult.nNumQuestions;
                strResult.append(testResult.nScore + " out of " + testResult.nNumQuestions + "\n\n");
                txtViewTestReport.setText(strResult.toString());
                ttsManager.initQueue(sScore);

                if (testResult.nScore == testResult.nNumQuestions) {
                    // All answers are correct.
                    String sAllAnsCorrect = "All the answers were correct.";
                    strResult.append("\n" + sAllAnsCorrect + "\n");
                    txtViewTestReport.setText(strResult.toString());
                    ttsManager.addQueue(sAllAnsCorrect);
                } else {
                    jsonWrongAnswers = jObj.getJSONArray(Result.TAG_WRONG_ANSWERS);


                    mDbHelper = new EnableIndiaDataAdapter(this);
                    mDbHelper.open();
                    contents = mDbHelper.getTestContentForCategory(progress.get(nProgressIndex).Exercise_Id_List);
                    mDbHelper.close();

                    String sWrongAnswerTitle = "";
                    String sWord = "";
                    String sSentence = "";
                    String sWrongAnswerIndex = "";

                    String sContentID;
                    String sWrongAnswer;
                    if (jsonWrongAnswers.length() > 0) {
                        if (jsonWrongAnswers.length() > 1) {
                            sWrongAnswerTitle = "The number of wrong answers is: " + jsonWrongAnswers.length();
                        } else if (jsonWrongAnswers.length() == 1) {
                            sWrongAnswerTitle = "Wrong answer was given for 1 question.";
                        }

                        if (!sWrongAnswerTitle.isEmpty()) {
                            strResult.append("\n" + sWrongAnswerTitle + "\n");
                            txtViewTestReport.setText(strResult.toString());
                            ttsManager.addQueue(sWrongAnswerTitle);
                        }

                        for (int i = 0; i < jsonWrongAnswers.length(); i++) {
                            JSONObject jobj = jsonWrongAnswers.getJSONObject(i);
                            sContentID = jobj.getString(Result.TAG_CONTENT_ID);
                            sWrongAnswer = jobj.getString(Result.TAG_WRONG_ANSWER);

                            for (int j = 0; j < contents.size(); j++) {
                                if (contents.get(j).Content_Id == Integer.parseInt(sContentID)) {
                                    sWord = contents.get(j).Content_String.trim().toUpperCase();
                                    sSentence = contents.get(j).Content_Example;
                                    if (sSentence != null) {
                                        sSentence = sSentence.toUpperCase();
                                    } else {
                                        sSentence = "";
                                    }

                                    sWrongAnswerIndex = "Wrong Answer Number: " + (i + 1) + ". \nThe question was to spell " + sWord;
                                    strResult.append("\n" + sWrongAnswerIndex + "\n");
                                    txtViewTestReport.setText(strResult.toString());
                                    ttsManager.addQueue(sWrongAnswerIndex);

                                    if (!sSentence.equals("")) {
                                        strResult.append("as used in the sentence: ");
                                        strResult.append(sSentence + "\n");
                                        txtViewTestReport.setText(strResult.toString());
                                        ttsManager.addQueue(" as used in the sentence ");
                                        ttsManager.addQueue(sSentence);
                                    }

                                    boolean bQuestionSkipped = false;

                                    if (!sWrongAnswer.equals("")) {
                                        strResult.append("\n" + "The spelling that was entered is:  ");
                                        strResult.append(sWrongAnswer + "\n");
                                        txtViewTestReport.setText(strResult.toString());
                                        ttsManager.addQueue("The spelling that was entered is: ");
                                        for (int k = 0; k < sWrongAnswer.length(); k++) {
                                            String sChar = (Character.toString(sWrongAnswer.charAt(k)));
                                            if (!sChar.equalsIgnoreCase(" ")) {
                                                ttsManager.addQueue(sChar);
                                            } else {
                                                ttsManager.addQueue("space");
                                            }
                                        }
                                    } else {
                                        strResult.append("\n" + "This question was skipped." + "\n");
                                        txtViewTestReport.setText(strResult.toString());
                                        ttsManager.addQueue("This question was skipped.");
                                        bQuestionSkipped = true;
                                    }

                                    if (!bQuestionSkipped) {
                                        strResult.append("\n" + "The correct spelling is: ");
                                        strResult.append(contents.get(j).Content_String + "\n\n");
                                        txtViewTestReport.setText(strResult.toString());

                                        ttsManager.addQueue("The correct spelling is: ");
                                    } else {
                                        strResult.append("\n" + "The spelling is: ");
                                        strResult.append(contents.get(j).Content_String + "\n\n");
                                        txtViewTestReport.setText(strResult.toString());

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
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Test Mode", "Entered onPause for activity");
        if ( ttsManager != null) {
            ttsManager.shutDown();
        }
    }

    /**
     * Releases the resources used by the TextToSpeech engine.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Test Mode", "Entered onDestroy");

        if ( ttsManager != null) {
            ttsManager.shutDown();
        }

        if (contents != null) {
            // Clearing Contents
            contents.clear();
            contents = null;
        }
    }
}