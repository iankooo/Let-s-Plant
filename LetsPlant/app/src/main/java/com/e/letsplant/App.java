package com.e.letsplant;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class App extends Application {
    private static StorageReference storageReference = null;
    private static DatabaseReference databaseReference = null;
    private static FirebaseStorage firebaseStorage = null;
    private static FirebaseAuth firebaseAuth = null;

    @Override
    public void onCreate() {
        super.onCreate();
        // init your vars here
    }

    public static DatabaseReference getDatabaseReference() {
        if (databaseReference == null)
            databaseReference = FirebaseDatabase.getInstance().getReference();
        return databaseReference;
    }

    public static FirebaseStorage getFirebaseStorage() {
        if (firebaseStorage == null)
            firebaseStorage = FirebaseStorage.getInstance();
        return firebaseStorage;
    }

    public static StorageReference getStorageReference() {
        if (storageReference == null)
            storageReference = getFirebaseStorage().getReference();
        return storageReference;
    }

    public static FirebaseAuth getFirebaseAuthReference() {
        if (firebaseAuth == null)
            firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth;
    }
}

