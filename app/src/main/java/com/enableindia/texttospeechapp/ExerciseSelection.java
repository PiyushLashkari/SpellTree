package com.enableindia.texttospeechapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.texttospeechapp.R;

import java.util.LinkedList;
import java.util.List;

public class ExerciseSelection extends Activity {

    String[] sExerciseNames;
    String modeName;
    String categoryName;
    String categoryID;
    List<Exercise> exercises;
    List<Progress> progress;

    protected void onCreate(Bundle savedInstanceState) {
        Context context = getBaseContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        getActionBar().setTitle("Choose Module");
    }


    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("entered on resume of exercise selection");

        Intent prevIntent = getIntent();
        modeName = prevIntent.getStringExtra("mode_name");
        categoryName = prevIntent.getStringExtra("category_name");
        categoryID = prevIntent.getStringExtra("category_id");

        // Geting the data from the DB.
        EnableIndiaDataAdapter mDbHelper = new EnableIndiaDataAdapter(this);
        mDbHelper.open();

        exercises = mDbHelper.getExercises(categoryID);
        progress = mDbHelper.getProgress();

        sExerciseNames = new String[exercises.size()];

        boolean bProgressFound;

        for (int i = 0; i < exercises.size(); i++) {
            bProgressFound = false;
            for (int j = 0; j < progress.size(); j++) {
                if ( progress.get(j).Progress_Type.trim().equalsIgnoreCase("Learn")) {
                    if (Integer.toString(exercises.get(i).SubCategory_Id).trim().equalsIgnoreCase(progress.get(j).Exercise_Id_List.trim())) {
                        bProgressFound = true;
                        break;
                    }
                }
            }

            if (bProgressFound == false) {
                sExerciseNames[i] = exercises.get(i).SubCategory_Name;
            } else {
                sExerciseNames[i] = exercises.get(i).SubCategory_Name + " - Completed";
            }
        }
        mDbHelper.close();


        final ListView list = (ListView) findViewById(R.id.listView);
        System.out.println("entered");

        if (sExerciseNames.length > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, sExerciseNames);
            list.setAdapter(adapter);
            list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            list.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                    System.out.println("entered again");
                    Log.d("Sample", "Clicked on item  in exercise selection");
                    Intent intent = new Intent();
                    if (modeName.trim().equalsIgnoreCase("Learn Mode")) {
                        intent = new Intent(ExerciseSelection.this, LearnMode.class);
                    } else if (modeName.trim().equalsIgnoreCase("Test Mode")) {
                        intent = new Intent(ExerciseSelection.this, TestMode.class);
                    }

                    String exerciseName = (String) list.getItemAtPosition(position);
                    intent.putExtra("category_id", categoryID);
                    intent.putExtra("category_name", categoryName);

                    Integer nExerNameComplIndex = 0;
                    if (exerciseName.length() > 3) {
                        nExerNameComplIndex = exerciseName.indexOf(" - Completed");
                        if ( nExerNameComplIndex > 0 ) {
                            // Extracting the ExerciseName.
                            exerciseName = exerciseName.substring(0, nExerNameComplIndex);
                        }
                    }
                    intent.putExtra("exercise_name", exerciseName);

                    int exerciseIdSel = -1;
                    for (int i = 0; i < exercises.size(); i++) {
                        if (exerciseName.trim().equalsIgnoreCase(exercises.get(i).SubCategory_Name)) {
                            exerciseIdSel = exercises.get(i).SubCategory_Id;
                        }
                    }
                    intent.putExtra("exercise_id", Integer.toString(exerciseIdSel));

                    startActivity(intent);
                }
            });
        }
        else {
            new AlertDialog.Builder(ExerciseSelection.this)
                    .setTitle("No Modules")
                    .setMessage("No Modules in this Category! Please select another Category.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Close the Activity
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ExerciseSelection.this, CategorySelection.class);
        intent.putExtra("mode_name", modeName);
        startActivity(intent);

        //super.onBackPressed();
    }
}