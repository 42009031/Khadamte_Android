package com.khdamte.bitcode.khdamte_app.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.activities.MainActivity;
import com.khdamte.bitcode.khdamte_app.models.PhoneModel;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Amado on 4/7/2017.
 */

public class PhoneAdapter extends BaseAdapter {

    private Context context ;
    private ArrayList<PhoneModel> phoneArrayList ;
    private Typeface lightFace ;
    private LayoutInflater layoutInflater;
    private SharedPreferences languagepref;

    public PhoneAdapter(Context context, ArrayList<PhoneModel> phoneArrayList){
        this.context = context ;
        this.phoneArrayList = phoneArrayList ;
        lightFace = MainActivity.lightFace;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return phoneArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return phoneArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.phones_office_row_layout, null);
            holder = new ViewHolder();

            holder.call_btn = (ImageView) convertView.findViewById(R.id.call_imgView);
            holder.phone_img = (ImageView) convertView.findViewById(R.id.phone_img);

            holder.name_tv = (TextView)convertView.findViewById(R.id.phone_name_tv);
            holder.number_tv = (TextView)convertView.findViewById(R.id.phone_number_tv);

            holder.name_tv.setTypeface(lightFace);
            holder.number_tv.setTypeface(lightFace, Typeface.BOLD);

            languagepref = context.getSharedPreferences("language", MODE_PRIVATE);
            String langToLoad = languagepref.getString("languageToLoad", null);
            if (langToLoad.contains("العربية")) {
                holder.call_btn.setImageResource(R.drawable.call_ar);
            } else {
                holder.call_btn.setImageResource(R.drawable.call);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name_tv.setText(phoneArrayList.get(position).getPhone_name());
        holder.number_tv.setText(phoneArrayList.get(position).getPhone_number());

        return convertView;
    }
    static class ViewHolder{
        TextView name_tv, number_tv;
        ImageView call_btn , phone_img;
    }


}
