package com.e.letsplant.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.e.letsplant.R;
import com.e.letsplant.data.Plant;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class PlantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Plant> plantList;
    Context context;

    public PlantAdapter(Context context, List<Plant> plantList) {
        this.context = context;
        this.plantList = plantList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plant, parent, false);

        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Plant plantUploadInfo = plantList.get(position);
        ((PlantViewHolder)holder).title.setText(plantUploadInfo.getTitle());
        //Picasso.get().load(plantUploadInfo.getImage()).into(((PlantViewHolder) holder).image);
        //Loading image from Glide library.
        Log.d("GLIDE",plantUploadInfo.getImage());
        Glide.with(context)
                .load(plantUploadInfo.getImage())
                .into(((PlantViewHolder) holder).image);
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    public static class PlantViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        public final ImageView image;

        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.title);
            this.image = itemView.findViewById(R.id.image);
        }
    }
}
