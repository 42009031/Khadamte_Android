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
import com.khdamte.bitcode.khdamte_app.models.Office_Model;
import com.khdamte.bitcode.khdamte_app.ui.RoundedCornersTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Amado on 7/28/2017.
 */

public class FavouritAdapter extends BaseAdapter {

    private Context context ;
    private ArrayList<Office_Model> office_models ;
    private LayoutInflater layoutInflater;

    public FavouritAdapter(Context context, ArrayList<Office_Model> office_models){
        this.context = context ;
        this.office_models = office_models ;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return office_models.size();
    }

    @Override
    public Object getItem(int position) {
        return office_models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.main_office_row_layout, null);
            holder = new ViewHolder();

            holder.office_imgview = (ImageView) convertView.findViewById(R.id.office_imgview);
            holder.office_name = (TextView) convertView.findViewById(R.id.office_name_tv);
            holder.office_desc = (TextView) convertView.findViewById(R.id.office_desc_tv);

            holder.office_name.setTypeface(Helper.getTypeFace());
            holder.office_desc.setTypeface(Helper.getTypeFace());


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String office_image = office_models.get(position).getOffice_img();
        String office_name = office_models.get(position).getOffice_name() ;
        String office_desc = office_models.get(position).getOffice_desc() ;
        holder.office_name.setText(office_name);
        holder.office_desc.setText(office_desc);
        try {
            int image = Integer.parseInt(office_image);
            holder.office_imgview.setImageResource(image);
        } catch (Exception ex) {
            Picasso.with(context).load(office_image).transform(new RoundedCornersTransform()).into(holder.office_imgview);
        }

        return convertView;
    }
    static class ViewHolder{
        ImageView office_imgview;
        TextView office_name, office_desc;
    }
}
