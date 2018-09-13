package com.khdamte.bitcode.khdamte_app.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.activities.MainActivity;
import com.khdamte.bitcode.khdamte_app.models.Helper;
import com.khdamte.bitcode.khdamte_app.models.NavigationModel;

import java.util.ArrayList;



/**
 * Created by Ahmed Dawoud on 1/30/2017.
 */

public class Navigation_Adapter extends BaseAdapter{

    private Context context ;
    private ArrayList<NavigationModel> navigation_itemsArrayList ;

    private LayoutInflater layoutInflater;

    public Navigation_Adapter(Context context, ArrayList<NavigationModel> navigation_itemsArrayList){
        this.context = context ;
        this.navigation_itemsArrayList = navigation_itemsArrayList ;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return navigation_itemsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return navigation_itemsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.nav_row_layout, null);
            holder = new ViewHolder();
            holder.image = (ImageView)convertView.findViewById(R.id.nav_imgView);
            holder.title = (TextView)convertView.findViewById(R.id.nav_title_textView);

            holder.title.setTypeface(Helper.getTypeFace());


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.image.setImageResource(navigation_itemsArrayList.get(position).getImg());
        holder.title.setText(navigation_itemsArrayList.get(position).getTitle());
        holder.title.setTypeface(Helper.getTypeFace());
        return convertView;
    }
    static class ViewHolder{
        ImageView image ;
        TextView title ;
    }
}
