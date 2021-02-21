package com.e.letsplant.fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.e.letsplant.adapters.PlantAdapter;
import com.e.letsplant.R;
import com.e.letsplant.listeners.RecyclerItemClickListener;
import com.e.letsplant.data.Plant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlantsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlantsFragment extends MainFragment {
    private OnItemSelectedListener listener;
    private Uri filePath;
    private final List<Plant> plantList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PlantAdapter plantAdapter;
    private EditText plantNameEditText;
    CardView cardView;
    ImageView imageView;
    String Storage_Path = "All_Image_Uploads/";
    String Database_Path = "All_Image_Uploads_Database";

    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;

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
        ImageView uploadImageView = rootView.findViewById(R.id.uploadImageView);
        plantNameEditText = rootView.findViewById(R.id.plantNameEditText);

        recyclerView = rootView.findViewById(R.id.plantRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
//        plantAdapter.setOnItemClickListener(new ClickListener<Plant>() {
//            @Override
//            public void onItemClick(Plant data) {
//                Toast.makeText(MainActivity.this, data.getTitle(), Toast.LENGTH_SHORT).show();
//            }
//        });
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path + "/" + userUid);


        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading plants");
        progressDialog.show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                plantList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    Plant plantUploadInfo = postSnapshot.getValue(Plant.class);

                    plantList.add(plantUploadInfo);
                }

                plantAdapter = new PlantAdapter(getContext(), plantList);

                recyclerView.setAdapter(plantAdapter);

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
                progressDialog.dismiss();
            }
        });

        uploadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener
                .OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Toast.makeText(getContext(),"clicked" + position, Toast.LENGTH_SHORT).show();
//                PlantDetailedFragment plantDetailedFragment = new PlantDetailedFragment();
//                getActivity().getSupportFragmentManager()
//                        .beginTransaction()
////                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_left, R.anim.exit_to_right)
//                        .replace(R.id.fragment_container, plantDetailedFragment) // replace fragment_container
//                        .addToBackStack(null)
//                        .commit();
                listener.onPlantItemSelected(plantList.get(position));
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(getContext(),"longgggg clicked" + plantList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
            }
        }));

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.top_plants_bar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_camera) {
            TakeImage();
            return true;
        }
        if (id == R.id.action_photo_library) {
            SelectImage();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void TakeImage() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                plantNameEditText.setText("");
                cardView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getActivity().getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void uploadImage() {
        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference ref = storageReference.child(Storage_Path + "/" + userUid + System.currentTimeMillis() + "." + GetFileExtension(filePath));

            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (taskSnapshot.getMetadata() != null) {
                        if (taskSnapshot.getMetadata().getReference() != null) {
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();

                                    cardView.setVisibility(View.GONE);
                                    String tempImageName = plantNameEditText.getText().toString().trim();

                                    progressDialog.dismiss();

                                    Toast.makeText(getActivity(), "Image Uploaded!!", Toast.LENGTH_SHORT).show();

                                    Plant plantUploadInfo = new Plant(tempImageName, imageUrl);
                                    String imageUploadId = databaseReference.push().getKey();
                                    databaseReference.child(Objects.requireNonNull(imageUploadId)).setValue(plantUploadInfo);

                                }
                            });
                        }
                    }
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

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if(context instanceof OnItemSelectedListener){      // context instanceof YourActivity
            this.listener = (OnItemSelectedListener) context; // = (YourActivity) context
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement PlantsFragment.OnItemSelectedListener");
        }
    }

    public interface OnItemSelectedListener {
        // This can be any number of events to be sent to the activity
        void onPlantItemSelected(Plant plant);
    }
}