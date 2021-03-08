package com.e.letsplant.fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.fragment.app.Fragment;

import com.e.letsplant.data.User;

public abstract class MainFragment  extends Fragment {
    protected final int PICK_IMAGE_REQUEST = 22;
    public static final String USERS_INFORMATION_REALTIME_DATABASE = "All_Users_Information_Realtime_Database";
    public static final String ALL_USER_PROFILE_IMAGES = "ALL_USER_PROFILE_IMAGES";

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
