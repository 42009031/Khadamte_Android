package com.khdamte.bitcode.khdamte_app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.activities.MainActivity;
import com.khdamte.bitcode.khdamte_app.models.Helper;

import java.util.ArrayList;


public class SpinnerAdapter extends ArrayAdapter<String> {

    private ArrayList<String> arrayList = new ArrayList<String>();
    private Context context ;

    public SpinnerAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
        super(context, textViewResourceId, objects);
        arrayList = objects;
        this.context = context ;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tv = (TextView) view;
        tv.setTypeface(Helper.getTypeFace());
        if (position == 0) {
            tv.setTextColor(Color.GRAY);
        } else {
            tv.setTextColor(Color.BLACK);
        }
        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        if (position == 0) {
            return true;
        } else {
            return true;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.spinner_item, parent, false);
        TextView label = (TextView) row.findViewById(R.id.spinner_textView);
        label.setTypeface(Helper.getTypeFace());
        label.setText(arrayList.get(position));
        if (this.isEnabled(position)) {
            if(position != 0){
                label.setTextColor(Color.parseColor("#000000"));
            }else{
                label.setTextColor(Color.parseColor("#595959"));
            }
        }
        return row;
    }
}