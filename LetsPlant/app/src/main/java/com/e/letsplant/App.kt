package com.e.letsplant

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class App : Application() {

    companion object {
        var storageReference: StorageReference? = null
            get() {
                if (field == null) field = firebaseStorage!!.reference
                return field
            }
            private set
        var databaseReference: DatabaseReference? = null
            get() {
                if (field == null) field = FirebaseDatabase.getInstance().reference
                return field
            }
            private set
        var firebaseStorage: FirebaseStorage? = null
            get() {
                if (field == null) field = FirebaseStorage.getInstance()
                return field
            }
        private var firebaseAuth: FirebaseAuth? = null
        val firebaseAuthReference: FirebaseAuth?
            get() {
                if (firebaseAuth == null) firebaseAuth = FirebaseAuth.getInstance()
                return firebaseAuth
            }
    }
}