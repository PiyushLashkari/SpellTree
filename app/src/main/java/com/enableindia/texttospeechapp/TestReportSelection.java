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
import android.widget.ListView;

import com.example.texttospeechapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class TestReportSelection extends Activity {

    List<Progress> progress;
    static String categoryID;
    private String PREFS_NAME = "TTS_PREFS";

    String[] sTestResults;
    TTSManager ttsManager = null;
    private static int MAX_RESULTS = 10;
    List<String> lstTestTimeScore;
    List<Integer> lstTestIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context = getBaseContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        getActionBar().setTitle("Choose a Test");
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

        Intent prevIntent = getIntent();
        categoryID = prevIntent.getStringExtra("category_id");

        EnableIndiaDataAdapter mDbHelper = new EnableIndiaDataAdapter(this);
        mDbHelper.open();
        progress = mDbHelper.getProgress();
        mDbHelper.close();

        boolean bProgressFound;
        String sScore = "";
        String sNumQuestions = "";
        String sTemp;

        lstTestTimeScore = new LinkedList<String>();
        lstTestIDs = new LinkedList<Integer>();

        bProgressFound = false;
        lstTestTimeScore.clear();
        for (int j = 0; j < progress.size(); j++) {
            if (progress.get(j).Progress_Type.trim().equalsIgnoreCase("Test")) {
                if (categoryID.trim().equalsIgnoreCase(progress.get(j).Exercise_Id_List.trim())) {
                    bProgressFound = true;

                    sTemp = progress.get(j).Timestamp.trim() + "\n";
                    try {
                        JSONObject jObj = new JSONObject(progress.get(j).Result);
                        sScore = jObj.getString(Result.TAG_SCORE);
                        sNumQuestions = jObj.getString(Result.TAG_NUM_QUESTIONS);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    sTemp += "Score: " + sScore.trim() + " out of " + sNumQuestions.trim();

                    lstTestTimeScore.add(sTemp);
                    lstTestIDs.add(progress.get(j).Test_Id);
                }
            }
        }

        if (bProgressFound == false) {
            ttsManager.initQueue("A test has not been taken on this category yet. Please take a test on this category.");
        } else {
            final ListView list = (ListView) findViewById(R.id.listView);
            System.out.println("entered");

            if (lstTestTimeScore.size() > MAX_RESULTS) {
                sTestResults = new String[MAX_RESULTS];
            } else {
                sTestResults = new String[lstTestTimeScore.size()];
            }

            int nStartIndex = 0;
            if (lstTestTimeScore.size() > MAX_RESULTS) {
                nStartIndex = lstTestTimeScore.size() - MAX_RESULTS;
            }
            int nTestResultIndex = 0;

            for ( int i = nStartIndex; i < lstTestTimeScore.size(); i++) {
                sTestResults[nTestResultIndex] = lstTestTimeScore.get(i);
                nTestResultIndex++;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, sTestResults);
            list.setAdapter(adapter);
            list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                    System.out.println("entered again");
                    Log.d("Sample", "Clicked on item in Content selection");
                    Intent intent;
                    String testID;

                    if (lstTestIDs.size() > MAX_RESULTS) {
                        testID = Integer.toString(lstTestIDs.get(lstTestIDs.size() - MAX_RESULTS + position));
                    } else {
                        testID = Integer.toString(lstTestIDs.get(position));
                    }
                    // Launch the ExerciseSelection page

                    intent = new Intent(TestReportSelection.this, TestReport.class);

                    //  Pass category name to next activity
                    intent.putExtra("test_id", testID);

                    startActivity(intent);
                }
            });
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
    }
}