package com.e.letsplant.fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.MimeTypeMap;

import androidx.fragment.app.Fragment;

import com.e.letsplant.data.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public abstract class MainFragment  extends Fragment {
    protected final int PICK_IMAGE_REQUEST = 22;
    public static final String ALL_PLANTS_UPLOADS_DATABASE = "All_Image_Uploads_Database";
    public static final String USERS_INFORMATION_REALTIME_DATABASE = "All_Users_Information_Realtime_Database";
    public static final String ALL_USER_PROFILE_IMAGES = "ALL_USER_PROFILE_IMAGES";
    public static final String ALL_PLANT_IMAGES = "All_Image_Uploads/";

    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;

    View rootView;
    String userUid;

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
