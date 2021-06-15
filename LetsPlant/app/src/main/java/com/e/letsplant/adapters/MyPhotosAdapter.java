package com.e.letsplant.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.e.letsplant.R;
import com.e.letsplant.data.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class MyPhotosAdapter extends MainAdapter {
    private final Context context;
    private final List<Post> posts;

    public MyPhotosAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post post = posts.get(position);

        Glide.with(context).load(post.getPostImage()).into(((ViewHolder) holder).post_image);

        ((ViewHolder) holder).post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit_post:
                                editPost(post.getPostId());
                                return true;
                            case R.id.delete_post:
                                databaseReference.child("Posts").child(post.getPostId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if (task.isSuccessful())
                                            Toast.makeText(context, "Post deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.post_menu);
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView post_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_image = itemView.findViewById(R.id.post_image);
        }
    }

    private void editPost(String postId) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Edit Post");

        EditText editText = new EditText(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        editText.setLayoutParams(layoutParams);
        alertDialog.setView(editText);

        getText(postId, editText);

        alertDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("description", editText.getText().toString());

                databaseReference.child("Posts").child(postId).updateChildren(hashMap);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void getText(String postId, final EditText editText) {
        databaseReference.child("Posts").child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                editText.setText(snapshot.getValue(Post.class).getDescription());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
