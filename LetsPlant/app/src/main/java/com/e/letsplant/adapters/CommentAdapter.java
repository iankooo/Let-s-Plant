package com.e.letsplant.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.e.letsplant.R;
import com.e.letsplant.data.Comment;
import com.e.letsplant.data.User;
import com.e.letsplant.fragments.FeedFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentAdapter extends MainAdapter {

    private final Context context;
    private final List<Comment> comments;

    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent,false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        ((CommentViewHolder) holder).comment.setText(comment.getComment());
        getUserInfo(((CommentViewHolder) holder).image_profile, ((CommentViewHolder) holder).username, comment.getPublisher());

        ((CommentViewHolder) holder).comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) v.getContext();
                Fragment fragment = new FeedFragment();
                Bundle bundle = new Bundle();
                bundle.putString("publisherId", comment.getPublisher());
                fragment.setArguments(bundle);
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });
        ((CommentViewHolder) holder).image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) v.getContext();
                Fragment fragment = new FeedFragment();
                Bundle bundle = new Bundle();
                bundle.putString("publisherId", comment.getPublisher());
                fragment.setArguments(bundle);
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_profile;
        public TextView username, comment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
        }
    }

    private void getUserInfo(ImageView imageView, TextView username, String publisherId) {
        databaseReference.child(DB_USERS).child(publisherId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(context).load(user.getProfileImage()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
