package com.e.letsplant.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.e.letsplant.R;
import com.google.android.gms.tasks.OnSuccessListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class PlantDetailedFragment extends MainFragment {
    private static Fragment instance = null;
    private String plantId = "";
    private String title = "";
    private String image = "";
    private TextView plantDetailedTextView;
    private ImageView plantDetailedImageView;
    private EditText plantDetailedDescriptionEditView;
    private String userUid;

    public PlantDetailedFragment() {
    }

    public static Fragment getInstance() {
        if (instance == null)
            instance = new PlantDetailedFragment();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            plantId = getArguments().getString("plantId", "");
            title = getArguments().getString("title", "");
            image = getArguments().getString("image", "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userUid = firebaseAuth.getUid();
        return inflater.inflate(R.layout.fragment_plant_detailed, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        plantDetailedTextView = view.findViewById(R.id.plantDetailedTextView);
        plantDetailedImageView = view.findViewById(R.id.plantDetailedImageView);
        plantDetailedDescriptionEditView = view.findViewById(R.id.plantDetailedDescriptionEditView);

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
            uploadPost();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void uploadPost() {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Posting...");
        progressDialog.show();

        String postId = databaseReference.child(DB_POSTS).push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("postId", postId);
        hashMap.put("postImage", image);
        hashMap.put("description", plantDetailedDescriptionEditView.getText().toString());
        hashMap.put("publisher", userUid);

        databaseReference.child(DB_POSTS).child(postId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(), "Posted successfully!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        progressDialog.dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        plantId = "";
        title = "";
        image = "";
    }
}