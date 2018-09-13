package com.khdamte.bitcode.khdamte_app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.activities.MainActivity;
import com.khdamte.bitcode.khdamte_app.models.Helper;
import com.khdamte.bitcode.khdamte_app.models.Office_Model;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class Office_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Office_Model> mDataset;
    private Context context;

    public Office_Adapter(Context context, ArrayList<Office_Model> mDataset) {
        this.mDataset = mDataset;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 666) {
            return new AdsHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.office_row_ads_layout, parent, false));
        } else {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_office_row_layout, parent, false));
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
            String office_image = mDataset.get(position).getOffice_img();
            String office_name = mDataset.get(position).getOffice_name();
            String office_desc = mDataset.get(position).getOffice_desc();
            myViewHolder.office_name.setText(office_name);
            myViewHolder.office_desc.setText(office_desc);

            myViewHolder.office_name.setTypeface(Helper.getTypeFace());
            myViewHolder.office_desc.setTypeface(Helper.getTypeFace());

            try {
                int image = Integer.parseInt(office_image);
                myViewHolder.office_imgview.setImageResource(image);
            } catch (Exception ex) {
                Picasso.with(context).load(office_image).into(myViewHolder.office_imgview);
            }

            String officeName = myViewHolder.office_name.getText().toString();
            if (officeName.contains("جمعيه") || officeName.contains("جمعية") || officeName.contains("group") || officeName.contains("association"))
                myViewHolder.off_details_layout.setBackgroundResource(R.drawable.office_bg_orange);
            else
                myViewHolder.off_details_layout.setBackgroundResource(R.drawable.office_bg_red);
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

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView office_imgview;
        TextView office_name, office_desc;
        LinearLayout off_details_layout;

        public MyViewHolder(View itemView) {
            super(itemView);
            office_imgview = (ImageView) itemView.findViewById(R.id.office_imgview);
            office_name = (TextView) itemView.findViewById(R.id.office_name_tv);
            office_desc = (TextView) itemView.findViewById(R.id.office_desc_tv);
            off_details_layout = (LinearLayout) itemView.findViewById(R.id.off_details_layout);

            office_name.setTypeface(Helper.getTypeFace());
            office_desc.setTypeface(Helper.getTypeFace());
        }
    }

    class AdsHolder extends RecyclerView.ViewHolder {
        AdView mAdView;

        public AdsHolder(View itemView) {
            super(itemView);
            mAdView = (AdView) itemView.findViewById(R.id.office_adView);
        }
    }

}
