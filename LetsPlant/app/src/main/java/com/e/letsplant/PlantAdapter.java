package com.e.letsplant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.e.letsplant.data.Plant;

import java.util.List;

public class PlantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Plant> plantList;

    public PlantAdapter(List<Plant> plantList) {
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
        final Plant plant = plantList.get(position);

        ((PlantViewHolder)holder).getTitle().setText(plant.getTitle());
        ((PlantViewHolder)holder).getImage().setBackgroundResource(plant.getImage());
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    public static class PlantViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final ImageView image;

        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.title);
            this.image = itemView.findViewById(R.id.image);
        }

        public ImageView getImage() {
            return image;
        }

        public TextView getTitle() {
            return title;
        }
    }
}
