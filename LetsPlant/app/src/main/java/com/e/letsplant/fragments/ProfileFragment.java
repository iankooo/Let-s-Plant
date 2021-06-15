package com.e.letsplant.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.e.letsplant.R;
import com.e.letsplant.adapters.MyPhotosAdapter;
import com.e.letsplant.data.Post;
import com.e.letsplant.data.User;
import com.e.letsplant.interfaces.ProfileSettingsEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class ProfileFragment extends MainFragment {
    private static Fragment instance = null;
    private ProfileSettingsEventListener profileSettingsEventListener;
    private boolean isProfileSettingsOpen = false;

    private TextView posts, friends;
    private ImageView image_profile;
    private Button edit_profile;
    private EditText email, username, phone, location;
    private ImageButton my_photos, saved_photos;

    RecyclerView recyclerView_posts;
    MyPhotosAdapter myPhotosAdapter;
    List<Post> postList;

    List<String> mySaves;
    RecyclerView recyclerView_saves;
    MyPhotosAdapter myPhotosAdapter_saves;
    List<Post> postList_saves;

    private Menu menu;
    private Uri filePath;

    private double latitude = 0;
    private double longitude = 0;

    String profileId;

    String userUid;

    BroadcastReceiver receiverUpdateDownload = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean open = intent.getBooleanExtra("open", false);
            if (open) {
                initialize(true);
                menu.getItem(0).setVisible(true);
                menu.getItem(1).setVisible(true);
                menu.getItem(2).setVisible(false);
            }
        }
    };

    public ProfileFragment() {
    }

    public static Fragment getInstance() {
        if (instance == null)
            instance = new ProfileFragment();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Profile");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        userUid = firebaseAuth.getUid();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = sharedPreferences.getString("profileId", "none");

        edit_profile = rootView.findViewById(R.id.edit_profile);
        posts = rootView.findViewById(R.id.posts);
        friends = rootView.findViewById(R.id.friends);
        image_profile = rootView.findViewById(R.id.profileImageView);
        email = rootView.findViewById(R.id.emailEditText);
        username = rootView.findViewById(R.id.usernameEditText);
        phone = rootView.findViewById(R.id.phoneEditText);
        location = rootView.findViewById(R.id.locationEditText);
        my_photos = rootView.findViewById(R.id.my_photos);
        saved_photos = rootView.findViewById(R.id.saved_photos);

        recyclerView_posts = rootView.findViewById(R.id.recycler_view_photos);
        recyclerView_posts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView_posts.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        myPhotosAdapter = new MyPhotosAdapter(getContext(), postList);
        recyclerView_posts.setAdapter(myPhotosAdapter);

        recyclerView_saves = rootView.findViewById(R.id.recycler_view_saved_photos);
        recyclerView_saves.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_saves = new GridLayoutManager(getContext(), 2);
        recyclerView_saves.setLayoutManager(linearLayoutManager_saves);
        postList_saves = new ArrayList<>();
        myPhotosAdapter_saves = new MyPhotosAdapter(getContext(), postList_saves);
        recyclerView_saves.setAdapter(myPhotosAdapter_saves);

        recyclerView_posts.setVisibility(View.VISIBLE);
        recyclerView_saves.setVisibility(View.GONE);

        userInfo();
        initialize(false);


        getFriends();
        getNrPosts();
        getPhotos();
        getSaves();

        if (profileId.equals(userUid)) {
            edit_profile.setVisibility(View.GONE);
        } else {
            edit_profile.setVisibility(View.VISIBLE);
            checkFriend();
            saved_photos.setVisibility(View.GONE);
        }

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = edit_profile.getText().toString();

                switch (btn) {
                    case "edit profile":
                        // go to Edit Profile
                        break;
                    case "add friend":
                        databaseReference.child("Follow").child(userUid).child("friends").child(profileId).setValue(true);
                        break;
                    case "unfriend":
                        databaseReference.child("Follow").child(userUid).child("friends").child(profileId).removeValue();
                        break;
                }
            }
        });

        my_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView_posts.setVisibility(View.VISIBLE);
                recyclerView_saves.setVisibility(View.GONE);
            }
        });
        saved_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView_posts.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.VISIBLE);
            }
        });

        IntentFilter filter = new IntentFilter("openEditProfileSettings");
        getActivity().registerReceiver(receiverUpdateDownload, filter);

        return rootView;
    }

    private void userInfo() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading profile");
        progressDialog.show();

        databaseReference.child(DB_USERS).child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }
                currentUserInfo = snapshot.getValue(User.class);
                if (currentUserInfo == null) {
                    Log.e(TAG, "User data is null");
                    progressDialog.dismiss();
                    return;
                }
                Glide.with(getContext()).load(currentUserInfo.getProfileImage()).into(image_profile);
                filePath = Uri.parse(currentUserInfo.getProfileImage());
                email.setText(currentUserInfo.getEmail());
                username.setText(currentUserInfo.getUsername());
                phone.setText(currentUserInfo.getPhone());
                location.setText(currentUserInfo.getLocation());
                longitude = currentUserInfo.getLongitude();
                latitude = currentUserInfo.getLatitude();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadUser:onCancelled", error.toException());
            }
        });
    }

    private void checkFriend() {
        databaseReference.child(DB_FOLLOW).child(userUid).child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileId).exists())
                    edit_profile.setText("unfriend");
                else
                    edit_profile.setText("add friend");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFriends() {
        databaseReference.child(DB_FOLLOW).child(profileId).child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friends.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNrPosts() {
        databaseReference.child(DB_POSTS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (Objects.requireNonNull(post).getPublisher().equals(profileId))
                        i++;
                }
                posts.setText("" + i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initialize(boolean value) {
        email.setCursorVisible(value);
        email.setLongClickable(value);
        email.setClickable(value);
        email.setFocusable(value);
        email.setSelected(value);

        username.setCursorVisible(value);
        username.setLongClickable(value);
        username.setClickable(value);
        username.setFocusable(value);
        username.setFocusableInTouchMode(value);
        username.setSelected(value);

        phone.setCursorVisible(value);
        phone.setLongClickable(value);
        phone.setClickable(value);
        phone.setFocusable(value);
        phone.setFocusableInTouchMode(value);
        phone.setSelected(value);

        location.setCursorVisible(value);
        location.setLongClickable(value);
        location.setClickable(value);
        location.setFocusable(value);
        location.setFocusableInTouchMode(value);
        location.setSelected(value);

        if (!value) {
            email.setBackgroundResource(android.R.color.transparent);
            username.setBackgroundResource(android.R.color.transparent);
            phone.setBackgroundResource(android.R.color.transparent);
            location.setBackgroundResource(android.R.color.transparent);
            image_profile.setOnClickListener(null);
        } else {
            email.setBackgroundResource(android.R.drawable.edit_text);
            username.setBackgroundResource(android.R.drawable.edit_text);
            phone.setBackgroundResource(android.R.drawable.edit_text);
            location.setBackgroundResource(android.R.drawable.edit_text);

            image_profile.setOnClickListener(v -> SelectImage());

            Places.initialize(getContext(), "AIzaSyAYJE72q4PNe5oH5GbT2OHV186UO3t30qo");
            location.setFocusable(false);
            location.setOnClickListener(v -> {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(getActivity());
                startActivityForResult(intent, 100);
            });
        }
    }

    @Override
    public void onAttach(@NotNull Activity activity) {
        super.onAttach(activity);
        try {
            profileSettingsEventListener = (ProfileSettingsEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ProfileSettingsEventListener");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isProfileSettingsOpen = false;
        profileSettingsEventListener.closeActivityBottomSheet();
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.top_profile_bar_menu, menu);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_menu) {
            if (!isProfileSettingsOpen) {
                isProfileSettingsOpen = true;
                profileSettingsEventListener.openActivityBottomSheet();
            } else {
                isProfileSettingsOpen = false;
                profileSettingsEventListener.closeActivityBottomSheet();
            }
            return true;
        }
        if (item.getItemId() == R.id.action_cancel) {
            initialize(false);
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(true);
            return true;
        }
        if (item.getItemId() == R.id.action_save) {
            updateCurrentUserProfile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateCurrentUserProfile() {
        String email = this.email.getText().toString().trim();
        String username = this.username.getText().toString().trim();
        String phone = this.phone.getText().toString().trim();
        String location = this.location.getText().toString().trim();
        String profileImage = String.valueOf(filePath);

        if (TextUtils.isEmpty(username)) {
            this.username.setError("Username is required!");
            return;
        }
        if (username.length() < 4) {
            this.username.setError("Username must be >= 4  characters");
            return;
        }

        User user = new User(userUid, email, location, latitude, longitude, phone, profileImage, username);

        Map<String, Object> userValues = user.toMap();

        databaseReference.child(DB_USERS).child(userUid).updateChildren(userValues).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(false);
                    menu.getItem(2).setVisible(true);
                }
            }
        });
        if (!profileImage.equals("") && !profileImage.contains("https")) {
            uploadImage();
        }
        initialize(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                image_profile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            location.setText(place.getAddress());
            latitude = place.getLatLng().latitude;
            longitude = place.getLatLng().longitude;

        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Updating...");
            progressDialog.show();

            StorageReference ref = storageReference.child(ALL_USER_PROFILE_IMAGES).child(userUid + "." + GetFileExtension(filePath));

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
                                    progressDialog.dismiss();
                                    databaseReference.child(DB_USERS).child(userUid).child("profileImage").setValue(imageUrl);
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
            });
        }
    }

    private void getPhotos() {
        databaseReference.child(DB_POSTS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId))
                        postList.add(post);
                }
                Collections.reverse(postList);
                myPhotosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSaves() {
        mySaves = new ArrayList<>();
        databaseReference.child(DB_SAVES).child(userUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    mySaves.add(dataSnapshot.getKey());
                }
                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSaves() {
        databaseReference.child(DB_POSTS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList_saves.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);

                    for (String id : mySaves)
                        if (post.getPostId().equals(id))
                            postList_saves.add(post);
                }
                myPhotosAdapter_saves.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}