package com.e.letsplant.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.e.letsplant.R;

public class PlantDetailedFragment extends Fragment {
    String title = "";
    String image = "";
    TextView plantDetailedTextView;
    ImageView plantDetailedImageView;

    public PlantDetailedFragment() {
    }

    public static PlantDetailedFragment newInstance(String param1, String param2) {
        return new PlantDetailedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title", "");
            image = getArguments().getString("image", "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plant_detailed, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        plantDetailedTextView = view.findViewById(R.id.plantDetailedTextView);
        plantDetailedImageView = view.findViewById(R.id.plantDetailedImageView);

        plantDetailedTextView.setText(title);
        Glide.with(getContext())
                .load(image)
                .into(plantDetailedImageView);
    }
}