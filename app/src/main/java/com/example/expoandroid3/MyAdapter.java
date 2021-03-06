package com.example.expoandroid3;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private int layout;
    private ArrayList<Image> images;

    public MyAdapter(int layout, ArrayList<Image> images) {
        this.layout = layout;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.Bind(images.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imagen;
        public TextView name;


        public ViewHolder(View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.content_image);
            name = itemView.findViewById(R.id.content_name);
        }

        public void Bind(Image image) {
            name.setText(image.getName());
            Picasso.get().load(image.getImageUrl()).into(imagen);
        }
    }
}
