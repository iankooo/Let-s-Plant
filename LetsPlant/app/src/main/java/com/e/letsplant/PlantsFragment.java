package com.e.letsplant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.e.letsplant.data.Plant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlantsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlantsFragment extends Fragment {

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;
    private List<Plant> plantList;
    private RecyclerView recyclerView;
    private PlantAdapter plantAdapter;
    private ImageView uploadImageView;
    CardView cardView;
    ImageView imageView;

    FirebaseStorage storage;
    StorageReference storageReference;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlantsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlantsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlantsFragment newInstance(String param1, String param2) {
        PlantsFragment fragment = new PlantsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_plants, container, false);

        cardView = rootView.findViewById(R.id.cardView);
        imageView = rootView.findViewById(R.id.imageView);
        uploadImageView = rootView.findViewById(R.id.uploadImageView);

        plantList = new ArrayList<>();
        preparePlant();
        recyclerView = rootView.findViewById(R.id.plantRecyclerView);
        plantAdapter = new PlantAdapter(plantList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

//        plantAdapter.setOnItemClickListener(new ClickListener<Plant>() {
//            @Override
//            public void onItemClick(Plant data) {
//                Toast.makeText(MainActivity.this, data.getTitle(), Toast.LENGTH_SHORT).show();
//            }
//        });
        recyclerView.setAdapter(plantAdapter);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        uploadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.top_plants_bar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_camera) {
            Toast.makeText(getActivity(), "camera", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_photo_library) {
            SelectImage();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                cardView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NotNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        }
    }

    private void preparePlant() {
        Plant plant = new Plant("Cactus", R.drawable.cactus);
        plantList.add(plant);
        plant = new Plant("Zambila", R.drawable.zambila);
        plantList.add(plant);
        plant = new Plant("Busuioc", R.drawable.busuioc);
        plantList.add(plant);
        plant = new Plant("Floarea-soarelui", R.drawable.floarea_soarelui);
        plantList.add(plant);
        plant = new Plant("Lavanda", R.drawable.lavanda);
        plantList.add(plant);
        plant = new Plant("Rozmarin", R.drawable.rozmarin);
        plantList.add(plant);
        plant = new Plant("Orhidee", R.drawable.orhidee);
        plantList.add(plant);
    }
}