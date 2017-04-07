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

import com.example.texttospeechapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class TestModeOption extends Activity {

    private String[] strOptions = {"Take a Test", "Report"};
    static String categoryID;
    static String categoryName;

    List<Progress> progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context = getBaseContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        getActionBar().setTitle("Choose an Option");
    }

    @Override
    protected void onResume() {
        System.out.println("entered on resume in TestModeOption Selection page");
        final ListView list = (ListView) findViewById(R.id.listView);

        Intent prevIntent = getIntent();
        categoryID = prevIntent.getStringExtra("category_id");
        categoryName = prevIntent.getStringExtra("category_name");

        EnableIndiaDataAdapter mDbHelper = new EnableIndiaDataAdapter(this);
        mDbHelper.open();
        progress = mDbHelper.getProgress();
        mDbHelper.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, strOptions);
        list.setAdapter(adapter);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        registerForContextMenu(list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                System.out.println("Selected a TestModeOption");
                Log.d("Sample", "Clicked on an option in the TestModeOption List");
                Intent intent = new Intent();
                String sTestModeOption = (String) list.getItemAtPosition(position);

                if (sTestModeOption.trim().equalsIgnoreCase("Take a Test")) {
                    intent = new Intent(TestModeOption.this, TestMode.class);
                    //  Pass category name to next activity
                    intent.putExtra("mode_name", "Test Mode");
                    intent.putExtra("category_name", categoryName);
                    intent.putExtra("category_id", categoryID);

                    startActivity(intent);
                } else if (sTestModeOption.trim().equalsIgnoreCase("Report")) {
                    boolean bProgressFound = false;

                    for (int j = 0; j < progress.size(); j++) {
                        if (progress.get(j).Progress_Type.trim().equalsIgnoreCase("Test")) {
                            if (categoryID.trim().equalsIgnoreCase(progress.get(j).Exercise_Id_List.trim())) {
                                bProgressFound = true;
                                break;
                            }
                        }
                    }

                    if (bProgressFound) {
                        intent = new Intent(TestModeOption.this, TestReportSelection.class);
                        //  Pass category name to next activity
                        intent.putExtra("mode_name", "Test Mode");
                        intent.putExtra("category_name", categoryName);
                        intent.putExtra("category_id", categoryID);

                        startActivity(intent);
                    } else {
                        new AlertDialog.Builder(TestModeOption.this)
                                .setTitle("Test Report Not Found")
                                .setCancelable(false)
                                .setMessage("A test has not been taken on this category yet.\n\nPlease take a test on this category.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d("Test Mode Option", "A test has not been taken on this category yet.");
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
            }
        });

        super.onResume();
    }
}
