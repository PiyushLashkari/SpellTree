package com.enableindia.texttospeechapp;

import com.enableindia.texttospeechapp.adapters.ModeSelectionListAdapter;
import com.example.texttospeechapp.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ModeSelection extends Activity {

    private String[] details = {"Learn Mode", "Test Mode"};

    protected void onCreate(Bundle savedInstanceState) {
        Context context = getBaseContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        getActionBar().setTitle("Choose Mode");

        EnableIndiaDataAdapter mDbHelper = new EnableIndiaDataAdapter(this);
        mDbHelper.createDatabase();
        mDbHelper.close();
    }


    @Override
    protected void onResume() {
        System.out.println("entered on resume in ModeSelection");
        final ListView list = (ListView) findViewById(R.id.listView);
        System.out.println("entered");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, details);
        list.setAdapter(adapter);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        registerForContextMenu(list);
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                System.out.println("entered again");
                Log.d("Sample", "Clicked on item in ModeSelection");
                String modeName = (String) list.getItemAtPosition(position);
                Intent intent = new Intent(ModeSelection.this, CategorySelection.class);
                intent.putExtra("mode_name", modeName);
                startActivity(intent);

            }
        });

        super.onResume();
    }

}