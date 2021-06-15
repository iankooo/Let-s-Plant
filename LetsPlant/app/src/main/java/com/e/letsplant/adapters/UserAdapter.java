package com.e.letsplant.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.e.letsplant.R;
import com.e.letsplant.data.User;
import com.e.letsplant.fragments.ProfileFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends MainAdapter {

    private final Context context;
    private List<User> users;

    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User user = users.get(position);

        ((UserViewHolder) holder).actionTextView.setVisibility(View.VISIBLE);
        ((UserViewHolder) holder).userUsernameTextView.setText(user.getUsername());
        Glide.with(context).load(user.getProfileImage()).into(((UserViewHolder) holder).userProfileImageView);

        checkIsFriend(user, ((UserViewHolder) holder).actionTextView);

        ((UserViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileId", user.getId());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ProfileFragment.getInstance()).commit();
            }
        });

        ((UserViewHolder) holder).actionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag().toString().equals("user")) {
                    addFriend(user);
                    v.setTag("friend");
                } else {
                    unFriend(user);
                    v.setTag("user");
                }
            }
        });
    }

    public void updateDataSet(List<User> usersList) {
        this.users = usersList;
        notifyDataSetChanged();
    }

    private void checkIsFriend(User user, final ImageView actionTextView) {
        databaseReference.child(DB_FOLLOW).child(userUid).child("friends")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(user.getId()).exists())
                            actionTextView.setImageResource(R.drawable.ic_user_24dp);
                        else
                            actionTextView.setImageResource(R.drawable.ic_add_user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void addFriend(User newFriend) {
        databaseReference.child(DB_FOLLOW).child(userUid).child("friends").child(newFriend.getId()).setValue(true);
    }

    private void unFriend(User user) {
        databaseReference.child(DB_FOLLOW).child(userUid).child("friends").child(user.getId()).removeValue();
    }

    @Override
    public int getItemCount() {
        return users.size();
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