package com.khdamte.bitcode.khdamte_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.khdamte.bitcode.khdamte_app.R;
import com.khdamte.bitcode.khdamte_app.activities.MainActivity;
import com.khdamte.bitcode.khdamte_app.models.CommentModels;

import java.util.ArrayList;

/**
 * Created by Amado on 8/10/2017.
 */

public class CommentAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CommentModels> commentModelses;
    private LayoutInflater layoutInflater;


    public CommentAdapter(Context context, ArrayList<CommentModels> commentModelses){
        this.context = context ;
        this.commentModelses = commentModelses ;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return commentModelses.size();
    }

    @Override
    public Object getItem(int position) {
        return commentModelses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.comment_row_layout, null);
            holder = new ViewHolder();

            holder.comment_content_tv = (TextView) convertView.findViewById(R.id.comment_content_tv);
            holder.comment_sender_textView = (TextView) convertView.findViewById(R.id.comment_sender_textView);
            holder.comment_date_textView = (TextView) convertView.findViewById(R.id.comment_date_textView);

            holder.comment_content_tv.setTypeface(MainActivity.lightFace);
            holder.comment_sender_textView.setTypeface(MainActivity.lightFace);
            holder.comment_date_textView.setTypeface(MainActivity.lightFace);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.comment_content_tv.setText(commentModelses.get(position).getComment_content());
        holder.comment_sender_textView.setText(commentModelses.get(position).getUsername());
        holder.comment_date_textView.setText(commentModelses.get(position).getDate());

        return convertView;
    }
    static class ViewHolder{
        TextView comment_content_tv, comment_sender_textView, comment_date_textView;
    }
}