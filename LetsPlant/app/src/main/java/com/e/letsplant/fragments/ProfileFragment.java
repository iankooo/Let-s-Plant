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

import com.bumptech.glide.Glide;
import com.e.letsplant.R;
import com.e.letsplant.data.User;
import com.e.letsplant.interfaces.ProfileSettingsEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class ProfileFragment extends MainFragment {
    private ProfileSettingsEventListener profileSettingsEventListener;
    private boolean isProfileSettingsOpen = false;

    private EditText username, email, phone, location;
    private ImageView profileImage;
    private TextView titleFragmentProfileTextView;
    private View rootView;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser firebaseUser;
    private String userUid;
    private DatabaseReference mDatabase;

    private Menu menu;

    private Uri filePath;

    String Database_Path = "All_Users_Information_Realtime_Database";

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = rootView.findViewById(R.id.profileImageView);
        username = rootView.findViewById(R.id.usernameEditText);
        email = rootView.findViewById(R.id.emailEditText);
        phone = rootView.findViewById(R.id.phoneEditText);
        location = rootView.findViewById(R.id.locationEditText);
        titleFragmentProfileTextView = rootView.findViewById(R.id.titleFragmentProfileTextView);

        initialize(false);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        firebaseUser = fAuth.getCurrentUser();
        userUid = firebaseUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference(Database_Path + "/");

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading profile");
        progressDialog.show();

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (!user.getProfileImage().equals("")) {
                    Glide.with(getContext())
                            .load(user.getProfileImage())
                            .into(profileImage);
                } else
                    profileImage.setImageResource(R.drawable.ic_user_rounded);
                username.setText(user.getUsername());
                email.setText(user.getEmail());
                phone.setText(user.getPhone());
                location.setText(user.getLocation());
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child(userUid).addValueEventListener(userListener);

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
            email.setBackgroundResource(android.R.color.transparent);
            username.setBackgroundResource(android.R.color.transparent);
            phone.setBackgroundResource(android.R.color.transparent);
            location.setBackgroundResource(android.R.color.transparent);
        } else {
            titleFragmentProfileTextView.setText("Edit Profile");
            if (profileImage == null)
                profileImage.setImageResource(R.drawable.ic_add_user_rounded);
            email.setBackgroundResource(android.R.drawable.edit_text);
            username.setBackgroundResource(android.R.drawable.edit_text);
            phone.setBackgroundResource(android.R.drawable.edit_text);
            location.setBackgroundResource(android.R.drawable.edit_text);

            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectImage();
                }
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
        User user = new User(email, location, phone, profileImage, username);
        Map<String, Object> userValues = user.toMap();

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put(userUid, userValues);

        mDatabase.updateChildren(userUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    initialize(false);
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(false);
                    menu.getItem(2).setVisible(true);
                }
            }
        });
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
    }
}