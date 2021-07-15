package com.example.rocketcat.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rocketcat.R;

import java.util.List;

public class MyGalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Integer> imgs;


    public MyGalleryAdapter(List<Integer> imgs) {
        this.imgs = imgs;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;


        MyViewHolder(View view) {
            super(view);
            this.imageView = view.findViewById(R.id.iv_content);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_gallery, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Glide.with(holder.itemView.getContext())
                .asBitmap()
                .load(imgs.get(position))
                .into(((MyViewHolder) holder).imageView);

    }

    @Override
    public int getItemCount() {
        return imgs.size();
    }
}
