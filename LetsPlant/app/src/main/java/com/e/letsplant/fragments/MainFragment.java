package com.e.letsplant.fragments;

import android.content.Intent;

import androidx.fragment.app.Fragment;

public abstract class MainFragment  extends Fragment {
    protected final int PICK_IMAGE_REQUEST = 22;

    protected void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }
}
