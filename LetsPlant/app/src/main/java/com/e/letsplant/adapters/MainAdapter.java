package com.e.letsplant.adapters;

import androidx.recyclerview.widget.RecyclerView;

import com.e.letsplant.App;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;

public abstract class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public static final String DB_USERS = "Users";
    public static final String DB_SAVES = "Saves";
    public static final String DB_LIKES = "Likes";
    public static final String DB_COMMENTS = "Comments";
    public static final String DB_FOLLOW = "Follow";


    DatabaseReference databaseReference = App.getDatabaseReference();
    FirebaseAuth firebaseAuth = App.getFirebaseAuthReference();

    String userUid = firebaseAuth.getUid();
}
