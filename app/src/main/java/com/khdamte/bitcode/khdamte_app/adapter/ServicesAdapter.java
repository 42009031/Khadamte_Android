package com.khdamte.bitcode.khdamte_app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.activities.MainActivity;
import com.khdamte.bitcode.khdamte_app.models.Helper;
import com.khdamte.bitcode.khdamte_app.models.ServicesModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Amado on 7/24/2017.
 */

public class ServicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ServicesModel> mDataset;
    private Context context;

    public ServicesAdapter(Context context, ArrayList<ServicesModel> mDataset) {
        this.mDataset = mDataset;
        this.context = context;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        if (viewType == 666) {
            return new AdsHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.office_row_ads_layout, parent, false));
        } else {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_services_row_layout, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();

        if (viewType == 666) {
            AdsHolder adsHolder = (AdsHolder) holder;

            AdRequest adRequest = new AdRequest.Builder().build();
            adsHolder.mAdView.loadAd(adRequest);

        } else {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            String office_image = mDataset.get(position).getServ_img();
            try {
                int image = Integer.parseInt(office_image);
                myViewHolder.serv_imgview.setImageResource(image);
            } catch (Exception ex) {
                Picasso.with(context).load(office_image).into(myViewHolder.serv_imgview);
            }
            myViewHolder.serv_mob1_tv.setText(mDataset.get(position).getServ_phonr1());
            myViewHolder.office_name_tv.setText(mDataset.get(position).getServ_name());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == 1) {
            return super.getItemViewType(position);
        } else if (position == 2) {
            return 666;
        } else if (position == 3 || position == 4) {
            return super.getItemViewType(position);
        } else if (((position % 5) == 0)) {
            return 666;
        } else {
            return super.getItemViewType(position);
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    class AdsHolder extends RecyclerView.ViewHolder {
        AdView mAdView;
        public AdsHolder(View itemView) {
            super(itemView);
            mAdView = (AdView) itemView.findViewById(R.id.office_adView);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView serv_imgview, phone1_imgView;
        Button serv_mob1_tv;
        TextView office_name_tv;

        public MyViewHolder(View itemView) {
            super(itemView);
            serv_imgview = (ImageView) itemView.findViewById(R.id.serv_imgview);
            serv_mob1_tv = (Button) itemView.findViewById(R.id.serv_mob1_tv);
            phone1_imgView = (ImageView) itemView.findViewById(R.id.phone1_imgView);
            office_name_tv = (TextView) itemView.findViewById(R.id.serv_name_tv);

            serv_mob1_tv.setTypeface(Helper.getTypeFace());
            office_name_tv.setTypeface(Helper.getTypeFace());
        }
    }

}