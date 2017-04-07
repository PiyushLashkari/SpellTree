package com.cisco.texttospeechapp;

import com.cisco.texttospeechapp.adapters.ModeSelectionListAdapter;
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

    private String[] details = {"Learn Mode", "Practice Mode", "Test Mode"};

    float x1,x2;
    float y1, y2;
    protected void onCreate(Bundle savedInstanceState) {
        Context context = getBaseContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        getActionBar().setTitle("Choose mode");
    }


    @Override
    protected void onResume() {
        System.out.println("entered on resume in ModeSelection");
        final ListView list = (ListView) findViewById(R.id.listView1);
        System.out.println("entered");
       // ModeSelectionListAdapter adapter = new ModeSelectionListAdapter(this, details);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, details);
        list.setAdapter(adapter);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        registerForContextMenu(list);
       /* list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View viewClicked, int position, long id) {
                System.out.println("entered again");
                Log.d("Sample", "Clicked on item in ModeSelection");
                //Toast.makeText(getApplicationContext(), position, Toast.LENGTH_LONG);
                TextView tv1 = (TextView) viewClicked.findViewById(R.id.textView1);
                String name = (String) tv1.getText();
                Intent intent = new Intent(ModeSelection.this, ContentsSelection.class);
                intent.putExtra("name", name);
                startActivity(intent);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                System.out.println("entered again");
                Log.d("Sample", "Clicked on item in ModeSelection");
                //Toast.makeText(getApplicationContext(), position, Toast.LENGTH_LONG);
               // TextView tv1 = (TextView) viewClicked.findViewById(R.id.textView1);
               // String name = (String) tv1.getText();
                String name= (String)list.getItemAtPosition(position);
                Intent intent = new Intent(ModeSelection.this, ContentsSelection.class);
                intent.putExtra("mode_name", name);
                startActivity(intent);

            }
        });

        super.onResume();
    }



}