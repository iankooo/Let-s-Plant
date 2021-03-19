package com.e.letsplant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.e.letsplant.R;
import com.e.letsplant.data.User;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

import dagger.multibindings.ElementsIntoSet;

public class UserAdapter extends MainAdapter {
    public static final String USERS_INFORMATION_REALTIME_DATABASE = "All_Users_Information_Realtime_Database";

    private final Context context;
    private final List<User> usersList;
    private final LayoutInflater layoutInflater;

    private final FirebaseAuth firebaseAuth;
    private final String userUid;

    public UserAdapter(Context context, List<User> usersList) {
        this.context = context;
        this.usersList = usersList;
        this.layoutInflater = LayoutInflater.from(context);

        firebaseAuth = FirebaseAuth.getInstance();
        userUid = firebaseAuth.getUid();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User user = usersList.get(position);
        String hisUid = user.getId();
        String userProfileImage = user.getProfileImage();
        String userUsername = user.getUsername();

        ((UserViewHolder)holder).userUsernameTextView.setText(userUsername);
        Glide.with(context)
                .load(userProfileImage)
                .into(((UserViewHolder)holder).userProfileImageView);
        ((UserViewHolder)holder).actionTextView.setImageResource(R.drawable.ic_add_user);
        checkIsFriend(hisUid, ((UserViewHolder)holder), position);
        //isFriendOrNot(hisUid);
        ((UserViewHolder)holder).actionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!user.isFriend())
                    addFriend(user);
                else
                    unFriend(hisUid);
            }
        });
    }

    private void checkIsFriend(String hisUid, UserViewHolder holder, int position) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(USERS_INFORMATION_REALTIME_DATABASE);
        databaseReference.child(userUid).child("Friends").orderByChild("id").equalTo(hisUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.exists()) {
                                holder.actionTextView.setImageResource(R.drawable.ic_user_24dp);
                                usersList.get(position).setFriend(true);
                            } else
                                holder.actionTextView.setImageResource(R.drawable.ic_add_user);
                            //usersList.get(position).setFriend(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void addFriend(User newFriend) {
        Map<String, Object> hashMap = newFriend.toMap();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(USERS_INFORMATION_REALTIME_DATABASE);
        databaseReference.child(userUid).child("Friends").child(newFriend.getId()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void unFriend(String hisUid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(USERS_INFORMATION_REALTIME_DATABASE);
        databaseReference.child(userUid).child("Friends").orderByChild("id").equalTo(hisUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                            if (dataSnapshot.exists()) {
                                dataSnapshot.getRef().removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //unFriend successfully
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed to unFriend
                                            }
                                        });
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImageView;
        TextView userUsernameTextView;
        ImageView actionTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImageView = itemView.findViewById(R.id.userProfileImageView);
            userUsernameTextView = itemView.findViewById(R.id.userUsernameTextView);
            actionTextView = itemView.findViewById(R.id.actionTextView);
        }
    }
}
