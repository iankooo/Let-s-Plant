package com.e.letsplant.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.e.letsplant.R;
import com.e.letsplant.adapters.CommentAdapter;
import com.e.letsplant.data.Comment;
import com.e.letsplant.data.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CommentsFragment extends MainFragment {

    private static Fragment instance = null;
    private EditText addComment;
    private ImageView image_profile;

    String userUid;

    private String postId;
    private String publisherId;

    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    public CommentsFragment() {
    }

    public static Fragment getInstance() {
        if (instance == null)
            instance = new CommentsFragment();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Comments");
        if (getArguments() != null) {
            postId = getArguments().getString("postId");
            publisherId = getArguments().getString("publisherId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        userUid = firebaseAuth.getUid();

        addComment = view.findViewById(R.id.add_comment);
        image_profile = view.findViewById(R.id.image_profile);
        TextView post = view.findViewById(R.id.post);
        RecyclerView recyclerView = view.findViewById(R.id.commentsRecyclerView);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(getContext(), commentList);
        recyclerView.setAdapter(commentAdapter);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addComment.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "You can't send empty comment", Toast.LENGTH_SHORT).show();
                } else {
                    addComment();
                }
            }
        });
        getImage();
        readComments();
        return view;
    }

    private void addComment() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", addComment.getText().toString());
        hashMap.put("publisher", userUid);

        databaseReference.child(DB_COMMENTS).child(postId).push().setValue(hashMap);
        addComment.setText("");
    }

    private void getImage() {
        databaseReference.child(DB_USERS).child(userUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(getContext()).load(user.getProfileImage()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readComments() {
        databaseReference.child(DB_COMMENTS).child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}