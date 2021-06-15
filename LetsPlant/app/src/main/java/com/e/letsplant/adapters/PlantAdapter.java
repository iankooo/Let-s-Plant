package com.e.letsplant.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.e.letsplant.R;
import com.e.letsplant.data.Plant;
import com.e.letsplant.fragments.CommentsFragment;
import com.e.letsplant.fragments.PlantDetailedFragment;

import java.util.List;

import static android.graphics.Color.green;
import static android.graphics.Color.red;
import static com.e.letsplant.fragments.MainFragment.DB_PLANTS;

public class PlantAdapter extends MainAdapter {
    private final Context context;
    private final List<Plant> plantList;

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
        Plant plant = plantList.get(position);
        String plantId = plant.getPlantId();
        String title = plant.getTitle();
        String image = plant.getImage();
        int moisture = plant.getMoisture();
        float temp = plant.getTemperature();
        String temperature = temp + " \u2103";
        int light = plant.getLight();
        int humidity = plant.getHumidity();

        ((PlantViewHolder) holder).title.setText(title);

        Log.d("GLIDE", plant.getImage());
        Glide.with(context)
                .load(image)
                .into(((PlantViewHolder) holder).image);

        if (moisture > 0 && moisture < 90) {
            ((PlantViewHolder) holder).moisture.setVisibility(View.VISIBLE);
            ((PlantViewHolder) holder).moisture.setImageResource(R.drawable.ic_water_red);
            ((PlantViewHolder) holder).moistureTextView.setVisibility(View.VISIBLE);
            ((PlantViewHolder) holder).moistureTextView.setText("moisture: bad");
        } else if (moisture >= 90 && moisture < 95) {
            ((PlantViewHolder) holder).moisture.setVisibility(View.VISIBLE);
            ((PlantViewHolder) holder).moisture.setImageResource(R.drawable.ic_water_green);
            ((PlantViewHolder) holder).moistureTextView.setVisibility(View.VISIBLE);
            ((PlantViewHolder) holder).moistureTextView.setText("moisture: good");
        } else if (moisture >= 95) {
            ((PlantViewHolder) holder).moisture.setVisibility(View.VISIBLE);
            ((PlantViewHolder) holder).moisture.setImageResource(R.drawable.ic_water);
            ((PlantViewHolder) holder).moistureTextView.setVisibility(View.VISIBLE);
            ((PlantViewHolder) holder).moistureTextView.setText("moisture: very good");
        }

        if (light == 0) {
            ((PlantViewHolder) holder).light.setVisibility(View.VISIBLE);
            ((PlantViewHolder) holder).light.setImageResource(R.drawable.ic_moon);
        }
        if (light == 1){
            ((PlantViewHolder) holder).light.setVisibility(View.VISIBLE);
            ((PlantViewHolder) holder).light.setImageResource(R.drawable.ic_sun);
        }

        if (humidity > 0) {
            String h = "humidity: " + humidity + "%";
            ((PlantViewHolder) holder).humidityTextView.setVisibility(View.VISIBLE);
            ((PlantViewHolder) holder).humidityTextView.setText(h);
        }

        if (temp > -99){
            ((PlantViewHolder) holder).temperatureTextView.setVisibility(View.VISIBLE);
            ((PlantViewHolder) holder).temperatureTextView.setText(temperature);
        }

        ((PlantViewHolder) holder).image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) v.getContext();
                Fragment fragment = PlantDetailedFragment.getInstance();
                Bundle bundle = new Bundle();
                bundle.putString("plantId", plantId);
                bundle.putString("title", title);
                bundle.putString("image", image);
                fragment.setArguments(bundle);
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });

        ((PlantViewHolder) holder).deletePlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePlant(plantId, title);
            }
        });
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    public static class PlantViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        public final ImageView image;
        public final ImageView deletePlant;
        public final ImageView moisture;
        public final TextView moistureTextView;
        public final TextView temperatureTextView;
        public final ImageView light;
        public final TextView humidityTextView;

        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.title);
            this.image = itemView.findViewById(R.id.image);
            this.moisture = itemView.findViewById(R.id.moisture);
            this.moistureTextView = itemView.findViewById(R.id.moistureTextView);
            this.temperatureTextView = itemView.findViewById(R.id.temperatureTextView);
            this.light = itemView.findViewById(R.id.lightImageView);
            this.humidityTextView = itemView.findViewById(R.id.humidityTextView);
            this.deletePlant = itemView.findViewById(R.id.deletePlant);
        }
    }

    private void deletePlant(String id, String title) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(title);
        alert.setMessage("Delete this plant?");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                databaseReference.child(DB_PLANTS).child(id).removeValue();
            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }
}
