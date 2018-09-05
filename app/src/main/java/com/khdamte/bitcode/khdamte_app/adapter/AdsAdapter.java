package com.khdamte.bitcode.khdamte_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.activities.DisplayAdsActivity;
import com.khdamte.bitcode.khdamte_app.models.AdsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Amado on 7/18/2017.
 */

public class AdsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<AdsModel> adsModels;
    private LayoutInflater layoutInflater;


    public AdsAdapter(Context context, ArrayList<AdsModel> adsModels){
        this.context = context ;
        this.adsModels = adsModels ;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return adsModels.size();
    }

    @Override
    public Object getItem(int position) {
        return adsModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.activity_main_ads_row, null);
            holder = new ViewHolder();
            holder.ads_img = (ImageView) convertView.findViewById(R.id.ads_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Picasso.with(context).load(adsModels.get(position).getAds_img()).into(holder.ads_img);
        holder.ads_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DisplayAdsActivity.class);
                intent.putExtra("ads_img", adsModels.get(position).getAds_img());
                intent.putExtra("office_id", adsModels.get(position).getOffice_id());
                context.startActivity(intent);
            }
        });
        return convertView;
    }
    static class ViewHolder{
        ImageView ads_img;
    }
}