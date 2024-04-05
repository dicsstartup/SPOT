package com.dicsstartup.spot.utils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dicsstartup.spot.R;

import java.util.ArrayList;

public class AdapterTag extends RecyclerView.Adapter<AdapterTag.MyViewHolder>{

    public  ArrayList<String> tagsSpot;

    public AdapterTag(ArrayList<String> imageList, Context context) {
        this.tagsSpot = imageList;

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tag;
        public LinearLayout linearLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            tag = itemView.findViewById(R.id.tag_item);
            linearLayout=itemView.findViewById(R.id.add_LinearLayout);

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tag, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
       holder.tag.setText(tagsSpot.get(position));
    }

    @Override
    public int getItemCount() {
        return tagsSpot.size();
    }
}

