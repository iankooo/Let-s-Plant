package com.e.letsplant.fragments

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.view.View
import android.webkit.MimeTypeMap
import androidx.fragment.app.Fragment
import com.e.letsplant.App
import com.e.letsplant.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference

abstract class MainFragment : Fragment() {
    var storageReference: StorageReference? = App.storageReference
    var databaseReference: DatabaseReference? = App.databaseReference
    var firebaseAuth: FirebaseAuth? = App.firebaseAuthReference
    var rootView: View? = null
    protected var currentUserInfo: User = User()
    protected fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Image from here..."),
            PICK_IMAGE_REQUEST
        )
    }

    fun getFileExtension(uri: Uri?): String? {
        val contentResolver: ContentResolver = requireActivity().contentResolver
        val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType((uri)!!))
    }

    companion object {
        const val PICK_IMAGE_REQUEST: Int = 22
        const val DB_PLANTS: String = "Plants"
        const val DB_USERS: String = "Users"
        const val DB_COMMENTS: String = "Comments"
        const val DB_FOLLOW: String = "Follow"
        const val DB_POSTS: String = "Posts"
        const val DB_SAVES: String = "Saves"
        const val ALL_USER_PROFILE_IMAGES: String = "ALL_USER_PROFILE_IMAGES"
        const val ALL_PLANT_IMAGES: String = "All_Image_Uploads/"
    }
}