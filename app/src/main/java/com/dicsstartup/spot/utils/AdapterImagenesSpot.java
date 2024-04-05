package com.dicsstartup.spot.utils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dicsstartup.spot.R;
import com.dicsstartup.spot.entities.ImagenSpot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterImagenesSpot extends RecyclerView.Adapter<AdapterImagenesSpot.MyViewHolder>{

    public  ArrayList<ImagenSpot> imageListSpot;

    public AdapterImagenesSpot(ArrayList<ImagenSpot> imageList, Context context) {
        this.imageListSpot = imageList;

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public LinearLayout linearLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.add_imageview);
            linearLayout=itemView.findViewById(R.id.add_LinearLayout);

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_imagespot, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Picasso.get().load(imageListSpot.get(position).getUri()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageListSpot.size();
    }
}

