package com.e.letsplant.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.e.letsplant.adapters.PlantAdapter;
import com.e.letsplant.R;
import com.e.letsplant.data.PlantAlertItem;
import com.e.letsplant.listeners.RecyclerItemClickListener;
import com.e.letsplant.data.Plant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
    private Uri mImageUri;
    private List<Plant> plantList;
    private RecyclerView recyclerView;
    private PlantAdapter plantAdapter;
    private EditText plantNameEditText;
    private CardView cardView;
    private ImageView imageView;

    private ValueEventListener mDBListener;

    public PlantsFragment() {
    }

    public static PlantsFragment newInstance(String param1, String param2) {
        return new PlantsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("My plants");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_plants, container, false);

        cardView = rootView.findViewById(R.id.cardView);
        imageView = rootView.findViewById(R.id.imageView);
        ImageView uploadImageView = rootView.findViewById(R.id.uploadImageView);
        plantNameEditText = rootView.findViewById(R.id.plantNameEditText);

        recyclerView = rootView.findViewById(R.id.plantRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(ALL_PLANTS_UPLOADS_DATABASE + "/" + userUid);

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading plants");
        progressDialog.show();

        plantList = new ArrayList<>();
        plantAdapter = new PlantAdapter(getContext(), plantList);
        recyclerView.setAdapter(plantAdapter);

        mDBListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                plantList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Plant plantUploadInfo = postSnapshot.getValue(Plant.class);
                    plantUploadInfo.setKey(postSnapshot.getKey());
                    plantList.add(plantUploadInfo);
                }
                plantAdapter.notifyDataSetChanged();
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
                listener.onPlantItemSelected(plantList.get(position));
            }

            @Override
            public void onItemLongClick(View view, int position) {
                PopupMenu popup = new PopupMenu(getContext(), view);
                popup.inflate(R.menu.plant_long_press);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.plant_delete:
                                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                                alert.setTitle(plantList.get(position).getTitle());
                                alert.setMessage("Delete this plant?");
                                alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Plant selectedItem = plantList.get(position);
                                        String selectedKey = selectedItem.getKey();

                                        StorageReference imageRef = firebaseStorage.getReferenceFromUrl(selectedItem.getImage());
                                        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                databaseReference.child(selectedKey).removeValue();
                                            }
                                        });
                                    }
                                });
                                alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                alert.show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        }));
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.top_plants_bar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_plant) {
            final PlantAlertItem[] items = {
                    new PlantAlertItem("Take a photo", R.drawable.ic_photo_camera),
                    new PlantAlertItem("Load from gallery", R.drawable.ic_picture),
            };

            ListAdapter adapter = new ArrayAdapter<PlantAlertItem>(
                    getContext(),
                    android.R.layout.select_dialog_item,
                    android.R.id.text1,
                    items){
                public View getView(int position, View convertView, ViewGroup parent) {
                    //Use super class to create the View
                    View v = super.getView(position, convertView, parent);
                    TextView tv = (TextView)v.findViewById(android.R.id.text1);

                    //Put the image on the TextView
                    tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);

                    //Add margin between image and text (support various screen densities)
                    int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                    tv.setCompoundDrawablePadding(dp5);

                    return v;
                }
            };

            new AlertDialog.Builder(getContext())
                .setTitle("Add new plant")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0)
                            TakeImage();
                        if (item == 1)
                            SelectImage();
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();

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
            mImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mImageUri);
                plantNameEditText.setText("");
                plantNameEditText.setFocusable(true);
                imageView.setImageBitmap(bitmap);
                cardView.setVisibility(View.VISIBLE);
                plantNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus)
                            cardView.setVisibility(View.GONE);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (mImageUri != null) {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference ref = storageReference.child(ALL_PLANT_IMAGES + "/" + userUid + System.currentTimeMillis() + "." + GetFileExtension(mImageUri));

            ref.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(mDBListener);
    }

    public interface OnItemSelectedListener {
        void onPlantItemSelected(Plant plant);
    }
}