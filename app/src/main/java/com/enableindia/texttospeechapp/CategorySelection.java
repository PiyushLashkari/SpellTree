package com.enableindia.texttospeechapp;

import java.util.ArrayList;

import com.enableindia.texttospeechapp.adapters.ContentsListAdapter;
import com.example.texttospeechapp.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class CategorySelection extends ActionBarActivity {

    String[] sCategoryNames;
    String modeName;
    List<Category> categories;
    List<Progress> progress;

    protected void onCreate(Bundle savedInstanceState) {
        Context context = getBaseContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        getActionBar().setTitle("Choose Category");
    }


    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("entered on resume");

        Intent prevIntent = getIntent();
        modeName = prevIntent.getStringExtra("mode_name");

        EnableIndiaDataAdapter mDbHelper = new EnableIndiaDataAdapter(this);
        //mDbHelper.createDatabase();
        mDbHelper.open();

        categories = mDbHelper.getCategories();
        progress = mDbHelper.getProgress();

        sCategoryNames = new String[categories.size()];
        boolean bProgressFound;
        List<Integer> lstModuleScores = new LinkedList<Integer>();
        String sScore = "";
        String sNumQuestions = "";

        for (int i = 0; i < categories.size(); i++) {
            if ( modeName.equalsIgnoreCase("Test Mode")) {
                bProgressFound = false;
                lstModuleScores.clear();
                for (int j = 0; j < progress.size(); j++) {
                    if (progress.get(j).Progress_Type.trim().equalsIgnoreCase("Test")) {
                        if (Integer.toString(categories.get(i).Category_Id).trim().equalsIgnoreCase(progress.get(j).Exercise_Id_List.trim())) {
                            bProgressFound = true;
                            lstModuleScores.add(j);
                        }
                    }
                }

                if (bProgressFound == false) {
                    sCategoryNames[i] = categories.get(i).Category_Name;
                } else {
                    try {
                        JSONObject jObj = new JSONObject(progress.get(lstModuleScores.get((lstModuleScores.size() - 1))).Result);
                        sScore = jObj.getString(Result.TAG_SCORE);
                        sNumQuestions = jObj.getString(Result.TAG_NUM_QUESTIONS);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (!sScore.equals("")) {
                        sCategoryNames[i] = categories.get(i).Category_Name + " - Completed. \nLatest Result: " + sScore + " out of " + sNumQuestions;
                    } else {
                        sCategoryNames[i] = categories.get(i).Category_Name + " - Completed";
                    }
                }
            } else {
                sCategoryNames[i] = categories.get(i).Category_Name;
            }
        }

        mDbHelper.close();

        final ListView list = (ListView) findViewById(R.id.listView);
        System.out.println("entered");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, sCategoryNames);
        list.setAdapter(adapter);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                System.out.println("entered again");
                Log.d("Sample", "Clicked on item in Content selection");
                Intent intent = new Intent();
                String categoryName = (String) list.getItemAtPosition(position);
                // Launch the ExerciseSelection page

                if (modeName.trim().equalsIgnoreCase("Learn Mode")) {
                    intent = new Intent(CategorySelection.this, ExerciseSelection.class);
                } else if (modeName.trim().equalsIgnoreCase("Test Mode")) {
                    intent = new Intent(CategorySelection.this, TestModeOption.class);
                }

                //  Pass category name to next activity
                intent.putExtra("mode_name", modeName);

                Integer nCategoryNameComplIndex = 0;
                if (categoryName.length() > 3) {
                    nCategoryNameComplIndex = categoryName.indexOf(" - Completed");
                    if(nCategoryNameComplIndex > 0) {
                        // Extracting the CategoryName.
                        categoryName = categoryName.substring(0, nCategoryNameComplIndex);
                    }
                }
                intent.putExtra("category_name", categoryName);

                int categoryIdSel = -1;
                for (int i = 0; i < categories.size(); i++) {
                    if (categoryName.trim().equalsIgnoreCase(categories.get(i).Category_Name)) {
                        categoryIdSel = categories.get(i).Category_Id;
                    }
                }
                intent.putExtra("category_id", Integer.toString(categoryIdSel));

                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CategorySelection.this, ModeSelection.class);
        startActivity(intent);

        //super.onBackPressed();
    }

}