package com.khdamte.bitcode.khdamte_app.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.activities.MainActivity;
import com.khdamte.bitcode.khdamte_app.models.Flags_Model;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Nationalty_Maids_Adapter extends RecyclerView.Adapter<Nationalty_Maids_Adapter.ViewHolder> {

    private ArrayList<Flags_Model> mDataset;
    private Context context ;

    public Nationalty_Maids_Adapter(Context context, ArrayList<Flags_Model> myDataset) {
        this.mDataset = myDataset;
        this.context  = context ;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public Nationalty_Maids_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.details_nation_row_layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String nat_img = mDataset.get(position).getFlag_img();
        String nat_name = mDataset.get(position).getFlag_name();

        Picasso.with(context).load(nat_img).into(holder.flag_img);
        holder.country_text.setText(nat_name);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView flag_img;
        private TextView country_text ;
        private Typeface lightFace ;

        private ViewHolder(View v) {
            super(v);

            lightFace = MainActivity.lightFace ;
            flag_img = (ImageView) v.findViewById(R.id.flag_imgview);
            country_text = (TextView) v.findViewById(R.id.country_tv);

            country_text.setTypeface(lightFace);
        }
    }
}