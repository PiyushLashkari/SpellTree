package com.cisco.texttospeechapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.texttospeechapp.R;

public class ExerciseSelection extends Activity {

    private String[] details = {"Exercise 1", "Exercise 2", "Exercise 3", "Exercise 4", "Exercise 5", "Exercise 6", "Exercise 7", "Exercise 8", "Exercise 9", "Exercise 10", "Exercise 11"};

    protected void onCreate(Bundle savedInstanceState) {
        Context context = getBaseContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        getActionBar().setTitle("Choose Exercise");
    }


    @Override
    protected void onResume() {
        System.out.println("entered on resume of exercise selection");
        final ListView list = (ListView) findViewById(R.id.listView1);
        System.out.println("entered");
        //ContentsListAdapter adapter = new ContentsListAdapter(this, details);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, details);
        list.setAdapter(adapter);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                System.out.println("entered again");
                Log.d("Sample", "Clicked on item  in exercise selection");
                //Toast.makeText(getApplicationContext(), position, Toast.LENGTH_LONG);
                //TextView tv1 = (TextView) viewClicked.findViewById(R.id.textView1);
                //String name = (String) tv1.getText();
                String name= (String)list.getItemAtPosition(position);
                Intent intent = new Intent(ExerciseSelection.this, LearnMode.class);
                intent.putExtra("name", name);
                startActivity(intent);

            }
        });
        super.onResume();
    }


}