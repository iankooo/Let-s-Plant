package com.e.letsplant.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.e.letsplant.R;

import org.jetbrains.annotations.NotNull;

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
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            title = getArguments().getString("title", "");
            image = getArguments().getString("image", "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.top_plant_detailed_bar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_post) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}