package com.dicsstartup.spot.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dicsstartup.spot.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterImagenes extends RecyclerView.Adapter<AdapterImagenes.MyViewHolder>{

    public ArrayList<Uri> imageList;

    public AdapterImagenes(ArrayList<Uri> imageList, Context context) {
        this.imageList = imageList;

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
                .inflate(R.layout.add_image, parent, false);

        return new MyViewHolder(itemView);
    }
    private long lastClickTime = 0;
    private final long DOUBLE_CLICK_TIME_DELTA = 300;
    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Uri image = imageList.get(position);
        String scheme = image.getScheme();
        if (scheme != null && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
            Picasso.get().load(image).into(holder.imageView);
            holder.imageView.setOnClickListener(v -> {
                holder.imageView.setForeground(holder.itemView.getResources().getDrawable(R.drawable.baseline_delete_24));
                long clickTime=System.currentTimeMillis();
                Toast.makeText(holder.itemView.getContext(), "doble click para eliminar ", Toast.LENGTH_LONG).show();
                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
                    imageList.remove(position);
                    notifyDataSetChanged();
                    holder.imageView.setForeground(null);
                }
                lastClickTime=clickTime;
            });
        } else {
            holder.imageView.setImageURI(image);
            holder.imageView.setOnClickListener(v -> {
                holder.imageView.setForeground(holder.itemView.getResources().getDrawable(R.drawable.baseline_delete_24));
                long clickTime=System.currentTimeMillis();
                Toast.makeText(holder.itemView.getContext(), "doble click para eliminar ", Toast.LENGTH_LONG).show();
                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
                    imageList.remove(position);
                    notifyDataSetChanged();
                    holder.imageView.setForeground(null);
                }
                lastClickTime=clickTime;
            });
        }


    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}

