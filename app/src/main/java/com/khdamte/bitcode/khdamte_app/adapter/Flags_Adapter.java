package com.khdamte.bitcode.khdamte_app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.models.Flags_Model;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Flags_Adapter extends RecyclerView.Adapter<Flags_Adapter.ViewHolder> {

    private ArrayList<Flags_Model> mDataset;
    private Context context ;

    public Flags_Adapter(Context context, ArrayList<Flags_Model> myDataset) {
        this.mDataset = myDataset;
        this.context  = context ;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public Flags_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.flags_row_layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String img = mDataset.get(position).getFlag_img();
        Picasso.with(context).load(img).into(holder.flag_img);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView flag_img;

        private ViewHolder(View v) {
            super(v);
            flag_img = (ImageView) v.findViewById(R.id.flag_imgview);
        }
    }
}