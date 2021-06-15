package com.e.letsplant.fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.MimeTypeMap;

import androidx.fragment.app.Fragment;

import com.e.letsplant.App;
import com.e.letsplant.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public abstract class MainFragment extends Fragment {
    public static final int PICK_IMAGE_REQUEST = 22;

    public static final String DB_PLANTS = "Plants";
    public static final String DB_USERS = "Users";
    public static final String DB_COMMENTS = "Comments";
    public static final String DB_FOLLOW = "Follow";
    public static final String DB_POSTS = "Posts";
    public static final String DB_SAVES = "Saves";

    public static final String ALL_USER_PROFILE_IMAGES = "ALL_USER_PROFILE_IMAGES";
    public static final String ALL_PLANT_IMAGES = "All_Image_Uploads/";

    StorageReference storageReference = App.getStorageReference();
    DatabaseReference databaseReference = App.getDatabaseReference();
    FirebaseStorage firebaseStorage = App.getFirebaseStorage();
    FirebaseAuth firebaseAuth = App.getFirebaseAuthReference();

    View rootView;

    protected User currentUserInfo = new User();

    protected void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getActivity().getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }
}
