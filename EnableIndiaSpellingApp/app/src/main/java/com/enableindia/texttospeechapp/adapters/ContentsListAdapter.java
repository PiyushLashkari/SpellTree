package com.cisco.texttospeechapp.adapters;

import java.util.List;

import com.example.texttospeechapp.R;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ContentsListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] contents;
    //  private final LayoutInflater mInflater;

    public ContentsListAdapter(Context context, String[] contents) {
        //  super(context, layoutResourceId,details);
        super(context, R.layout.contents_page, contents);
        //	mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.contents = contents;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.contents_page, parent, false);
        TextView textView1 = (TextView) rowView.findViewById(R.id.textView1);
        textView1.setText(contents[position]);
        return rowView;
    }


}
