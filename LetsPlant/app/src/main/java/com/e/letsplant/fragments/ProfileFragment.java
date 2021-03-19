package com.e.letsplant.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
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
import com.e.letsplant.data.User;
import com.e.letsplant.data.UserViewModel;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class ProfileFragment extends MainFragment {
    private ProfileSettingsEventListener profileSettingsEventListener;
    private boolean isProfileSettingsOpen = false;

    private TextView titleFragmentProfileTextView;
    private ImageView profileImage;
    private EditText email;
    private EditText username;
    private EditText phone;
    private EditText location;
    private Menu menu;
    private Uri filePath;

    private UserViewModel userViewModel;

    private double latitude = 0;
    private double longitude = 0;

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

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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

        titleFragmentProfileTextView = rootView.findViewById(R.id.titleFragmentProfileTextView);
        profileImage = rootView.findViewById(R.id.profileImageView);
        email = rootView.findViewById(R.id.emailEditText);
        username = rootView.findViewById(R.id.usernameEditText);
        phone = rootView.findViewById(R.id.phoneEditText);
        location = rootView.findViewById(R.id.locationEditText);

        initialize(false);

        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(USERS_INFORMATION_REALTIME_DATABASE + "/" + userUid);

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading profile");
        progressDialog.show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUserInfo = snapshot.getValue(User.class);
                if (currentUserInfo == null) {
                    Log.e(TAG, "User data is null");
                    progressDialog.dismiss();
                    return;
                }
                if (!currentUserInfo.getProfileImage().equals("null") && !currentUserInfo.getProfileImage().equals("")) {
                    Glide.with(getContext())
                            .load(currentUserInfo.getProfileImage())
                            .into(profileImage);
                } else {
                    profileImage.setImageResource(R.drawable.ic_user_rounded);
                }
                email.setText(currentUserInfo.getEmail());
                username.setText(currentUserInfo.getUsername());
                phone.setText(currentUserInfo.getPhone());
                location.setText(currentUserInfo.getLocation());

                userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
                userViewModel.setUserMutableLiveData(currentUserInfo);

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadUser:onCancelled", error.toException());
            }
        });

        IntentFilter filter = new IntentFilter("openEditProfileSettings");
        getActivity().registerReceiver(receiverUpdateDownload, filter);

        return rootView;
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
            titleFragmentProfileTextView.setText("Profile");
            if (!currentUserInfo.getProfileImage().equals("null") && !currentUserInfo.getProfileImage().equals("")) {
                Glide.with(getContext())
                        .load(currentUserInfo.getProfileImage())
                        .into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.ic_user_rounded);
            }
            email.setBackgroundResource(android.R.color.transparent);
            username.setBackgroundResource(android.R.color.transparent);
            phone.setBackgroundResource(android.R.color.transparent);
            location.setBackgroundResource(android.R.color.transparent);
            profileImage.setOnClickListener(null);
        } else {
            titleFragmentProfileTextView.setText("Tap to change");
            email.setBackgroundResource(android.R.drawable.edit_text);
            username.setBackgroundResource(android.R.drawable.edit_text);
            phone.setBackgroundResource(android.R.drawable.edit_text);
            location.setBackgroundResource(android.R.drawable.edit_text);

            profileImage.setOnClickListener(v -> SelectImage());

            Places.initialize(getContext(), "");
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

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.setUserMutableLiveData(user);

        Map<String, Object> userValues = user.toMap();

        databaseReference.updateChildren(userValues).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(false);
                    menu.getItem(2).setVisible(true);
                }
            }
        });
        if (!profileImage.equals("")) {
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
                profileImage.setImageBitmap(bitmap);
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

            StorageReference ref = storageReference.child(ALL_USER_PROFILE_IMAGES + "/" + userUid + "." + GetFileExtension(filePath));

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
                                    databaseReference.child("profileImage").setValue(imageUrl);
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
}