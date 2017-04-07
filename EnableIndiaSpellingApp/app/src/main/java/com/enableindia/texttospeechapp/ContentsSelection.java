package com.cisco.texttospeechapp;

import java.util.ArrayList;

import com.cisco.texttospeechapp.adapters.ContentsListAdapter;
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

import java.util.List;

public class ContentsSelection extends ActionBarActivity {

    String[] details = {"Homonyms", "Months in a year","Fruits","Animals","Days in a week","Seasons","Parts of body"};
    String[] sCategoryNames;
    String modeName;

    protected void onCreate(Bundle savedInstanceState) {
        Context context = getBaseContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        getActionBar().setTitle("Choose the Category");


        EnableIndiaDataAdapter mDbHelper = new EnableIndiaDataAdapter(this);
        mDbHelper.createDatabase();
        mDbHelper.open();

        List<Category> categories = mDbHelper.getCategories();

        sCategoryNames = new String[categories.size()];

        for (int i = 0; i < categories.size(); i++) {
            sCategoryNames[i] = categories.get(i).name;
        }
        //Cursor testdata = mDbHelper.getTestData();

        mDbHelper.close();
    }


    @Override
    protected void onResume() {
        System.out.println("entered on resume");
        Intent prevIntent = getIntent();
        modeName= prevIntent.getStringExtra("mode_name");
        final ListView list = (ListView) findViewById(R.id.listView1);
        System.out.println("entered");
        //use below for custom list view
        //ContentsListAdapter adapter = new ContentsListAdapter(this, details);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, details);
        list.setAdapter(adapter);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                System.out.println("entered again");
                Log.d("Sample", "Clicked on item in Contents selection");
                Intent intent=new Intent();
                //Toast.makeText(getApplicationContext(), position, Toast.LENGTH_LONG);
                //TextView tv1 = (TextView) viewClicked.findViewById(R.id.textView1);
                // String name = (String) tv1.getText();
                String categoryName = (String)list.getItemAtPosition(position);
                //Based on mode selected, call respective class
                //Bypassing ExerciceSelection page for demo purpose
                if (modeName.equalsIgnoreCase("Learn Mode")) {
                    intent = new Intent(ContentsSelection.this, LearnMode.class);
                } else if (modeName.equalsIgnoreCase("Practice Mode")) {
                    intent = new Intent(ContentsSelection.this, PracticeMode.class);
                }
                //  Pass catergory name to next activity
                intent.putExtra("category_name", categoryName);
                startActivity(intent);

            }
        });
        super.onResume();
    }

}