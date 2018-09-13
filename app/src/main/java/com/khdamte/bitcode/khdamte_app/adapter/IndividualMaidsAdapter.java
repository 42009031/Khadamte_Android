package com.khdamte.bitcode.khdamte_app.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.models.Helper;
import com.khdamte.bitcode.khdamte_app.models.IndividualMaidsModel;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;
import java.util.ArrayList;

public class IndividualMaidsAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Context context;
    private ArrayList<IndividualMaidsModel> indMaidsList;
    private SharedPreferences languagepref;
    private String langToLoad;

    public IndividualMaidsAdapter(Context context, ArrayList<IndividualMaidsModel> indMaidsList ){
        this.context = context ;
        this.indMaidsList = indMaidsList;
        languagepref = context.getSharedPreferences("language", Context.MODE_PRIVATE);
        langToLoad = languagepref.getString("languageToLoad", null);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 666) {
            return new AdsHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.office_row_ads_layout, parent, false));
        } else {
            return new MaidsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_maids_individuals_row_layout, parent, false));
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
            final MaidsViewHolder myViewHolder = (MaidsViewHolder) holder;
            myViewHolder.name.setText(indMaidsList.get(position).getName());
            myViewHolder.desc.setText(indMaidsList.get(position).getDesc());
            myViewHolder.price.setText(indMaidsList.get(position).getPrice());

            if(indMaidsList.get(position).getNationality().contains(",")){
                String [] country = indMaidsList.get(position).getNationality().split(",");
                String ar = country[1];
                String eng = country[0];
                if (langToLoad.equals("العربية")) {
                    myViewHolder.nationality.setText(ar);
                }else{
                    myViewHolder.nationality.setText(eng);
                }
            }else {
                myViewHolder.nationality.setText(indMaidsList.get(position).getNationality());
            }
            Picasso.with(context).load(indMaidsList.get(position).getImg()).placeholder(R.drawable.empty).into(myViewHolder.maids_img);
            myViewHolder.indicator.hide();
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
        return indMaidsList.size();
    }

        static class MaidsViewHolder extends RecyclerView.ViewHolder {
        public ImageView maids_img;
        TextView name, desc, price, nationality;

        AVLoadingIndicatorView indicator ;

        public MaidsViewHolder(View v) {
            super(v);
            maids_img = (ImageView) v.findViewById(R.id.maids_img);
            name = (TextView) itemView.findViewById(R.id.maids_name_tv);
            desc = (TextView) itemView.findViewById(R.id.maids_desc_tv);
            price = (TextView) itemView.findViewById(R.id.price_tv);
            nationality = (TextView) itemView.findViewById(R.id.nationality_tv);
            indicator = (AVLoadingIndicatorView) itemView.findViewById(R.id.indicator);

            name.setTypeface(Helper.getTypeFace());
            desc.setTypeface(Helper.getTypeFace());
            price.setTypeface(Helper.getTypeFace());
            nationality.setTypeface(Helper.getTypeFace());
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
