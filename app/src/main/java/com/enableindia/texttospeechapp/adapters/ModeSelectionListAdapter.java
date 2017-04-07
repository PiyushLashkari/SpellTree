package com.enableindia.texttospeechapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.texttospeechapp.R;

public class ModeSelectionListAdapter extends ArrayAdapter<String>  {



	  private final Context context;
private final String[] contents;
//  private final LayoutInflater mInflater;

public ModeSelectionListAdapter(Context context,String[] contents) {

	  super(context, R.layout.mode_selection,contents);
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
