package com.e.letsplant.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.e.letsplant.data.Post;
import com.e.letsplant.R;
import com.e.letsplant.data.User;
import com.e.letsplant.fragments.CommentsFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends MainAdapter {
    private final Context context;
    private final List<Post> posts;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post post = posts.get(position);
        Glide.with(context).load(post.getPostImage()).into(((PostViewHolder) holder).post_image);
        if (post.getDescription().equals(""))
            ((PostViewHolder) holder).description.setVisibility(View.GONE);
        else {
            ((PostViewHolder) holder).description.setVisibility(View.VISIBLE);
            ((PostViewHolder) holder).description.setText(post.getDescription());
        }

        publisherInfo(
                ((PostViewHolder) holder).image_profile,
                ((PostViewHolder) holder).username,
                ((PostViewHolder) holder).publisher, post.getPublisher()
        );
        isLiked(post.getPostId(), ((PostViewHolder) holder).like);
        nrLikes(((PostViewHolder) holder).likes, post.getPostId());
        getComments(post.getPostId(), ((PostViewHolder) holder).comments);
        isSaved(post.getPostId(), ((PostViewHolder) holder).save);

        ((PostViewHolder) holder).save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((PostViewHolder) holder).save.getTag().equals("save")) {
                    databaseReference.child(DB_SAVES).child(userUid).child(post.getPostId()).setValue(true);
                } else {
                    databaseReference.child(DB_SAVES).child(userUid).child(post.getPostId()).removeValue();
                }
            }
        });

        ((PostViewHolder) holder).like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((PostViewHolder) holder).like.getTag().equals("like")) {
                    databaseReference.child(DB_LIKES).child(post.getPostId()).child(userUid).setValue(true);
                } else {
                    databaseReference.child(DB_LIKES).child(post.getPostId()).child(userUid).removeValue();
                }
            }
        });

        ((PostViewHolder) holder).comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) v.getContext();
                Fragment fragment = CommentsFragment.getInstance();
                Bundle bundle = new Bundle();
                bundle.putString("postId", post.getPostId());
                bundle.putString("publisherId", post.getPublisher());
                fragment.setArguments(bundle);
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });

        ((PostViewHolder) holder).comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) v.getContext();
                Fragment fragment = CommentsFragment.getInstance();
                Bundle bundle = new Bundle();
                bundle.putString("postId", post.getPostId());
                bundle.putString("publisherId", post.getPublisher());
                fragment.setArguments(bundle);
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView image_profile, post_image, like, comment, save;
        TextView username, likes, comments, publisher, description;

        public PostViewHolder(View itemView) {
            super(itemView);

            this.image_profile = itemView.findViewById(R.id.image_profile);
            this.post_image = itemView.findViewById(R.id.post_image);
            this.like = itemView.findViewById(R.id.like);
            this.comment = itemView.findViewById(R.id.comment);
            this.save = itemView.findViewById(R.id.save);
            this.username = itemView.findViewById(R.id.username);
            this.likes = itemView.findViewById(R.id.likes);
            this.comments = itemView.findViewById(R.id.comments);
            this.publisher = itemView.findViewById(R.id.publisher);
            this.description = itemView.findViewById(R.id.description);
        }
    }

    private void getComments(String postId, TextView comments) {
        databaseReference.child(DB_COMMENTS).child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.setText("View all " + snapshot.getChildrenCount() + " comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isLiked(String postid, ImageView imageView) {
        databaseReference.child(DB_LIKES).child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userUid).exists()) {
                    imageView.setImageResource(R.drawable.ic_heart_red);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_heart);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void nrLikes(TextView likes, String postid) {
        databaseReference.child(DB_LIKES).child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void publisherInfo(ImageView image_profile, TextView username, TextView publisher, String userId) {
        databaseReference.child(DB_USERS).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(context).load(user.getProfileImage()).into(image_profile);
                username.setText(user.getUsername());
                publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isSaved(String postId, ImageView imageView) {
        databaseReference.child(DB_SAVES).child(userUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).exists()) {
                    imageView.setImageResource(R.drawable.ic_bookmark);
                    imageView.setTag("saved");
                } else {
                    imageView.setImageResource(R.drawable.ic_bookmark_save);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
